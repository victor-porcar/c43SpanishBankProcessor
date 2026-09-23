package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;

import java.util.List;
import java.util.Optional;

/**
 * Splits a line of a C43 file into the fields of its record type.
 */
public class C43LineParser {

    public ParsedRecord parse(int lineNumber, String line) {
        Optional<RecordType> type = RecordType.of(line);
        List<ParsedField> fields = type.map(recordType -> fieldsOf(recordType, line)).orElse(List.of());
        return new ParsedRecord(lineNumber, line, type, fields);
    }

    private List<ParsedField> fieldsOf(RecordType type, String line) {
        return type.fields().stream()
                .map(definition -> new ParsedField(definition, definition.extractFrom(line)))
                .toList();
    }
}
