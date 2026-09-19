#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#ifdef _WIN32
#include <windows.h>
#include <io.h>
#include <fcntl.h>
#include <direct.h>
#define PATH_SEP '\\'
#define PATH_SEP_STR "\\"
#define EXE_EXT ".exe"
#define DLL_EXT ".dll"
#define PLATFORM_DIR "win32-x64"
#define TOPIARY_NAME "topiary.exe"
#define GRAMMAR_NAME "tree-sitter-pnuts.dll"
#else
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>
#define PATH_SEP '/'
#define PATH_SEP_STR "/"
#define EXE_EXT ""
#ifdef __APPLE__
#include <mach-o/dyld.h>
#define DLL_EXT ".dylib"
#if defined(__arm64__) || defined(__aarch64__)
#define PLATFORM_DIR "darwin-arm64"
#else
#define PLATFORM_DIR "darwin-x64"
#endif
#define TOPIARY_NAME "topiary"
#define GRAMMAR_NAME "libtree-sitter-pnuts.dylib"
#else
#define DLL_EXT ".so"
#define PLATFORM_DIR "linux-x64"
#define TOPIARY_NAME "topiary"
#define GRAMMAR_NAME "libtree-sitter-pnuts.so"
#endif
#endif

typedef struct {
    char *data;
    size_t len;
    size_t cap;
} Buffer;

static void buf_init(Buffer *b) {
    b->cap = 1024;
    b->len = 0;
    b->data = (char *)malloc(b->cap);
    if (b->data) b->data[0] = '\0';
}

static void buf_append(Buffer *b, const char *s, size_t n) {
    if (!s || n == 0) return;
    if (b->len + n + 1 > b->cap) {
        while (b->len + n + 1 > b->cap) b->cap *= 2;
        b->data = (char *)realloc(b->data, b->cap);
    }
    memcpy(b->data + b->len, s, n);
    b->len += n;
    b->data[b->len] = '\0';
}

static void buf_free(Buffer *b) {
    if (b->data) free(b->data);
    b->data = NULL;
    b->len = b->cap = 0;
}

static char *read_all(FILE *f, size_t *out_len) {
    Buffer b;
    buf_init(&b);
    char chunk[4096];
    size_t n;
    while ((n = fread(chunk, 1, sizeof(chunk), f)) > 0) {
        buf_append(&b, chunk, n);
    }
    if (out_len) *out_len = b.len;
    return b.data;
}

static void get_self_dir(char *buffer, size_t max_len) {
#ifdef _WIN32
    GetModuleFileNameA(NULL, buffer, (DWORD)max_len);
    char *last_slash = strrchr(buffer, '\\');
    if (last_slash) *last_slash = '\0';
#elif defined(__APPLE__)
    uint32_t size = (uint32_t)max_len;
    if (_NSGetExecutablePath(buffer, &size) == 0) {
        char *last_slash = strrchr(buffer, '/');
        if (last_slash) *last_slash = '\0';
    }
#else
    ssize_t len = readlink("/proc/self/exe", buffer, max_len - 1);
    if (len != -1) {
        buffer[len] = '\0';
        char *last_slash = strrchr(buffer, '/');
        if (last_slash) *last_slash = '\0';
    }
#endif
}

static int file_exists(const char *path) {
#ifdef _WIN32
    DWORD attr = GetFileAttributesA(path);
    return (attr != INVALID_FILE_ATTRIBUTES && !(attr & FILE_ATTRIBUTE_DIRECTORY));
#else
    struct stat st;
    return (stat(path, &st) == 0 && S_ISREG(st.st_mode));
#endif
}

static void normalize_slashes(char *path) {
    for (size_t i = 0; path[i] != '\0'; i++) {
        if (path[i] == '\\') path[i] = '/';
    }
}

