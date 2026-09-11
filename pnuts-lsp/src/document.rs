use std::collections::HashMap;
use std::sync::Arc;
use tokio::sync::RwLock;
use tree_sitter::Tree;
use url::Url;

use crate::parser::{has_errors, parse_pnuts};

/// An in-memory representation of an open document.
#[derive(Debug)]
pub struct Document {
    pub uri: Url,
    pub language_id: String,
    pub version: i32,
    pub text: String,
    pub tree: Option<Tree>,
}

impl Document {
    /// Creates a new document and parses its AST.
    pub fn new(uri: Url, language_id: String, version: i32, text: String) -> Self {
        let tree = parse_pnuts(&text, None);
        Self {
            uri,
            language_id,
            version,
            text,
            tree,
        }
    }

    /// Updates the document text and re-parses its AST.
    pub fn update(&mut self, version: i32, text: String) {
        self.version = version;
        self.tree = parse_pnuts(&text, None);
        self.text = text;
    }

    /// Returns whether the document tree contains syntax errors.
    pub fn has_errors(&self) -> bool {
        self.tree.as_ref().map(has_errors).unwrap_or(false)
    }
}

/// A lightweight snapshot of a document state without holding a lock.
#[derive(Debug, Clone)]
pub struct DocumentSnapshot {
    pub uri: Url,
    pub language_id: String,
    pub version: i32,
    pub text: String,
    pub has_tree: bool,
    pub has_errors: bool,
}

/// Thread-safe in-memory store for open documents.
/// Supports both file:// and untitled: URI schemes.
#[derive(Debug, Default)]
pub struct DocumentStore {
    documents: RwLock<HashMap<Url, Document>>,
}

impl DocumentStore {
    pub fn new() -> Self {
        Self {
            documents: RwLock::new(HashMap::new()),
        }
    }

    /// Opens or replaces a document in memory.
    pub async fn open(&self, uri: Url, language_id: String, version: i32, text: String) {
        let doc = Document::new(uri.clone(), language_id, version, text);
        let mut docs = self.documents.write().await;
        docs.insert(uri, doc);
    }

    /// Updates the text and version of an existing document.
    /// Returns `true` if the document was found and updated, `false` otherwise.
    pub async fn update(&self, uri: &Url, version: i32, text: String) -> bool {
        let mut docs = self.documents.write().await;
        if let Some(doc) = docs.get_mut(uri) {
            doc.update(version, text);
            true
        } else {
            false
        }
    }

    /// Closes and removes a document from memory.
    pub async fn close(&self, uri: &Url) -> Option<Document> {
        let mut docs = self.documents.write().await;
        docs.remove(uri)
    }

    /// Checks if a document is present in the store.
    pub async fn contains(&self, uri: &Url) -> bool {
        let docs = self.documents.read().await;
        docs.contains_key(uri)
    }

    /// Gets a snapshot of the document.
    pub async fn get_snapshot(&self, uri: &Url) -> Option<DocumentSnapshot> {
        let docs = self.documents.read().await;
        docs.get(uri).map(|doc| DocumentSnapshot {
            uri: doc.uri.clone(),
            language_id: doc.language_id.clone(),
            version: doc.version,
            text: doc.text.clone(),
            has_tree: doc.tree.is_some(),
            has_errors: doc.has_errors(),
        })
    }

    /// Retrieves the full text of a document.
    pub async fn get_text(&self, uri: &Url) -> Option<String> {
        let docs = self.documents.read().await;
        docs.get(uri).map(|doc| doc.text.clone())
    }

    /// Retrieves the version of a document.
    pub async fn get_version(&self, uri: &Url) -> Option<i32> {
        let docs = self.documents.read().await;
        docs.get(uri).map(|doc| doc.version)
    }

    /// Returns the number of open documents.
    pub async fn len(&self) -> usize {
        let docs = self.documents.read().await;
        docs.len()
    }

    /// Returns `true` if no documents are open.
    pub async fn is_empty(&self) -> bool {
        let docs = self.documents.read().await;
        docs.is_empty()
    }

    /// Executes a closure with a reference to the document's parsed Tree-sitter tree.
    pub async fn with_tree<F, R>(&self, uri: &Url, f: F) -> Option<R>
    where
        F: FnOnce(&Tree) -> R,
    {
        let docs = self.documents.read().await;
        let doc = docs.get(uri)?;
        let tree = doc.tree.as_ref()?;
        Some(f(tree))
    }
}

pub type SharedDocumentStore = Arc<DocumentStore>;

#[cfg(test)]
mod tests {
    use super::*;

    #[tokio::test]
    async fn test_document_lifecycle_file_uri() {
        let store = DocumentStore::new();
        let uri = Url::parse("file:///workspace/script.pnuts").unwrap();

        assert_eq!(store.len().await, 0);
        assert!(!store.contains(&uri).await);

        // 1. Open document
        store
            .open(
                uri.clone(),
                "pnuts".to_string(),
                1,
                "x = 10; println(x);".to_string(),
            )
            .await;

        assert_eq!(store.len().await, 1);
        assert!(store.contains(&uri).await);

        let snap = store.get_snapshot(&uri).await.unwrap();
        assert_eq!(snap.version, 1);
        assert_eq!(snap.text, "x = 10; println(x);");
        assert!(snap.has_tree);
        assert!(!snap.has_errors);

        // 2. Update document
        let updated = store
            .update(&uri, 2, "x = 20; println(x + 5);".to_string())
            .await;
        assert!(updated);

        let snap2 = store.get_snapshot(&uri).await.unwrap();
        assert_eq!(snap2.version, 2);
        assert_eq!(snap2.text, "x = 20; println(x + 5);");

        // 3. Close document
        let closed = store.close(&uri).await;
        assert!(closed.is_some());
        assert_eq!(store.len().await, 0);
        assert!(!store.contains(&uri).await);
    }

    #[tokio::test]
    async fn test_untitled_buffer_support() {
        let store = DocumentStore::new();
        // Zed or VSCode untitled buffer scheme
        let uri = Url::parse("untitled:Untitled-1").unwrap();

        store
            .open(
                uri.clone(),
                "pnuts".to_string(),
                1,
                "function test() { return 42; }".to_string(),
            )
            .await;

        assert_eq!(store.len().await, 1);
        assert!(store.contains(&uri).await);

        let text = store.get_text(&uri).await.unwrap();
        assert_eq!(text, "function test() { return 42; }");

        // Check AST tree traversal
        let root_kind = store
            .with_tree(&uri, |tree| tree.root_node().kind().to_string())
            .await;
        assert_eq!(root_kind, Some("source_file".to_string()));
    }
}
