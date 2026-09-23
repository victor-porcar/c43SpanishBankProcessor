package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultRowAggregatorTest {

    private final ResultRowAggregator aggregator = new ResultRowAggregator();

    @Test
    void addsUpTheMovementsOfTheSameYearMonthCategoryAndSubcategory() {
        List<ResultRow> rows = aggregator.aggregate(List.of(
                row("2026", "JULIO", "Ocio", "Restaurantes", "10.50"),
                row("2026", "JULIO", "Ocio", "Restaurantes", "35.49"),
                row("2026", "JULIO", "Ocio", "Cine", "8.0")));

        assertEquals(List.of(new ResultRow("2026", "JULIO", "Ocio", "Restaurantes", "45.99"),
                new ResultRow("2026", "JULIO", "Ocio", "Cine", "8.0")), rows);
    }

    @Test
    void everyValueOfTheKeyCountsForTheGrouping() {
        List<ResultRow> rows = aggregator.aggregate(List.of(
                row("2026", "JULIO", "Ocio", "Restaurantes", "10.0"),
                row("2025", "JULIO", "Ocio", "Restaurantes", "10.0"),
                row("2026", "AGOSTO", "Ocio", "Restaurantes", "10.0"),
                row("2026", "JULIO", "Casa", "Restaurantes", "10.0"),
                row("2026", "JULIO", "Ocio", "Cine", "10.0")));

        assertEquals(5, rows.size());
    }

    @Test
    void groupsKeepTheOrderOfTheirFirstMovement() {
        List<ResultRow> rows = aggregator.aggregate(List.of(
                row("2026", "JULIO", "Nomina", "Empresa", "2100.0"),
                row("2026", "JULIO", "Ocio", "Restaurantes", "10.0"),
                row("2026", "JULIO", "Nomina", "Empresa", "100.0")));

        assertEquals(List.of("Nomina", "Ocio"), rows.stream().map(ResultRow::category).toList());
        assertEquals("2200.0", rows.get(0).importe());
    }

    @Test
    void centsAreAddedUpExactly() {
        List<ResultRow> rows = aggregator.aggregate(List.of(
                row("2026", "JULIO", "Casa", "Luz", "0.1"),
                row("2026", "JULIO", "Casa", "Luz", "0.2")));

        assertEquals("0.3", rows.get(0).importe());
    }

    @Test
    void anEmptyListGivesNoRows() {
        assertEquals(List.of(), aggregator.aggregate(List.of()));
    }

    private static CategorizedMovement row(String year, String month, String category, String subcategory,
                                           String importe) {
        return new CategorizedMovement(new MovementLine(year, month, importe, "plain line"),
                new DefinitionForCategorySubcategory(category, subcategory, "*"));
    }
}