static int find_comment_start(const char *line) {
    char in_str = 0;
    int escaped = 0;
    size_t len = strlen(line);
    for (size_t i = 0; i < len; i++) {
        char c = line[i];
        if (escaped) {
            escaped = 0;
            continue;
        }
        if (c == '\\' && in_str) {
            escaped = 1;
            continue;
        }
        if (in_str) {
            if (c == in_str) in_str = 0;
            continue;
        }
        if (c == '"' || c == '\'' || c == '`') {
            in_str = c;
            continue;
        }
        if (c == '/' && i + 1 < len) {
            if (line[i + 1] == '/') {
                return (int)i;
            }
            if (line[i + 1] == '*') {
                const char *close_ptr = strstr(line + i + 2, "*/");
                if (close_ptr) {
                    const char *p = close_ptr + 2;
                    while (*p == ' ' || *p == '\t' || *p == '\r' || *p == '\n') p++;
                    if (*p == '\0') {
                        return (int)i;
                    }
                }
            }
        }
    }
    return -1;
}

static void format_comment(const char *comment, char *out, size_t out_cap) {
    if (strncmp(comment, "/*", 2) == 0 || strncmp(comment, "//", 2) != 0) {
        snprintf(out, out_cap, "%s", comment);
        return;
    }
    const char *rest = comment + 2;
    if (*rest == '\0' || *rest == ' ' || *rest == '\t') {
        snprintf(out, out_cap, "%s", comment);
        return;
    }
    size_t rlen = strlen(rest);
    if (rlen >= 3 && strchr("*=-/#~.", rest[0]) != NULL) {
        if (rest[1] == rest[0] && rest[2] == rest[0]) {
            snprintf(out, out_cap, "%s", comment);
            return;
        }
    }
    snprintf(out, out_cap, "// %s", rest);
}

static int is_line_blank(const char *line) {
    if (!line) return 1;
    while (*line) {
        if (*line != ' ' && *line != '\t' && *line != '\r' && *line != '\n') return 0;
        line++;
    }
    return 1;
}

static char *preprocess_comments(const char *text) {
    Buffer out;
    buf_init(&out);

    const char *p = text;
    while (*p) {
        const char *line_start = p;
        while (*p && *p != '\n') p++;
        const char *line_end = p;
        if (*p == '\n') p++;

        const char *eol = "\n";
        size_t raw_len = line_end - line_start;
        if (raw_len > 0 && line_start[raw_len - 1] == '\r') {
            raw_len--;
            eol = "\r\n";
        }

        char *line = (char *)malloc(raw_len + 1);
        memcpy(line, line_start, raw_len);
        line[raw_len] = '\0';

        int idx = find_comment_start(line);
        if (idx == -1) {
            buf_append(&out, line, raw_len);
            buf_append(&out, eol, strlen(eol));
            free(line);
            continue;
        }

        char *code_part = (char *)malloc(idx + 1);
        memcpy(code_part, line, idx);
        code_part[idx] = '\0';

        const char *comment_part = line + idx;
        char formatted_comment[8192];
        format_comment(comment_part, formatted_comment, sizeof(formatted_comment));

        char *code_l = code_part;
        while (*code_l == ' ' || *code_l == '\t') code_l++;
        size_t indent_len = code_l - code_part;

        char *code_r = code_part + idx;
        while (code_r > code_l && (code_r[-1] == ' ' || code_r[-1] == '\t')) code_r--;
        *code_r = '\0';

        if (*code_l == '\0') {
            buf_append(&out, code_part, indent_len);
            buf_append(&out, formatted_comment, strlen(formatted_comment));
            buf_append(&out, eol, strlen(eol));
        } else {
            int keep_same_line = 0;
            if (code_l[0] == '}' ||
                strncmp(code_l, "else", 4) == 0 ||
                strncmp(code_l, "catch", 5) == 0 ||
                strncmp(code_l, "finally", 7) == 0) {
                keep_same_line = 1;
            }

            if (keep_same_line) {
                buf_append(&out, code_part, code_r - code_part);
                buf_append(&out, " ", 1);
                buf_append(&out, formatted_comment, strlen(formatted_comment));
                buf_append(&out, eol, strlen(eol));
            } else {
                buf_append(&out, code_part, indent_len);
                buf_append(&out, formatted_comment, strlen(formatted_comment));
                buf_append(&out, eol, strlen(eol));

                buf_append(&out, code_part, code_r - code_part);
                buf_append(&out, eol, strlen(eol));
            }
        }

        free(code_part);
        free(line);
    }

    return out.data;
}

