package com.github.victormpcmun.c43spanishbankprocessor.layout;

/**
 * A field of a C43 record: its name and where it is in the line.
 *
 * @param start first position of the field, counting from 1 as the C43 specification does
 */
public record FieldDefinition(String name, int start, int length, FieldType type) {

    public int end() {
        return start + length - 1;
    }

    /** The characters of this field in the line; positions beyond a short line read as blanks. */
    public String extractFrom(String line) {
        String padded = line.length() >= end() ? line : line + " ".repeat(end() - line.length());
        return padded.substring(start - 1, end());
    }
}
