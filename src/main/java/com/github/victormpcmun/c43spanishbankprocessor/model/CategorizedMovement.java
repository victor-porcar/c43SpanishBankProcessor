package com.github.victormpcmun.c43spanishbankprocessor.model;

/**
 * A movement together with the category whose definition it matches: one row of the result file.
 */
public record CategorizedMovement(MovementLine movement, DefinitionForCategorySubcategory category) {
}
