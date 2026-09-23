package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.List;

/**
 * What matching the movements against the categories produced: the rows of the result file, one
 * per movement, and the movements that matched no category and took the default one, which are
 * written to the log as well.
 */
public record Categorization(List<CategorizedMovement> rows, List<MovementLine> movementsWithoutCategory) {

    public Categorization {
        rows = List.copyOf(rows);
        movementsWithoutCategory = List.copyOf(movementsWithoutCategory);
    }
}
