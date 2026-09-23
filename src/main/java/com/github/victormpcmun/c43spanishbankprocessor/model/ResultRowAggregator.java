package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds up the movements that share year, month, category and subcategory, leaving one result row
 * for each of those groups, in the order the groups first appear. Amounts carry their sign, so
 * debits subtract and credits add.
 */
public class ResultRowAggregator {

    public List<ResultRow> aggregate(List<CategorizedMovement> rows) {
        Map<ResultRow, BigDecimal> totals = new LinkedHashMap<>();
        for (CategorizedMovement row : rows) {
            totals.merge(groupOf(row), amountOf(row), BigDecimal::add);
        }
        return totals.entrySet().stream()
                .map(total -> withImporte(total.getKey(), total.getValue()))
                .toList();
    }

    /** The four values that make a group; the amount is left out, it is what gets added up. */
    private static ResultRow groupOf(CategorizedMovement row) {
        return new ResultRow(row.movement().getYear(), row.movement().getMonth(),
                row.category().getCategory(), row.category().getSubcategory(), "");
    }

    private static BigDecimal amountOf(CategorizedMovement row) {
        return new BigDecimal(row.movement().getImporte());
    }

    /** Written as a plain number without trailing zeros, the same way a movement amount is. */
    private static ResultRow withImporte(ResultRow group, BigDecimal total) {
        return new ResultRow(group.year(), group.month(), group.category(), group.subcategory(),
                String.valueOf(total.doubleValue()));
    }
}