typedef struct {
    char **lines;
    size_t count;
    size_t cap;
} LineList;

static void ll_init(LineList *ll) {
    ll->cap = 128;
    ll->count = 0;
    ll->lines = (char **)malloc(sizeof(char *) * ll->cap);
}

static void ll_push(LineList *ll, const char *str) {
    if (ll->count + 1 > ll->cap) {
        ll->cap *= 2;
        ll->lines = (char **)realloc(ll->lines, sizeof(char *) * ll->cap);
    }
    ll->lines[ll->count++] = strdup(str);
}

static void ll_free(LineList *ll) {
    for (size_t i = 0; i < ll->count; i++) {
        free(ll->lines[i]);
    }
    free(ll->lines);
    ll->lines = NULL;
    ll->count = ll->cap = 0;
}

static void split_lines(const char *text, LineList *ll, int *has_crlf) {
    *has_crlf = 0;
    const char *p = text;
    while (*p) {
        const char *line_start = p;
        while (*p && *p != '\n') p++;
        const char *line_end = p;
        if (*p == '\n') p++;

        size_t raw_len = line_end - line_start;
        if (raw_len > 0 && line_start[raw_len - 1] == '\r') {
            raw_len--;
            *has_crlf = 1;
        }

        char *line = (char *)malloc(raw_len + 1);
        memcpy(line, line_start, raw_len);
        line[raw_len] = '\0';
        ll_push(ll, line);
        free(line);
    }
}

static char *normalize_flush(const LineList *in_ll, int has_crlf) {
    Buffer out;
    buf_init(&out);
    LineList out_ll;
    ll_init(&out_ll);

    for (size_t i = 0; i < in_ll->count; i++) {
        const char *line = in_ll->lines[i];

        const char *p = line;
        while (*p == ' ' || *p == '\t') p++;
        int is_flush = 0;
        if (strncmp(p, "flush", 5) == 0) {
            const char *q = p + 5;
            while (*q == ' ' || *q == '\t') q++;
            if (*q == '(') {
                const char *r = strchr(q, ')');
                if (r) {
                    r++;
                    while (*r == ' ' || *r == '\t') r++;
                    if (*r == ';') r++;
                    while (*r == ' ' || *r == '\t') r++;
                    if (*r == '\0') is_flush = 1;
                }
            }
        }

        if (is_flush && out_ll.count > 0) {
            int64_t prev_idx = (int64_t)out_ll.count - 1;
            while (prev_idx >= 0 && is_line_blank(out_ll.lines[prev_idx])) {
                prev_idx--;
            }

            if (prev_idx >= 0) {
                const char *prev = out_ll.lines[prev_idx];
                const char *prev_p = prev;
                while (*prev_p == ' ' || *prev_p == '\t') prev_p++;
                size_t prev_len = strlen(prev);
                char prev_last = (prev_len > 0) ? prev[prev_len - 1] : '\0';

                int is_comment = (strncmp(prev_p, "//", 2) == 0 || strncmp(prev_p, "/*", 2) == 0 ||
                                  (prev_len >= 2 && prev[prev_len - 2] == '*' && prev[prev_len - 1] == '/'));
                int is_boundary = (prev_last == '{' || prev_last == '}' || prev_last == ':');
                int is_jump = (strncmp(prev_p, "continue", 8) == 0 ||
                               strncmp(prev_p, "break", 5) == 0 ||
                               strncmp(prev_p, "return", 6) == 0 ||
                               strncmp(prev_p, "throw", 5) == 0);

                if (!is_comment && !is_boundary && !is_jump && *prev_p != '\0') {
                    char merged[8192];
                    snprintf(merged, sizeof(merged), "%s%s %s%s",
                             prev,
                             (prev_last != ';') ? ";" : "",
                             p,
                             (p[strlen(p) - 1] != ';') ? ";" : "");
                    free(out_ll.lines[prev_idx]);
                    out_ll.lines[prev_idx] = strdup(merged);
                    continue;
                }
            }
        }

        ll_push(&out_ll, line);
    }

    const char *eol = has_crlf ? "\r\n" : "\n";
    for (size_t i = 0; i < out_ll.count; i++) {
        buf_append(&out, out_ll.lines[i], strlen(out_ll.lines[i]));
        buf_append(&out, eol, strlen(eol));
    }

    ll_free(&out_ll);
    return out.data;
}

