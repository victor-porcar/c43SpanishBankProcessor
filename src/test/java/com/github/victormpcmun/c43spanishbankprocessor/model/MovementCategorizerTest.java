package com.github.victormpcmun.c43spanishbankprocessor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovementCategorizerTest {

    private static final MovementLine CAFETERIA = movement("*CAFETERIA* y algo mas");
    private static final MovementLine NOMINA = movement("*NOMINA* de septiembre");

    private static final DefinitionForCategorySubcategory RESTAURANTS =
            new DefinitionForCategorySubcategory("Ocio", "Restaurantes", "*CAFETERIA*");
    private static final DefinitionForCategorySubcategory SALARY =
            new DefinitionForCategorySubcategory("Nomina", "Empresa", "*NOMINA*");
    private static final DefinitionForCategorySubcategory EVERYTHING =
            new DefinitionForCategorySubcategory("Todo", "Todo", "*");
    private static final DefinitionForCategorySubcategory DEFAULT_CATEGORY =
            new DefinitionForCategorySubcategory("SIN CLASIFICAR", "REVISAR", "");

    private final MovementCategorizer categorizer = new MovementCategorizer();

    @Test
    void everyMovementGoesWithTheCategoryItMatches() {
        Categorization categorization = categorize(List.of(CAFETERIA, NOMINA), List.of(RESTAURANTS, SALARY));

        assertEquals(List.of("Ocio", "Nomina"), categoriesOf(categorization));
        assertEquals(List.of(), categorization.movementsWithoutCategory());
    }

    @Test
    void aMovementTakesTheFirstCategoryItMatches() {
        assertEquals(List.of("Ocio"), categoriesOf(categorize(List.of(CAFETERIA), List.of(RESTAURANTS, EVERYTHING))));
        assertEquals(List.of("Todo"), categoriesOf(categorize(List.of(CAFETERIA), List.of(EVERYTHING, RESTAURANTS))));
    }

    @Test
    void aMovementMatchingNoCategoryTakesTheDefaultOneAndIsLogged() {
        Categorization categorization = categorize(List.of(CAFETERIA, NOMINA), List.of(SALARY));

        assertEquals(List.of("SIN CLASIFICAR", "Nomina"), categoriesOf(categorization));
        assertEquals("REVISAR", categorization.rows().get(0).category().getSubcategory());
        assertEquals(List.of(CAFETERIA), categorization.movementsWithoutCategory());
    }

    @Test
    void withoutCategoriesEveryMovementTakesTheDefaultOne() {
        Categorization categorization = categorize(List.of(CAFETERIA, NOMINA), List.of());

        assertEquals(List.of("SIN CLASIFICAR", "SIN CLASIFICAR"), categoriesOf(categorization));
        assertEquals(List.of(CAFETERIA, NOMINA), categorization.movementsWithoutCategory());
    }

    private Categorization categorize(List<MovementLine> movements, List<DefinitionForCategorySubcategory> categories) {
        return categorizer.categorize(movements, categories, DEFAULT_CATEGORY);
    }

    private static List<String> categoriesOf(Categorization categorization) {
        return categorization.rows().stream().map(row -> row.category().getCategory()).toList();
    }

    private static MovementLine movement(String plainLine) {
        return new MovementLine("2026", "SEPTIEMBRE", "45.99", plainLine);
    }
}
