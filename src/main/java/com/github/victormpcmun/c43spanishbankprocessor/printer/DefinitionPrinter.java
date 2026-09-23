package com.github.victormpcmun.c43spanishbankprocessor.printer;

import com.github.victormpcmun.c43spanishbankprocessor.model.DefinitionForCategorySubcategory;

import java.io.PrintStream;

/**
 * Prints a category definition showing its three fields, one per line.
 */
public class DefinitionPrinter {

    private final PrintStream out;

    public DefinitionPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(int number, DefinitionForCategorySubcategory definition) {
        out.printf("Definition %d%n", number);
        out.printf("  category    : %s%n", definition.getCategory());
        out.printf("  subcategory : %s%n", definition.getSubcategory());
        out.printf("  definition  : %s%n", definition.getDefinition());
        out.println();
    }
}
