package com.github.victormpcmun.c43spanishbankprocessor.writer;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Joins values into a CSV line separated by ';'. A value holding a ';', a double quote or a line
 * break is written between double quotes, and its own double quotes are doubled, which is how
 * Excel reads them back.
 */
final class CsvLineJoiner {

    private static final String SEPARATOR = ";";
    private static final String QUOTE = "\"";

    private CsvLineJoiner() {
    }

    static String join(String... values) {
        return Arrays.stream(values)
                .map(CsvLineJoiner::quoteIfNeeded)
                .collect(Collectors.joining(SEPARATOR));
    }

    private static String quoteIfNeeded(String value) {
        String text = value == null ? "" : value;
        if (!needsQuotes(text)) {
            return text;
        }
        return QUOTE + text.replace(QUOTE, QUOTE + QUOTE) + QUOTE;
    }

    private static boolean needsQuotes(String value) {
        return value.contains(SEPARATOR) || value.contains(QUOTE) || value.contains("\n") || value.contains("\r");
    }
}
