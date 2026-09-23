package com.github.victormpcmun.c43spanishbankprocessor.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldDefinitionTest {

    private final FieldDefinition field = new FieldDefinition("Test", 3, 4, FieldType.TEXT);

    @Test
    void positionsCountFromOne() {
        assertEquals(6, field.end());
        assertEquals("CDEF", field.extractFrom("ABCDEFGH"));
    }

    @Test
    void aShortLineReadsAsBlanks() {
        assertEquals("CD  ", field.extractFrom("ABCD"));
        assertEquals("    ", field.extractFrom("A"));
    }
}
