package com.github.victormpcmun.c43spanishbankprocessor.printer;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;
import com.github.victormpcmun.c43spanishbankprocessor.parser.ParsedField;
import com.github.victormpcmun.c43spanishbankprocessor.parser.ParsedRecord;
import com.github.victormpcmun.c43spanishbankprocessor.parser.RecordGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Writes a group of records (a movement with its complements, or any other record alone) as a
 * single line of "name:value" pairs separated by " - ". Each record contributes its type, every
 * informed field and, when it is not 80 characters long, a warning. A value that needs explaining
 * (a date, an amount...) is followed by its meaning in brackets.
 */
public class RecordLineFormatter {

    private static final String SEPARATOR = " - ";

    public String format(RecordGroup group) {
        List<String> pairs = new ArrayList<>();
        group.records().forEach(record -> pairs.addAll(pairsOf(record)));
        return String.join(SEPARATOR, pairs);
    }

    private List<String> pairsOf(ParsedRecord record) {
        List<String> pairs = new ArrayList<>();
        record.type().ifPresentOrElse(
                type -> pairs.addAll(knownPairs(record, type)),
                () -> pairs.addAll(unknownPairs(record)));
        lengthWarning(record).ifPresent(pairs::add);
        return pairs;
    }

    private List<String> knownPairs(ParsedRecord record, RecordType type) {
        List<String> pairs = new ArrayList<>();
        pairs.add(pair("Registro", type.code() + " " + type.description()));
        record.informedFields().forEach(field -> pairs.add(pairOf(field)));
        return pairs;
    }

    private List<String> unknownPairs(ParsedRecord record) {
        return List.of(pair("Registro", "desconocido"), pair("Contenido", record.line()));
    }

    private Optional<String> lengthWarning(ParsedRecord record) {
        if (record.hasExpectedLength()) {
            return Optional.empty();
        }
        return Optional.of(pair("Aviso",
                record.line().length() + " caracteres, " + RecordType.LINE_LENGTH + " esperados"));
    }

    private static String pairOf(ParsedField field) {
        String meaning = field.meaning().map(text -> " (" + text + ")").orElse("");
        return pair(field.name(), field.value() + meaning);
    }

    private static String pair(String name, String value) {
        return name + ":" + value;
    }
}
