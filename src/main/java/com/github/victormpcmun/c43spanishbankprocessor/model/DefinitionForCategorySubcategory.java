package com.github.victormpcmun.c43spanishbankprocessor.model;

/**
 * A category and subcategory together with the definition that tells which movements belong to
 * them: one or more patterns separated by "|", as MovementLine.matchPattern understands them.
 */
public class DefinitionForCategorySubcategory {

    private String category;
    private String subcategory;
    private String definition;

    public DefinitionForCategorySubcategory() {
    }

    public DefinitionForCategorySubcategory(String category, String subcategory, String definition) {
        this.category = category;
        this.subcategory = subcategory;
        this.definition = definition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "DefinitionForCategorySubcategory{category='%s', subcategory='%s', definition='%s'}"
                .formatted(category, subcategory, definition);
    }
}
