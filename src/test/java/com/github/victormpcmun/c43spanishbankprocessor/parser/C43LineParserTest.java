package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class C43LineParserTest {

    private static final String MOVEMENT =
            "22    0418260903260903120041000000000045990000000000000000001234CAFETERIA       ";

    private final C43LineParser parser = new C43LineParser();

    @Test
    void splitsAMovementIntoItsFields() {
        ParsedRecord record = parser.parse(2, MOVEMENT);

        Map<String, String> values = record.fields().stream()
                .collect(Collectors.toMap(ParsedField::name, ParsedField::value));

        assertEquals(Optional.of(RecordType.MOVEMENT), record.type());
        assertEquals("0418", values.get("Clave de oficina origen"));
        assertEquals("260903", values.get("Fecha de operación"));
        assertEquals("12", values.get("Concepto común"));
        assertEquals("1", values.get("Clave debe o haber"));
        assertEquals("00000000004599", values.get("Importe"));
        assertEquals("000000001234", values.get("Referencia 1"));
        assertEquals("CAFETERIA", values.get("Referencia 2"));
        assertTrue(record.hasExpectedLength());
    }

    @Test
    void blankFieldsAreNotInformed() {
        String movementWithoutReference2 = MOVEMENT.substring(0, 64) + " ".repeat(16);

        ParsedRecord record = parser.parse(1, movementWithoutReference2);

        assertTrue(record.informedFields().stream().noneMatch(field -> field.name().equals("Referencia 2")));
        assertEquals(record.fields().size() - 1, record.informedFields().size());
    }

    @Test
    void anUnknownLineHasNoTypeAndNoFields() {
        ParsedRecord record = parser.parse(5, "XX something that is not C43");

        assertTrue(record.type().isEmpty());
        assertTrue(record.fields().isEmpty());
    }

    @Test
    void aShortLineIsStillParsed() {
        ParsedRecord record = parser.parse(1, MOVEMENT.substring(0, 42));

        assertFalse(record.hasExpectedLength());
        assertEquals("00000000004599", record.fields().stream()
                .filter(field -> field.name().equals("Importe")).findFirst().orElseThrow().value());
    }
}
