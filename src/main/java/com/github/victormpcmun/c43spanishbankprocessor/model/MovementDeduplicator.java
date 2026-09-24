package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Leaves out the movements that repeat one already seen, which happens when two statements cover
 * the same days. Two movements are the same when their whole line is, that is every field of the
 * movement record and of the records that complete it: dates, amount, concepts and references.
 * The first one of them is kept.
 */
public class MovementDeduplicator {

    public Deduplication removeDuplicates(List<MovementLine> movements) {
        Set<String> seen = new HashSet<>();
        List<MovementLine> kept = new ArrayList<>();
        List<MovementLine> duplicates = new ArrayList<>();
        for (MovementLine movement : movements) {
            if (seen.add(movement.getPlainLine())) {
                kept.add(movement);
            } else {
                duplicates.add(movement);
            }
        }
        return new Deduplication(kept, duplicates);
    }
}
