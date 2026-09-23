package com.github.victormpcmun.c43spanishbankprocessor.model;

/**
 * One movement of the statement: the whole line as plain text plus the few values taken out of it.
 */
public class MovementLine {

    private String year;
    private String month;
    private String importe;
    private String plainLine;

    public MovementLine() {
    }

    public MovementLine(String year, String month, String importe, String plainLine) {
        this.year = year;
        this.month = month;
        this.importe = importe;
        this.plainLine = plainLine;
    }

    /** Year of the value date (fecha valor), such as "2026". */
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    /** Month of the value date (fecha valor), named in Spanish, such as "JULIO". */
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    /** Amount of the movement, such as "950.0". */
    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    /** The whole movement as printed: every field of the 22 record and of its complements. */
    public String getPlainLine() {
        return plainLine;
    }

    public void setPlainLine(String plainLine) {
        this.plainLine = plainLine;
    }

    /**
     * Whether the plain line matches at least one of the patterns of the definition, which holds
     * one or more patterns separated by "|". In a pattern "*" stands for zero or more characters
     * of any kind and the rest is literal, so "*Hola*test*" matches "Hola mundotest kk". The
     * pattern has to match the whole line. A definition that is blank matches nothing.
     */
    public boolean matchPattern(String definition) {
        return WildcardPattern.matchesAny(definition, plainLine);
    }

    @Override
    public String toString() {
        return "MovementLine{year='%s', month='%s', importe='%s', plainLine='%s'}"
                .formatted(year, month, importe, plainLine);
    }
}
