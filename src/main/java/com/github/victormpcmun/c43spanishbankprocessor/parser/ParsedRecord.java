package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;

import java.util.List;
import java.util.Optional;

/**
 * A line of the file split into its fields. The type is empty when the line does not start
 * with a known record code, and then there are no fields.
 */
public record ParsedRecord(int lineNumber, String line, Optional<RecordType> type, List<ParsedField> fields) {

    public ParsedRecord {
        fields = List.copyOf(fields);
    }

    public boolean hasExpectedLength() {
        return line.length() == RecordType.LINE_LENGTH;
    }

    public List<ParsedField> informedFields() {
        return fields.stream().filter(ParsedField::isInformed).toList();
    }
}
