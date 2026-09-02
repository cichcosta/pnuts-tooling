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

static void format_line_comments(const char *src, char *dst, size_t dst_size) {
    size_t i = 0, j = 0;
    char in_str = 0;
    int escaped = 0;
    size_t len = strlen(src);

    while (i < len && j + 2 < dst_size) {
        char c = src[i];

        if (escaped) {
            escaped = 0;
            dst[j++] = c;
            i++;
            continue;
        }

        if (c == '\\' && in_str) {
            escaped = 1;
            dst[j++] = c;
            i++;
            continue;
        }

        if (in_str) {
            if (c == in_str) {
                in_str = 0;
            }
            dst[j++] = c;
            i++;
            continue;
        }

        if (c == '"' || c == '\'' || c == '`') {
            in_str = c;
            dst[j++] = c;
            i++;
            continue;
        }

        // Check for line comment //
        if (c == '/' && i + 1 < len && src[i + 1] == '/') {
            dst[j++] = '/';
            dst[j++] = '/';
            i += 2;

            if (i < len) {
                char next_c = src[i];
                int is_space = (next_c == ' ' || next_c == '\t' || next_c == '\r' || next_c == '\n');
                int is_divider = 0;
                if (next_c == '/' || next_c == '-' || next_c == '=' || next_c == '*' || next_c == '#') {
                    if (i + 2 < len && src[i + 1] == next_c && src[i + 2] == next_c) {
                        is_divider = 1;
                    }
                }

                if (!is_space && !is_divider && j + 1 < dst_size) {
                    dst[j++] = ' ';
                }
            }

            while (i < len && j + 1 < dst_size) {
                dst[j++] = src[i++];
            }
            break;
        }

        dst[j++] = c;
        i++;
    }

    dst[j] = '\0';
}

static void normalize_and_print(FILE *in, FILE *out) {
    char line[8192];
    char prev_line[8192] = {0};
    int has_prev = 0;

    while (fgets(line, sizeof(line), in)) {
        size_t len = strlen(line);
        char eol[3] = "\n";
        if (len > 0 && (line[len - 1] == '\n' || line[len - 1] == '\r')) {
            if (len > 1 && line[len - 2] == '\r') strcpy(eol, "\r\n");
            while (len > 0 && (line[len - 1] == '\n' || line[len - 1] == '\r')) {
                line[--len] = '\0';
            }
        }

        char formatted_line[8192];
        format_line_comments(line, formatted_line, sizeof(formatted_line));
        strncpy(line, formatted_line, sizeof(line) - 1);
        line[sizeof(line) - 1] = '\0';

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

        if (is_flush && has_prev) {
            const char *prev_p = prev_line;
            while (*prev_p == ' ' || *prev_p == '\t') prev_p++;
            int prev_len = (int)strlen(prev_line);
            char prev_last = (prev_len > 0) ? prev_line[prev_len - 1] : '\0';
            int is_comment = (strncmp(prev_p, "//", 2) == 0 || strncmp(prev_p, "/*", 2) == 0 ||
                              (prev_len >= 2 && prev_line[prev_len - 2] == '*' && prev_line[prev_len - 1] == '/'));
            int is_boundary = (prev_last == '{' || prev_last == '}' || prev_last == ':');

            if (!is_comment && !is_boundary && *prev_p != '\0') {
                if (prev_last != ';') {
                    strncat(prev_line, ";", sizeof(prev_line) - strlen(prev_line) - 1);
                }
                strncat(prev_line, " ", sizeof(prev_line) - strlen(prev_line) - 1);
                char flush_cmd[512];
                snprintf(flush_cmd, sizeof(flush_cmd), "%s", p);
                size_t flen = strlen(flush_cmd);
                if (flen > 0 && flush_cmd[flen - 1] != ';') {
                    strncat(flush_cmd, ";", sizeof(flush_cmd) - strlen(flush_cmd) - 1);
                }
                strncat(prev_line, flush_cmd, sizeof(prev_line) - strlen(prev_line) - 1);
                continue;
            }
        }

        if (has_prev) {
            fputs(prev_line, out);
            fputs(eol, out);
        }
        strncpy(prev_line, line, sizeof(prev_line) - 1);
        prev_line[sizeof(prev_line) - 1] = '\0';
        has_prev = 1;
    }

    if (has_prev) {
        fputs(prev_line, out);
        fputc('\n', out);
    }
}

