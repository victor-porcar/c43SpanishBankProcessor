package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvLineSplitterTest {

    @Test
    void splitsOnSemicolons() {
        assertEquals(List.of("one", "two", "three"), CsvLineSplitter.split("one;two;three"));
    }

    @Test
    void keepsEmptyValues() {
        assertEquals(List.of("one", "", "three"), CsvLineSplitter.split("one;;three"));
        assertEquals(List.of("", "two", ""), CsvLineSplitter.split(";two;"));
        assertEquals(List.of(""), CsvLineSplitter.split(""));
    }

    @Test
    void aQuotedValueMayHoldSemicolons() {
        assertEquals(List.of("Ocio;y cultura", "Restaurantes"), CsvLineSplitter.split("\"Ocio;y cultura\";Restaurantes"));
    }

    @Test
    void twoQuotesInsideAQuotedValueAreOne() {
        assertEquals(List.of("Dice \"hola\"", "x"), CsvLineSplitter.split("\"Dice \"\"hola\"\"\";x"));
    }

    @Test
    void keepsPipesAndAsterisksOfTheDefinitions() {
        assertEquals(List.of("Alimentacion", "Supermercado", "*MERCADONA*|*CARREFOUR*"),
                CsvLineSplitter.split("Alimentacion;Supermercado;*MERCADONA*|*CARREFOUR*"));
    }
}
