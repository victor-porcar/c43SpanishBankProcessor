package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.RecordType;

import java.util.ArrayList;
import java.util.List;

/**
 * Joins every movement (22) with the concept (23) and currency equivalence (24) records that
 * follow it. Other records stay on their own, and so does a complement with no movement above it.
 */
public class MovementGrouper {

    public List<RecordGroup> group(List<ParsedRecord> records) {
        List<RecordGroup> groups = new ArrayList<>();
        for (ParsedRecord record : records) {
            if (complementsLastMovement(groups, record)) {
                groups.set(groups.size() - 1, last(groups).with(record));
            } else {
                groups.add(RecordGroup.of(record));
            }
        }
        return groups;
    }

    private static boolean complementsLastMovement(List<RecordGroup> groups, ParsedRecord record) {
        return isComplement(record) && !groups.isEmpty() && isMovement(last(groups).main());
    }

    private static boolean isComplement(ParsedRecord record) {
        return record.type().map(RecordType::complementsMovement).orElse(false);
    }

    private static boolean isMovement(ParsedRecord record) {
        return record.type().map(type -> type == RecordType.MOVEMENT).orElse(false);
    }

    private static RecordGroup last(List<RecordGroup> groups) {
        return groups.get(groups.size() - 1);
    }
}
