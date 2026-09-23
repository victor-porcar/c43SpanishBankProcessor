package com.github.victormpcmun.c43spanishbankprocessor.model;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDefinitionsTest {

    private static final DefinitionForCategorySubcategory RESTAURANTS =
            new DefinitionForCategorySubcategory("Ocio", "Restaurantes", "*CAFETERIA*");
    private static final DefinitionForCategorySubcategory SALARY =
            new DefinitionForCategorySubcategory("Nomina", "Empresa", "*NOMINA*");
    private static final DefinitionForCategorySubcategory DEFAULT_ONE =
            new DefinitionForCategorySubcategory("SIN CLASIFICAR", "REVISAR", "***");

    @Test
    void theLineWithThreeAsterisksGivesTheDefaultCategory() {
        CategoryDefinitions definitions = CategoryDefinitions.of(List.of(RESTAURANTS, DEFAULT_ONE, SALARY));

        assertEquals("SIN CLASIFICAR", definitions.defaultCategory().getCategory());
        assertEquals("REVISAR", definitions.defaultCategory().getSubcategory());
    }

    @Test
    void theDefaultCategoryDoesNotMatchMovementsByItself() {
        CategoryDefinitions definitions = CategoryDefinitions.of(List.of(RESTAURANTS, DEFAULT_ONE, SALARY));

        assertEquals(List.of(RESTAURANTS, SALARY), definitions.categories());
    }

    @Test
    void theFirstDefaultLineWins() {
        DefinitionForCategorySubcategory another = new DefinitionForCategorySubcategory("OTRA", "OTRA", "***");

        CategoryDefinitions definitions = CategoryDefinitions.of(List.of(DEFAULT_ONE, another));

        assertEquals("SIN CLASIFICAR", definitions.defaultCategory().getCategory());
        assertEquals(List.of(), definitions.categories());
    }

    @Test
    void withoutADefaultLineItIsAnError() {
        C43Exception error = assertThrows(C43Exception.class,
                () -> CategoryDefinitions.of(List.of(RESTAURANTS, SALARY)));

        assertTrue(error.getMessage().contains("***"));
    }

    @Test
    void aDefinitionThatOnlyLooksLikeTheDefaultOneIsANormalCategory() {
        DefinitionForCategorySubcategory similar =
                new DefinitionForCategorySubcategory("Todo", "Todo", "*** y algo mas");

        CategoryDefinitions definitions = CategoryDefinitions.of(List.of(similar, DEFAULT_ONE));

        assertEquals(List.of(similar), definitions.categories());
    }
}
