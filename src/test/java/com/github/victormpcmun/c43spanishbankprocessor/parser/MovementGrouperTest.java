package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.ACCOUNT_FOOTER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.ACCOUNT_HEADER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.CONCEPT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.CURRENCY_EQUIVALENCE;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.FILE_END;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.MOVEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MovementGrouperTest {

    private final C43LineParser parser = new C43LineParser();
    private final MovementGrouper grouper = new MovementGrouper();

    @Test
    void joinsEveryMovementWithTheComplementsBelowIt() {
        List<RecordGroup> groups = grouper.group(new C43FileReader(parser).read(C43FileReaderTest.sample()));

        assertEquals(List.of(
                List.of(ACCOUNT_HEADER),
                List.of(MOVEMENT, CONCEPT),
                List.of(MOVEMENT, CONCEPT, CURRENCY_EQUIVALENCE),
                List.of(ACCOUNT_FOOTER),
                List.of(FILE_END)), typesOf(groups));
    }

    @Test
    void aMovementCanHaveSeveralConcepts() {
        List<RecordGroup> groups = grouper.group(records("22", "23", "23", "23", "22"));

        assertEquals(List.of(List.of(MOVEMENT, CONCEPT, CONCEPT, CONCEPT), List.of(MOVEMENT)), typesOf(groups));
    }

    @Test
    void aComplementWithNoMovementAboveStaysAlone() {
        List<RecordGroup> groups = grouper.group(records("11", "23", "22", "33", "24"));

        assertEquals(List.of(List.of(ACCOUNT_HEADER), List.of(CONCEPT), List.of(MOVEMENT),
                List.of(ACCOUNT_FOOTER), List.of(CURRENCY_EQUIVALENCE)), typesOf(groups));
    }

    @Test
    void anUnknownLineBreaksTheMovement() {
        List<RecordGroup> groups = grouper.group(records("22", "XX", "23"));

        assertEquals(3, groups.size());
    }

    private List<ParsedRecord> records(String... codes) {
        return IntStream.range(0, codes.length)
                .mapToObj(index -> parser.parse(index + 1, codes[index] + " ".repeat(78)))
                .toList();
    }

    private static List<List<RecordType>> typesOf(List<RecordGroup> groups) {
        return groups.stream()
                .map(group -> group.records().stream().map(record -> record.type().orElseThrow()).toList())
                .toList();
    }
}
