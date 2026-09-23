package com.github.victormpcmun.c43spanishbankprocessor.parser;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.AMOUNT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.COMMON_CONCEPT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.CURRENCY;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.DATE;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.DEBIT_CREDIT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.NUMBER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldInterpreterTest {

    @Test
    void datesAreYearMonthDayOfThe2000s() {
        assertEquals(Optional.of("2026-09-03"), FieldInterpreter.meaningOf(DATE, "260903"));
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(DATE, "261399"));
    }

    @Test
    void amountsHaveTwoImplicitDecimals() {
        assertEquals(Optional.of("45.99"), FieldInterpreter.meaningOf(AMOUNT, "00000000004599"));
        assertEquals(Optional.of("0.00"), FieldInterpreter.meaningOf(AMOUNT, "00000000000000"));
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(AMOUNT, "0000000000X599"));
    }

    @Test
    void debitCreditCurrencyAndCommonConceptAreNamed() {
        assertEquals(Optional.of("debe"), FieldInterpreter.meaningOf(DEBIT_CREDIT, "1"));
        assertEquals(Optional.of("haber"), FieldInterpreter.meaningOf(DEBIT_CREDIT, "2"));
        assertEquals(Optional.of("EUR"), FieldInterpreter.meaningOf(CURRENCY, "978"));
        assertEquals(Optional.of("Tarjetas de crédito - débito"), FieldInterpreter.meaningOf(COMMON_CONCEPT, "12"));
    }

    @Test
    void unknownCodesAndPlainValuesNeedNoExplanation() {
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(DEBIT_CREDIT, "7"));
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(CURRENCY, "000"));
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(TEXT, "CAFETERIA"));
        assertEquals(Optional.empty(), FieldInterpreter.meaningOf(NUMBER, "0418"));
    }
}