int main(int argc, char **argv) {
#ifdef _WIN32
    _setmode(_fileno(stdin), _O_BINARY);
    _setmode(_fileno(stdout), _O_BINARY);
#endif

    char self_dir[1024] = {0};
    get_self_dir(self_dir, sizeof(self_dir));

    char topiary_bin[1024] = {0};
    char grammar_dll[1024] = {0};
    char query_file[1024] = {0};
    char query_dir[1024] = {0};

    // Candidate 1: relative to self_dir (e.g. self is in zed-pnuts/bin/)
    snprintf(topiary_bin, sizeof(topiary_bin), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "bin" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR TOPIARY_NAME, self_dir);
    snprintf(grammar_dll, sizeof(grammar_dll), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "grammars" PATH_SEP_STR PLATFORM_DIR PATH_SEP_STR GRAMMAR_NAME, self_dir);
    snprintf(query_file, sizeof(query_file), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "queries" PATH_SEP_STR "pnuts.scm", self_dir);
    snprintf(query_dir, sizeof(query_dir), "%s" PATH_SEP_STR ".." PATH_SEP_STR "resources" PATH_SEP_STR "queries", self_dir);

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

    int has_file_arg = 0;
    for (int i = 1; i < argc; i++) {
        if (argv[i][0] != '-') {
            has_file_arg = 1;
            break;
        }
    }

    char cmdline[4096] = {0};
    if (has_file_arg) {
        snprintf(cmdline, sizeof(cmdline), "\"%s\" format -C \"%s\"", topiary_bin, ncl_path);
    } else {
        snprintf(cmdline, sizeof(cmdline), "\"%s\" format -C \"%s\" -q \"%s\" -l pnuts", topiary_bin, ncl_path, query_file);
    }

    for (int i = 1; i < argc; i++) {
        strncat(cmdline, " \"", sizeof(cmdline) - strlen(cmdline) - 1);
        strncat(cmdline, argv[i], sizeof(cmdline) - strlen(cmdline) - 1);
        strncat(cmdline, "\"", sizeof(cmdline) - strlen(cmdline) - 1);
    }

#ifdef _WIN32
    SetEnvironmentVariableA("TOPIARY_LANGUAGE_DIR", query_dir);

    HANDLE hChildStd_OUT_Rd = NULL;
    HANDLE hChildStd_OUT_Wr = NULL;
    SECURITY_ATTRIBUTES saAttr;
    saAttr.nLength = sizeof(SECURITY_ATTRIBUTES);
    saAttr.bInheritHandle = TRUE;
    saAttr.lpSecurityDescriptor = NULL;

    if (!CreatePipe(&hChildStd_OUT_Rd, &hChildStd_OUT_Wr, &saAttr, 0)) {
        fprintf(stderr, "pnuts-format: CreatePipe failed\n");
        return 1;
    }
    SetHandleInformation(hChildStd_OUT_Rd, HANDLE_FLAG_INHERIT, 0);

    STARTUPINFOA si;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);
    si.hStdOutput = hChildStd_OUT_Wr;
    si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
    si.dwFlags |= STARTF_USESTDHANDLES;

    PROCESS_INFORMATION pi;
    ZeroMemory(&pi, sizeof(pi));

    if (!CreateProcessA(NULL, cmdline, NULL, NULL, TRUE, 0, NULL, NULL, &si, &pi)) {
        DWORD err = GetLastError();
        fprintf(stderr, "pnuts-format: CreateProcess failed (error %lu)\n", (unsigned long)err);
        CloseHandle(hChildStd_OUT_Wr);
        CloseHandle(hChildStd_OUT_Rd);
        return 1;
    }

    CloseHandle(hChildStd_OUT_Wr);

    int fd = _open_osfhandle((intptr_t)hChildStd_OUT_Rd, _O_RDONLY | _O_BINARY);
    if (fd != -1) {
        FILE *pipe = _fdopen(fd, "rb");
        if (pipe) {
            normalize_and_print(pipe, stdout);
            fclose(pipe);
        } else {
            _close(fd);
        }
    }

    WaitForSingleObject(pi.hProcess, INFINITE);
    DWORD exit_code = 0;
    GetExitCodeProcess(pi.hProcess, &exit_code);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    return (int)exit_code;
#else
    setenv("TOPIARY_LANGUAGE_DIR", query_dir, 1);
    chmod(topiary_bin, 0755);
    FILE *pipe = popen(cmdline, "r");
    if (!pipe) {
        perror("pnuts-format: popen failed");
        return 1;
    }
    normalize_and_print(pipe, stdout);
    int status = pclose(pipe);
    return WIFEXITED(status) ? WEXITSTATUS(status) : 1;
#endif
}
