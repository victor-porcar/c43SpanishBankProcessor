package com.github.victormpcmun.c43spanishbankprocessor.layout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordTypeTest {

    @ParameterizedTest
    @EnumSource(RecordType.class)
    void fieldsFitInTheLineAfterTheRecordCode(RecordType type) {
        for (FieldDefinition field : type.fields()) {
            assertTrue(field.start() >= 3, field.name() + " overlaps the record code");
            assertTrue(field.end() <= RecordType.LINE_LENGTH, field.name() + " goes beyond position 80");
        }
    }

    @ParameterizedTest
    @EnumSource(RecordType.class)
    void fieldsAreInOrderAndDoNotOverlap(RecordType type) {
        List<FieldDefinition> fields = type.fields();
        for (int i = 1; i < fields.size(); i++) {
            assertTrue(fields.get(i).start() > fields.get(i - 1).end(),
                    fields.get(i).name() + " overlaps " + fields.get(i - 1).name());
        }
    }

    @ParameterizedTest
    @EnumSource(RecordType.class)
    void theRecordCodeTellsTheType(RecordType type) {
        assertEquals(type, RecordType.of(type.code() + "rest of the line").orElseThrow());
    }

    @Test
    void anUnknownCodeHasNoType() {
        assertTrue(RecordType.of("99 whatever").isEmpty());
        assertTrue(RecordType.of("").isEmpty());
    }
}
