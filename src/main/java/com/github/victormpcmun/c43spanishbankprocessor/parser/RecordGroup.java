package com.github.victormpcmun.c43spanishbankprocessor.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A record together with the records that complete it: a movement with its concept (23) and
 * currency equivalence (24) records. Any other record makes a group on its own.
 */
public record RecordGroup(ParsedRecord main, List<ParsedRecord> complements) {

    public RecordGroup {
        complements = List.copyOf(complements);
    }

    public static RecordGroup of(ParsedRecord main) {
        return new RecordGroup(main, List.of());
    }

    public RecordGroup with(ParsedRecord complement) {
        List<ParsedRecord> all = new ArrayList<>(complements);
        all.add(complement);
        return new RecordGroup(main, all);
    }

    /** The main record followed by its complements, in file order. */
    public List<ParsedRecord> records() {
        List<ParsedRecord> all = new ArrayList<>();
        all.add(main);
        all.addAll(complements);
        return all;
    }
}
