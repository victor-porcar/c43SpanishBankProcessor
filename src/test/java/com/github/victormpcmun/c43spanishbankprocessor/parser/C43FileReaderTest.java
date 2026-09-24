package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;
import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.ACCOUNT_FOOTER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.ACCOUNT_HEADER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.CONCEPT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.CURRENCY_EQUIVALENCE;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.FILE_END;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType.MOVEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class C43FileReaderTest {

    private final C43FileReader reader = new C43FileReader(new C43LineParser());

    @Test
    void readsEveryLineOfTheSampleFile() {
        List<ParsedRecord> records = reader.read(sample());

        List<RecordType> types = records.stream().map(record -> record.type().orElseThrow()).toList();
        assertEquals(List.of(ACCOUNT_HEADER, MOVEMENT, CONCEPT, MOVEMENT, CONCEPT, CURRENCY_EQUIVALENCE,
                ACCOUNT_FOOTER, FILE_END), types);
        assertTrue(records.stream().allMatch(ParsedRecord::hasExpectedLength));
    }

    @Test
    void keepsAccentsAndEnye() {
        ParsedRecord concept = reader.read(sample()).get(2);

        assertEquals(Optional.of("COMPRA EN CAFETERÍA LA ESPAÑOLA"), concept.fields().stream()
                .filter(field -> field.name().equals("Concepto 1")).map(ParsedField::value).findFirst());
    }

    @Test
    void severalFilesAreReadAsIfTheyWereJoinedTogether() {
        List<ParsedRecord> records = reader.read(List.of(sample(), sample()));

        List<ParsedRecord> single = reader.read(sample());
        assertEquals(single.size() * 2, records.size());
        assertEquals(1, records.get(0).lineNumber());
        assertEquals(9, records.get(single.size()).lineNumber());
        assertEquals(2, records.stream().filter(record -> record.type().orElseThrow() == ACCOUNT_HEADER).count());
        assertEquals(4, records.stream().filter(record -> record.type().orElseThrow() == MOVEMENT).count());
    }

    @Test
    void aMissingFileIsReported() {
        assertThrows(C43Exception.class, () -> reader.read(Path.of("does-not-exist.c43")));
    }

    static Path sample() {
        try {
            return Path.of(C43FileReaderTest.class.getClassLoader().getResource("sample.c43").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