static char *normalize_block_spacing(const char *text) {
    if (!text || *text == '\0') return strdup("");

    int has_crlf = 0;
    LineList ll;
    ll_init(&ll);
    split_lines(text, &ll, &has_crlf);

    size_t stack_cap = 256;
    size_t stack_len = 0;
    size_t *stack = (size_t *)malloc(sizeof(size_t) * stack_cap);

    typedef struct {
        size_t open_line;
        size_t close_line;
    } BlockPair;
    size_t blocks_cap = 256;
    size_t blocks_len = 0;
    BlockPair *blocks = (BlockPair *)malloc(sizeof(BlockPair) * blocks_cap);

    int in_block_comment = 0;
    char in_str = 0;
    int escaped = 0;

    for (size_t line_idx = 0; line_idx < ll.count; line_idx++) {
        const char *line = ll.lines[line_idx];
        size_t n = strlen(line);
        size_t i = 0;

        while (i < n) {
            char c = line[i];

            if (in_block_comment) {
                if (c == '*' && i + 1 < n && line[i + 1] == '/') {
                    in_block_comment = 0;
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }

            if (in_str) {
                if (escaped) {
                    escaped = 0;
                    i++;
                    continue;
                }
                if (c == '\\' && in_str) {
                    escaped = 1;
                    i++;
                    continue;
                }
                if (c == in_str) {
                    in_str = 0;
                    i++;
                    continue;
                }
                i++;
                continue;
            }

            if (c == '"' || c == '\'' || c == '`') {
                in_str = c;
                i++;
                continue;
            }

            if (c == '/' && i + 1 < n) {
                if (line[i + 1] == '/') break;
                if (line[i + 1] == '*') {
                    in_block_comment = 1;
                    i += 2;
                    continue;
                }
            }

            if (c == '{') {
                if (stack_len + 1 > stack_cap) {
                    stack_cap *= 2;
                    stack = (size_t *)realloc(stack, sizeof(size_t) * stack_cap);
                }
                stack[stack_len++] = line_idx;
                i++;
                continue;
            }

            if (c == '}') {
                if (stack_len > 0) {
                    size_t open_line = stack[--stack_len];
                    if (blocks_len + 1 > blocks_cap) {
                        blocks_cap *= 2;
                        blocks = (BlockPair *)realloc(blocks, sizeof(BlockPair) * blocks_cap);
                    }
                    blocks[blocks_len].open_line = open_line;
                    blocks[blocks_len].close_line = line_idx;
                    blocks_len++;
                }
                i++;
                continue;
            }

            i++;
        }
    }

    char *lines_to_remove = (char *)calloc(ll.count, sizeof(char));
    char *need_blank = (char *)calloc(ll.count, sizeof(char));

    for (size_t b = 0; b < blocks_len; b++) {
        size_t open_line = blocks[b].open_line;
        size_t close_line = blocks[b].close_line;

        int64_t k = (int64_t)close_line - 1;
        while (k > (int64_t)open_line && is_line_blank(ll.lines[k])) {
            lines_to_remove[k] = 1;
            k--;
        }

        if (close_line > open_line + 1) {
            size_t non_blank = 0;
            for (size_t idx = open_line + 1; idx < close_line; idx++) {
                if (!lines_to_remove[idx] && !is_line_blank(ll.lines[idx])) {
                    non_blank++;
                }
            }
            if (non_blank > 1) {
                if (!is_line_blank(ll.lines[open_line + 1])) {
                    need_blank[open_line] = 1;
                }
            }
        }
    }

    Buffer out;
    buf_init(&out);
    const char *eol = has_crlf ? "\r\n" : "\n";

    for (size_t line_idx = 0; line_idx < ll.count; line_idx++) {
        if (lines_to_remove[line_idx]) {
            continue;
        }
        buf_append(&out, ll.lines[line_idx], strlen(ll.lines[line_idx]));
        buf_append(&out, eol, strlen(eol));
        if (need_blank[line_idx]) {
            buf_append(&out, eol, strlen(eol));
        }
    }

    free(lines_to_remove);
    free(need_blank);
    free(stack);
    free(blocks);
    ll_free(&ll);

    return out.data;
}

#ifdef _WIN32
static char *run_topiary(const char *topiary_bin, const char *ncl_path, const char *query_file, const char *query_dir, const char *input_text, int *exit_code) {
    char temp_dir[MAX_PATH];
    char temp_file[MAX_PATH];
    if (GetTempPathA(sizeof(temp_dir), temp_dir) == 0) {
        strncpy(temp_dir, ".", sizeof(temp_dir) - 1);
    }
    if (GetTempFileNameA(temp_dir, "pnt", 0, temp_file) == 0) {
        *exit_code = 1;
        return NULL;
    }

    FILE *tf = fopen(temp_file, "wb");
    if (!tf) {
        *exit_code = 1;
        return NULL;
    }
    fwrite(input_text, 1, strlen(input_text), tf);
    fclose(tf);

    SetEnvironmentVariableA("TOPIARY_LANGUAGE_DIR", query_dir);

    SECURITY_ATTRIBUTES saAttr;
    saAttr.nLength = sizeof(SECURITY_ATTRIBUTES);
    saAttr.bInheritHandle = TRUE;
    saAttr.lpSecurityDescriptor = NULL;

    HANDLE hInFile = CreateFileA(temp_file, GENERIC_READ, FILE_SHARE_READ, &saAttr, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    if (hInFile == INVALID_HANDLE_VALUE) {
        DeleteFileA(temp_file);
        *exit_code = 1;
        return NULL;
    }

    HANDLE hChildStd_OUT_Rd = NULL;
    HANDLE hChildStd_OUT_Wr = NULL;
    if (!CreatePipe(&hChildStd_OUT_Rd, &hChildStd_OUT_Wr, &saAttr, 0)) {
        CloseHandle(hInFile);
        DeleteFileA(temp_file);
        *exit_code = 1;
        return NULL;
    }
    SetHandleInformation(hChildStd_OUT_Rd, HANDLE_FLAG_INHERIT, 0);

    STARTUPINFOA si;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    si.hStdInput = hInFile;
    si.hStdOutput = hChildStd_OUT_Wr;
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);
    si.dwFlags |= STARTF_USESTDHANDLES;

    PROCESS_INFORMATION pi;
    ZeroMemory(&pi, sizeof(pi));

    char cmd[4096];
    snprintf(cmd, sizeof(cmd), "\"%s\" format -C \"%s\" -q \"%s\" -l pnuts", topiary_bin, ncl_path, query_file);

    if (!CreateProcessA(NULL, cmd, NULL, NULL, TRUE, 0, NULL, NULL, &si, &pi)) {
        CloseHandle(hInFile);
        CloseHandle(hChildStd_OUT_Wr);
        CloseHandle(hChildStd_OUT_Rd);
        DeleteFileA(temp_file);
        *exit_code = 1;
        return NULL;
    }

    CloseHandle(hChildStd_OUT_Wr);
    CloseHandle(hInFile);

    Buffer out_buf;
    buf_init(&out_buf);
    char buf[4096];
    DWORD bytes_read = 0;
    while (ReadFile(hChildStd_OUT_Rd, buf, sizeof(buf), &bytes_read, NULL) && bytes_read > 0) {
        buf_append(&out_buf, buf, bytes_read);
    }
    CloseHandle(hChildStd_OUT_Rd);

    WaitForSingleObject(pi.hProcess, INFINITE);
    DWORD code = 0;
    GetExitCodeProcess(pi.hProcess, &code);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    DeleteFileA(temp_file);

    *exit_code = (int)code;
    return out_buf.data;
}
#else
static char *run_topiary(const char *topiary_bin, const char *ncl_path, const char *query_file, const char *query_dir, const char *input_text, int *exit_code) {
    char temp_file[512];
    const char *tmp = getenv("TMPDIR");
    if (!tmp) tmp = "/tmp";
    snprintf(temp_file, sizeof(temp_file), "%s/pnt_XXXXXX", tmp);
    int fd = mkstemp(temp_file);
    if (fd == -1) {
        *exit_code = 1;
        return NULL;
    }
    write(fd, input_text, strlen(input_text));
    close(fd);

    setenv("TOPIARY_LANGUAGE_DIR", query_dir, 1);
    chmod(topiary_bin, 0755);

    int out_pipe[2];
    if (pipe(out_pipe) != 0) {
        unlink(temp_file);
        *exit_code = 1;
        return NULL;
    }

    pid_t pid = fork();
    if (pid == 0) {
        int in_fd = open(temp_file, O_RDONLY);
        if (in_fd != -1) {
            dup2(in_fd, STDIN_FILENO);
            close(in_fd);
        }
        dup2(out_pipe[1], STDOUT_FILENO);
        close(out_pipe[0]);
        close(out_pipe[1]);

        execl(topiary_bin, topiary_bin, "format", "-C", ncl_path, "-q", query_file, "-l", "pnuts", (char *)NULL);
        _exit(127);
    }

    close(out_pipe[1]);

    Buffer out_buf;
    buf_init(&out_buf);
    char buf[4096];
    ssize_t n;
    while ((n = read(out_pipe[0], buf, sizeof(buf))) > 0) {
        buf_append(&out_buf, buf, (size_t)n);
    }
    close(out_pipe[0]);

    int status = 0;
    waitpid(pid, &status, 0);
    unlink(temp_file);

    *exit_code = WIFEXITED(status) ? WEXITSTATUS(status) : 1;
    return out_buf.data;
}
#endif

int main(int argc, char **argv) {
#ifdef _WIN32
    _setmode(_fileno(stdin), _O_BINARY);
    _setmode(_fileno(stdout), _O_BINARY);
#endif

    const char *target_file = NULL;
    int force_stdin = 0;
    int write_in_place = 0;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "--stdin") == 0 || strcmp(argv[i], "-s") == 0) {
            force_stdin = 1;
        } else if (strcmp(argv[i], "-w") == 0 || strcmp(argv[i], "--write") == 0) {
            write_in_place = 1;
        } else if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            fprintf(stdout, "Usage: pnuts-format [--stdin] [-w|--write] [file.pnuts]\n");
            return 0;
        } else if (strcmp(argv[i], "-v") == 0 || strcmp(argv[i], "--version") == 0) {
            fprintf(stdout, "pnuts-format 0.2.4\n");
            return 0;
        } else if (argv[i][0] != '-') {
            target_file = argv[i];
        }
    }

    char self_dir[1024] = {0};
    get_self_dir(self_dir, sizeof(self_dir));

    char topiary_bin[1024] = {0};
    char grammar_dll[1024] = {0};
    char query_file[1024] = {0};
    char query_dir[1024] = {0};

    // Candidate 0: self is in resources/bin/<platform>/ (e.g. inside vscode-pnuts extension bundle)
    snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR TOPIARY_NAME, self_dir);
    snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR ".." PATH_SEP_STR ".." PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
    snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR ".." PATH_SEP_STR ".." PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
    snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR ".." PATH_SEP_STR ".." PATH_SEP_STR "queries", self_dir);

    // Candidate 1: relative to self_dir (e.g. self is in bin/ next to resources/)
    if (!file_exists(topiary_bin) || !file_exists(grammar_dll)) {
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, self_dir);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "queries", self_dir);
    }

    // Candidate 2: self is in zed-pnuts/ (root of extension)
    if (!file_exists(topiary_bin)) {
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, self_dir);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "queries", self_dir);
    }

    // Candidate 3: self is in scripts/
    if (!file_exists(topiary_bin)) {
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR ".." PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, self_dir);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR ".." PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR ".." PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR ".." PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries", self_dir);
    }

    // Candidate 4: self is in repo root
    if (!file_exists(topiary_bin)) {
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, self_dir);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries", self_dir);
    }

    // Candidate 6: environment variable PNUTS_TOOLING_DIR
    const char *env_tooling = getenv("PNUTS_TOOLING_DIR");
    if (!file_exists(topiary_bin) && env_tooling && *env_tooling) {
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, env_tooling);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, env_tooling);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", env_tooling);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR "zed-pnuts" PATH_SEP_STR "resources" PATH_SEP_STR "queries", env_tooling);
    }

    // Candidate 7: standard user personal project fallback
    if (!file_exists(topiary_bin)) {
        const char *known_root = "/home/chsilva/personal/projects/pnuts-tooling/zed-pnuts";
        snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, known_root);
        snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, known_root);
        snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", known_root);
        snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR "resources" PATH_SEP_STR "queries", known_root);
    }

    if (!file_exists(topiary_bin)) {
        fprintf(stderr, "pnuts-format: Topiary binary not found (looked in: %s)\n", topiary_bin);
        return 1;
    }
    if (!file_exists(grammar_dll)) {
        fprintf(stderr, "pnuts-format: Grammar library not found (looked in: %s)\n", grammar_dll);
        return 1;
    }
    if (!file_exists(query_file)) {
        fprintf(stderr, "pnuts-format: Query file not found (looked in: %s)\n", query_file);
        return 1;
    }

    // Prepare temporary languages.ncl
    char temp_dir[512] = {0};
