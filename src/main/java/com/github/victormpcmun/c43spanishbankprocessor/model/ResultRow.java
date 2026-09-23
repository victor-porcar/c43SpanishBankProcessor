package com.github.victormpcmun.c43spanishbankprocessor.model;

/**
 * One line of the result file: the amounts of every movement of a year, month, category and
 * subcategory, added up.
 */
public record ResultRow(String year, String month, String category, String subcategory, String importe) {
}
