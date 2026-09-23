package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes text files in windows-1252, the charset Excel and Notepad expect in Spanish Windows,
 * creating the parent directories when needed and replacing whatever was there before.
 */
final class TextFileWriter {

    static final Charset CHARSET = Charset.forName("windows-1252");

    private TextFileWriter() {
    }

    static void write(Path file, List<String> lines, String description) {
        try {
            createParentDirectories(file);
            Files.write(file, lines, CHARSET);
        } catch (IOException e) {
            throw new C43Exception("cannot write " + description + " " + file.toAbsolutePath(), e);
        }
    }

    private static void createParentDirectories(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
