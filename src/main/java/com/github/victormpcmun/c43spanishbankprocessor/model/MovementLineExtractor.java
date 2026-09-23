package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds a MovementLine out of a formatted line, reading the value date and the amount from the
 * text itself. A line without both of them is not a movement and gives nothing.
 */
public class MovementLineExtractor {

    /** "Fecha valor:260702" -> 26 (year), 07 (month). */
    private static final Pattern VALUE_DATE = Pattern.compile("Fecha valor:(\\d{2})(\\d{2})\\d{2}");

    /** "Importe:00000000095000 (950.00)" -> 950.00; "Importe en divisa origen" is left out. */
    private static final Pattern AMOUNT = Pattern.compile("Importe:\\d+ \\((-?\\d+\\.\\d+)\\)");

    /** "Clave debe o haber:1" -> a debit, money out; "2" is a credit, money in. */
    private static final Pattern DEBIT_CREDIT = Pattern.compile("Clave debe o haber:(\\d)");
    private static final String DEBIT = "1";

    private static final String[] MONTHS = {
            "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
            "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};

    private static final int FIRST_YEAR_OF_CENTURY = 2000;

    public Optional<MovementLine> extract(String plainLine) {
        Matcher valueDate = VALUE_DATE.matcher(plainLine);
        Matcher amount = AMOUNT.matcher(plainLine);
        if (!valueDate.find() || !amount.find()) {
            return Optional.empty();
        }
        return Optional.of(new MovementLine(
                yearOf(valueDate.group(1)),
                monthOf(valueDate.group(2)),
                importeOf(amount.group(1), isDebit(plainLine)),
                plainLine));
    }

    private static boolean isDebit(String plainLine) {
        Matcher debitCredit = DEBIT_CREDIT.matcher(plainLine);
        return debitCredit.find() && debitCredit.group(1).equals(DEBIT);
    }

    private static String yearOf(String twoDigitYear) {
        return String.valueOf(FIRST_YEAR_OF_CENTURY + Integer.parseInt(twoDigitYear));
    }

    /** "07" -> "JULIO"; a month out of range keeps its digits. */
    private static String monthOf(String twoDigitMonth) {
        int month = Integer.parseInt(twoDigitMonth);
        return month >= 1 && month <= MONTHS.length ? MONTHS[month - 1] : twoDigitMonth;
    }

    /** "950.00" -> "950.0", the plain number without its trailing zeros, negative for a debit. */
    private static String importeOf(String amount, boolean debit) {
        BigDecimal value = new BigDecimal(amount);
        return String.valueOf((debit ? value.negate() : value).doubleValue());
    }
}
