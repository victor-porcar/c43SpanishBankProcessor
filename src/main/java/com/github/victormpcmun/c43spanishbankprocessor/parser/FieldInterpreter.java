package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

/**
 * Explains what the characters of a field mean, depending on its type. Plain text and numbers
 * need no explanation; a value that does not fit its type gets none either.
 */
final class FieldInterpreter {

    private static final DateTimeFormatter C43_DATE = DateTimeFormatter.ofPattern("uuMMdd");

    private static final Map<String, String> DEBIT_CREDIT = Map.of(
            "1", "debe",
            "2", "haber");

    private static final Map<String, String> CURRENCIES = Map.of(
            "978", "EUR",
            "840", "USD",
            "826", "GBP",
            "756", "CHF",
            "392", "JPY");

    private static final Map<String, String> COMMON_CONCEPTS = Map.ofEntries(
            Map.entry("01", "Talones - reintegros"),
            Map.entry("02", "Abonarés - entregas - ingresos"),
            Map.entry("03", "Domiciliados - recibos - letras - pagos por su cuenta"),
            Map.entry("04", "Giros - transferencias - traspasos - cheques"),
            Map.entry("05", "Amortizaciones préstamos, créditos, etc."),
            Map.entry("06", "Remesas efectos"),
            Map.entry("07", "Suscripciones - dividendos - ventas de derechos"),
            Map.entry("08", "Dividendos - cupones - amortizaciones"),
            Map.entry("09", "Operaciones de valores"),
            Map.entry("10", "Cheques gasolina"),
            Map.entry("11", "Cajeros automáticos"),
            Map.entry("12", "Tarjetas de crédito - débito"),
            Map.entry("13", "Operaciones extranjero"),
            Map.entry("14", "Devoluciones e impagados"),
            Map.entry("15", "Nóminas - seguros sociales"),
            Map.entry("16", "Timbres - corretaje - póliza"),
            Map.entry("17", "Intereses - comisiones - custodia - gastos e impuestos"),
            Map.entry("98", "Anulaciones - correcciones asiento"),
            Map.entry("99", "Varios"));

    private FieldInterpreter() {
    }

    static Optional<String> meaningOf(FieldType type, String rawValue) {
        String value = rawValue.strip();
        return switch (type) {
            case TEXT, NUMBER -> Optional.empty();
            case DATE -> dateOf(value);
            case AMOUNT -> amountOf(value);
            case DEBIT_CREDIT -> Optional.ofNullable(DEBIT_CREDIT.get(value));
            case CURRENCY -> Optional.ofNullable(CURRENCIES.get(value));
            case COMMON_CONCEPT -> Optional.ofNullable(COMMON_CONCEPTS.get(value));
        };
    }

    /** YYMMDD, read as a date of the 2000s. */
    private static Optional<String> dateOf(String value) {
        try {
            return Optional.of(LocalDate.parse(value, C43_DATE).toString());
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /** Cents without decimal separator: 00000001234567 means 12345.67. */
    private static Optional<String> amountOf(String value) {
        if (!value.matches("\\d+")) {
            return Optional.empty();
        }
        return Optional.of(new BigDecimal(value).movePointLeft(2).toPlainString());
    }
}