#ifdef _WIN32
    GetTempPathA(sizeof(temp_dir), temp_dir);
    size_t tlen = strlen(temp_dir);
    if (tlen > 0 && (temp_dir[tlen - 1] == '/' || temp_dir[tlen - 1] == '\\')) {
        temp_dir[tlen - 1] = '\0';
    }
#else
    const char *tmp = getenv("TMPDIR");
    if (!tmp) tmp = "/tmp";
    snprintf(temp_dir, sizeof(temp_dir), "%s", tmp);
#endif

    char ncl_path[1024] = {0};
    snprintf(ncl_path, sizeof(ncl_path), "%s" PATH_SEP_STR "pnuts-topiary-languages.ncl", temp_dir);

    char esc_grammar[1024] = {0};
    strncpy(esc_grammar, grammar_dll, sizeof(esc_grammar) - 1);
    normalize_slashes(esc_grammar);

    FILE *ncl_file = fopen(ncl_path, "wb");
    if (ncl_file) {
        fprintf(ncl_file, "{\n  languages = {\n    pnuts = {\n      extensions = [\"pnuts\", \"pnut\"],\n      grammar = {\n        source = {\n          path = \"%s\",\n        },\n        symbol = \"tree_sitter_pnuts\",\n      },\n      indent = \"  \",\n    },\n  },\n}\n", esc_grammar);
        fclose(ncl_file);
    }

    size_t raw_len = 0;
    char *raw_input = NULL;

    if (target_file && !force_stdin) {
        FILE *tf = fopen(target_file, "rb");
        if (!tf) {
            fprintf(stderr, "pnuts-format: Cannot open file '%s'\n", target_file);
            return 1;
        }
        raw_input = read_all(tf, &raw_len);
        fclose(tf);
    } else {
        raw_input = read_all(stdin, &raw_len);
    }

    if (!raw_input || raw_len == 0) {
        if (raw_input) free(raw_input);
        return 0;
    }

    char *pre = preprocess_comments(raw_input);
    free(raw_input);

    int exit_code = 0;
    char *topiary_out = run_topiary(topiary_bin, ncl_path, query_file, query_dir, pre, &exit_code);
    free(pre);

    if (exit_code != 0 || !topiary_out) {
        if (topiary_out) {
            fputs(topiary_out, stderr);
            free(topiary_out);
        }
        return exit_code ? exit_code : 1;
    }

    int has_crlf = 0;
    LineList ll;
    ll_init(&ll);
    split_lines(topiary_out, &ll, &has_crlf);
    free(topiary_out);

    char *flushed = normalize_flush(&ll, has_crlf);
    ll_free(&ll);

    char *final_result = normalize_block_spacing(flushed);
    free(flushed);

    if (target_file && write_in_place && !force_stdin) {
        FILE *wf = fopen(target_file, "wb");
        if (!wf) {
            fprintf(stderr, "pnuts-format: Failed to write to '%s'\n", target_file);
            free(final_result);
            return 1;
        }
        fwrite(final_result, 1, strlen(final_result), wf);
        fclose(wf);
    } else {
        fputs(final_result, stdout);
        fflush(stdout);
    }

    free(final_result);
    return 0;
}
