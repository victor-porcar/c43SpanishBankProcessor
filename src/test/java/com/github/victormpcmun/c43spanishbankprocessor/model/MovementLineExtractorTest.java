package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovementLineExtractorTest {

    private static final String MOVEMENT = "Registro:22 Movimiento - Clave de oficina origen:0418"
            + " - Fecha de operación:260701 (2026-07-01) - Fecha valor:260702 (2026-07-02)"
            + " - Clave debe o haber:2 (haber) - Importe:00000000095000 (950.00)"
            + " - Registro:23 Concepto complementario - Concepto 1:NOMINA";

    private final MovementLineExtractor extractor = new MovementLineExtractor();

    @Test
    void takesYearMonthAndAmountOutOfTheLine() {
        MovementLine movement = extractor.extract(MOVEMENT).orElseThrow();

        assertEquals("2026", movement.getYear());
        assertEquals("JULIO", movement.getMonth());
        assertEquals("950.0", movement.getImporte());
        assertEquals(MOVEMENT, movement.getPlainLine());
    }

    @Test
    void aDebitIsNegativeAndACreditIsPositive() {
        assertEquals("-45.99", extractor.extract(movement("1", "00000000004599 (45.99)")).orElseThrow().getImporte());
        assertEquals("45.99", extractor.extract(movement("2", "00000000004599 (45.99)")).orElseThrow().getImporte());
    }

    @Test
    void aZeroAmountHasNoSign() {
        assertEquals("0.0", extractor.extract(movement("1", "00000000000000 (0.00)")).orElseThrow().getImporte());
    }

    @Test
    void withoutTheDebitCreditKeyTheAmountStaysPositive() {
        assertEquals("950.0", extractor.extract(line("260702", "00000000095000 (950.00)")).orElseThrow().getImporte());
    }

    @ParameterizedTest
    @CsvSource({"260102, 2026, ENERO", "991231, 2099, DICIEMBRE", "000615, 2000, JUNIO", "261331, 2026, 13"})
    void everyMonthIsNamedInSpanish(String valueDate, String year, String month) {
        MovementLine movement = extractor.extract(line(valueDate, "00000000095000 (950.00)")).orElseThrow();

        assertEquals(year, movement.getYear());
        assertEquals(month, movement.getMonth());
    }

    @ParameterizedTest
    @CsvSource({"00000000095000 (950.00), 950.0", "00000000004599 (45.99), 45.99", "00000000000000 (0.00), 0.0"})
    void amountsKeepTheirCentsButNotTheirTrailingZeros(String amount, String importe) {
        assertEquals(importe, extractor.extract(line("260702", amount)).orElseThrow().getImporte());
    }

    @Test
    void theAmountOfTheMovementIsNotTheOneOfTheCurrencyEquivalence() {
        String withEquivalence = MOVEMENT + " - Registro:24 Equivalencia de divisa"
                + " - Importe en divisa origen:00000000005000 (50.00)";

        assertEquals("950.0", extractor.extract(withEquivalence).orElseThrow().getImporte());
    }

    @Test
    void aLineWithoutValueDateOrAmountIsNotAMovement() {
        assertEquals(Optional.empty(), extractor.extract("Registro:11 Cabecera de cuenta - Fecha inicial:260901 (2026-09-01)"));
        assertEquals(Optional.empty(), extractor.extract("Registro:88 Fin de fichero - Número de registros:000007"));
        assertEquals(Optional.empty(), extractor.extract("Fecha valor:260702 (2026-07-02)"));
        assertEquals(Optional.empty(), extractor.extract(""));
    }

    private static String line(String valueDate, String amount) {
        return "Registro:22 Movimiento - Fecha valor:" + valueDate + " - Importe:" + amount;
    }

    /** A movement of July with the given debit/credit key: 1 is a debit, 2 a credit. */
    private static String movement(String debitCredit, String amount) {
        return "Registro:22 Movimiento - Fecha valor:260702 (2026-07-02)"
                + " - Clave debe o haber:" + debitCredit + " (debe) - Importe:" + amount;
    }
}
