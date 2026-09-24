package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovementDeduplicatorTest {

    private static final MovementLine CAFETERIA = movement("Registro:22 Movimiento - Concepto 1:CAFETERIA");
    private static final MovementLine NOMINA = movement("Registro:22 Movimiento - Concepto 1:NOMINA");

    private final MovementDeduplicator deduplicator = new MovementDeduplicator();

    @Test
    void keepsTheFirstOfTheRepeatedMovementsAndSetsTheOthersApart() {
        MovementLine repeated = movement(CAFETERIA.getPlainLine());

        Deduplication result = deduplicator.removeDuplicates(List.of(CAFETERIA, NOMINA, repeated));

        assertEquals(List.of(CAFETERIA, NOMINA), result.movements());
        assertEquals(List.of(repeated), result.duplicates());
    }

    @Test
    void movementsThatDifferInAnythingAreNotRepeats() {
        MovementLine sameButOneReference = movement(CAFETERIA.getPlainLine() + " - Referencia 1:000000001234");

        Deduplication result = deduplicator.removeDuplicates(List.of(CAFETERIA, sameButOneReference));

        assertEquals(List.of(CAFETERIA, sameButOneReference), result.movements());
        assertEquals(List.of(), result.duplicates());
    }

    @Test
    void aMovementRepeatedSeveralTimesIsKeptOnlyOnce() {
        Deduplication result = deduplicator.removeDuplicates(
                List.of(CAFETERIA, movement(CAFETERIA.getPlainLine()), movement(CAFETERIA.getPlainLine())));

        assertEquals(1, result.movements().size());
        assertEquals(2, result.duplicates().size());
    }

    @Test
    void withoutMovementsThereIsNothingToDo() {
        Deduplication result = deduplicator.removeDuplicates(List.of());

        assertEquals(List.of(), result.movements());
        assertEquals(List.of(), result.duplicates());
    }

    private static MovementLine movement(String plainLine) {
        return new MovementLine("2026", "SEPTIEMBRE", "-45.99", plainLine);
    }
}
