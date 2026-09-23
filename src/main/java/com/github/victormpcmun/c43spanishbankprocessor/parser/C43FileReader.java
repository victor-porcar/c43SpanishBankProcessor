package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Reads a C43 file and parses every non-blank line. Banks write these files in ISO-8859-1,
 * which keeps accents and "ñ" readable.
 */
public class C43FileReader {

    private static final Charset C43_CHARSET = StandardCharsets.ISO_8859_1;

    private final C43LineParser lineParser;

    public C43FileReader(C43LineParser lineParser) {
        this.lineParser = lineParser;
    }

    public List<ParsedRecord> read(Path file) {
        List<String> lines = linesOf(file);
        return IntStream.range(0, lines.size())
                .filter(index -> !lines.get(index).isBlank())
                .mapToObj(index -> lineParser.parse(index + 1, lines.get(index)))
                .toList();
    }

    private static List<String> linesOf(Path file) {
        try {
            return Files.readAllLines(file, C43_CHARSET);
        } catch (IOException e) {
            throw new C43Exception("cannot read C43 file " + file.toAbsolutePath(), e);
        }
    }
}
