package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads C43 files and parses every non-blank line. Several files are read one after another as
 * if they were a single one: the records that are not movements (account header, account footer
 * and end of file) simply give no movement, so their headers and footers do no harm.
 * Banks write these files in ISO-8859-1, which keeps accents and "ñ" readable.
 */
public class C43FileReader {

    private static final Charset C43_CHARSET = StandardCharsets.ISO_8859_1;

    private final C43LineParser lineParser;

    public C43FileReader(C43LineParser lineParser) {
        this.lineParser = lineParser;
    }

    public List<ParsedRecord> read(Path file) {
        return read(List.of(file));
    }

    /** Every line of every file, numbered from 1 as if the files were joined together. */
    public List<ParsedRecord> read(List<Path> files) {
        List<ParsedRecord> records = new ArrayList<>();
        int lineNumber = 0;
        for (Path file : files) {
            for (String line : linesOf(file)) {
                lineNumber++;
                if (!line.isBlank()) {
                    records.add(lineParser.parse(lineNumber, line));
                }
            }
        }
        return List.copyOf(records);
    }

    private static List<String> linesOf(Path file) {
        try {
            return Files.readAllLines(file, C43_CHARSET);
        } catch (IOException e) {
            throw new C43Exception("cannot read C43 file " + file.toAbsolutePath(), e);
        }
    }
}
