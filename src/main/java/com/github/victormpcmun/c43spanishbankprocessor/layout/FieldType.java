package com.github.victormpcmun.c43spanishbankprocessor.layout;

/**
 * How the characters of a field are to be understood.
 */
public enum FieldType {

    /** Free text. */
    TEXT,

    /** Whole number, possibly padded with leading zeros. */
    NUMBER,

    /** Date written as YYMMDD. */
    DATE,

    /** Amount in cents, without decimal separator: the last two digits are the decimals. */
    AMOUNT,

    /** 1 = debit (debe), 2 = credit (haber). */
    DEBIT_CREDIT,

    /** ISO 4217 numeric currency code, such as 978 for euros. */
    CURRENCY,

    /** Common concept code shared by every bank, such as 12 for cards. */
    COMMON_CONCEPT
}
