package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementLineMatchPatternTest {

    private static final String PATTERN = "*Hola*test*";

    @ParameterizedTest
    @ValueSource(strings = {"Hola mundotest    kk", "  Hola munditest", "Holatest", "xHolaxtestx"})
    void matchesTheLinesThatFollowThePattern(String plainLine) {
        assertTrue(movement(plainLine).matchPattern(PATTERN));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hola mundotedt    kk", "test y luego Hola", "HOLA test", "hola test", "Hola"})
    void doesNotMatchTheLinesThatDoNotFollowIt(String plainLine) {
        assertFalse(movement(plainLine).matchPattern(PATTERN));
    }

    @Test
    void oneMatchingPatternOfTheDefinitionIsEnough() {
        MovementLine movement = movement("Registro:22 Movimiento - Importe:00000000095000 (950.00)");

        assertTrue(movement.matchPattern("*NOMINA*|*Importe:*"));
        assertTrue(movement.matchPattern("*Importe:*|*NOMINA*"));
        assertFalse(movement.matchPattern("*NOMINA*|*RECIBO*"));
    }

    @Test
    void thePatternHasToMatchTheWholeLine() {
        MovementLine movement = movement("Hola mundo");

        assertTrue(movement.matchPattern("Hola*"));
        assertTrue(movement.matchPattern("Hola mundo"));
        assertFalse(movement.matchPattern("Hola"));
        assertFalse(movement.matchPattern("mundo"));
    }

    @Test
    void charactersOfRegularExpressionsAreLiteral() {
        MovementLine movement = movement("Importe:00000000095000 (950.00)");

        assertTrue(movement.matchPattern("*(950.00)"));
        assertFalse(movement.matchPattern("*(950X00)"));
        assertFalse(movement.matchPattern("*\\d+*"));
    }

    @Test
    void aBlankDefinitionMatchesNothing() {
        MovementLine movement = movement("Hola mundotest");

        assertFalse(movement.matchPattern(""));
        assertFalse(movement.matchPattern("   "));
        assertFalse(movement.matchPattern(null));
    }

    @Test
    void blankPatternsOfTheDefinitionAreIgnored() {
        MovementLine movement = movement("Hola mundotest");

        assertTrue(movement.matchPattern("*mundo*||*nada*"));
        assertFalse(movement.matchPattern("*nada*||*tampoco*"));
    }

    @Test
    void aMovementWithoutPlainLineMatchesNothing() {
        assertFalse(new MovementLine().matchPattern("*Hola*"));
    }

    private static MovementLine movement(String plainLine) {
        return new MovementLine("2026", "JULIO", "950.0", plainLine);
    }
}
