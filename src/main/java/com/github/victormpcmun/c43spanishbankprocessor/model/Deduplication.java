package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.List;

/**
 * What looking for repeated movements produced: the movements to process and the ones left out
 * because an identical movement came before, which are written to the log.
 */
public record Deduplication(List<MovementLine> movements, List<MovementLine> duplicates) {

    public Deduplication {
        movements = List.copyOf(movements);
        duplicates = List.copyOf(duplicates);
    }
}
