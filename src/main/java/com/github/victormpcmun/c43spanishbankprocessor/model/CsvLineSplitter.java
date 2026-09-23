package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a CSV line into its values. Values are separated by ';' and may be written between
 * double quotes when they hold a ';' themselves; inside a quoted value, two double quotes
 * stand for one.
 */
final class CsvLineSplitter {

    private static final char SEPARATOR = ';';
    private static final char QUOTE = '"';

    private CsvLineSplitter() {
    }

    static List<String> split(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (quoted && character == QUOTE && isQuote(line, index + 1)) {
                value.append(QUOTE);
                index++;
            } else if (character == QUOTE) {
                quoted = !quoted;
            } else if (character == SEPARATOR && !quoted) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }
        values.add(value.toString());
        return values;
    }

    private static boolean isQuote(String line, int index) {
        return index < line.length() && line.charAt(index) == QUOTE;
    }
}
