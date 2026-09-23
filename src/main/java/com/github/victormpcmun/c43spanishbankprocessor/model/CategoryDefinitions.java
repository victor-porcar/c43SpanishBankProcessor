package com.github.victormpcmun.c43spanishbankprocessor.model;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.util.List;

/**
 * The categories read from the CSV, told apart: the ones that match movements by their definition
 * and the default one, the line whose definition is "***", used by the movements that match none
 * of the others. The default one is left out of the matching: its "***" would match everything.
 */
public record CategoryDefinitions(List<DefinitionForCategorySubcategory> categories,
                                  DefinitionForCategorySubcategory defaultCategory) {

    /** The definition that marks the default category. */
    public static final String DEFAULT_CATEGORY_DEFINITION = "***";

    public CategoryDefinitions {
        categories = List.copyOf(categories);
    }

    public static CategoryDefinitions of(List<DefinitionForCategorySubcategory> definitions) {
        return new CategoryDefinitions(matchingOnes(definitions), defaultOne(definitions));
    }

    private static List<DefinitionForCategorySubcategory> matchingOnes(
            List<DefinitionForCategorySubcategory> definitions) {
        return definitions.stream().filter(definition -> !isDefault(definition)).toList();
    }

    /** The first line marked as the default one; without it there is nowhere to put the rest. */
    private static DefinitionForCategorySubcategory defaultOne(List<DefinitionForCategorySubcategory> definitions) {
        return definitions.stream()
                .filter(CategoryDefinitions::isDefault)
                .findFirst()
                .orElseThrow(() -> new C43Exception("the categories file needs a line whose definition is "
                        + DEFAULT_CATEGORY_DEFINITION + ", to name the category of the movements that match no other"));
    }

    private static boolean isDefault(DefinitionForCategorySubcategory definition) {
        return DEFAULT_CATEGORY_DEFINITION.equals(definition.getDefinition());
    }
}
