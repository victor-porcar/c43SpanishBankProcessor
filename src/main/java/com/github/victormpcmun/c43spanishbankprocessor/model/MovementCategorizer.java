package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Matches every movement against the category definitions. A movement takes the first definition
 * it matches, in the order of the CSV; a movement matching none takes the default category and is
 * kept apart as well, to be logged.
 */
public class MovementCategorizer {

    public Categorization categorize(List<MovementLine> movements,
                                     List<DefinitionForCategorySubcategory> categories,
                                     DefinitionForCategorySubcategory defaultCategory) {
        List<CategorizedMovement> rows = new ArrayList<>();
        List<MovementLine> withoutCategory = new ArrayList<>();
        for (MovementLine movement : movements) {
            Optional<DefinitionForCategorySubcategory> category = firstCategoryOf(movement, categories);
            if (category.isEmpty()) {
                withoutCategory.add(movement);
            }
            rows.add(new CategorizedMovement(movement, category.orElse(defaultCategory)));
        }
        return new Categorization(rows, withoutCategory);
    }

    private static Optional<DefinitionForCategorySubcategory> firstCategoryOf(
            MovementLine movement, List<DefinitionForCategorySubcategory> categories) {
        return categories.stream()
                .filter(category -> movement.matchPattern(category.getDefinition()))
                .findFirst();
    }
}
