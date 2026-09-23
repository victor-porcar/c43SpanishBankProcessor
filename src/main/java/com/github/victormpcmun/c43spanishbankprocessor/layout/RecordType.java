package com.github.victormpcmun.c43spanishbankprocessor.layout;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.AMOUNT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.COMMON_CONCEPT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.CURRENCY;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.DATE;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.DEBIT_CREDIT;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.NUMBER;
import static com.github.victormpcmun.c43spanishbankprocessor.layout.FieldType.TEXT;

/**
 * The records of the AEB Cuaderno 43 (Norma 43) format. Every line has 80 characters and its
 * first two tell the record type. Positions count from 1; the unused ("libre") parts are left out.
 */
public enum RecordType {

    ACCOUNT_HEADER("11", "Cabecera de cuenta",
            field("Clave de la entidad", 3, 4, NUMBER),
            field("Clave de oficina", 7, 4, NUMBER),
            field("Número de cuenta", 11, 10, NUMBER),
            field("Fecha inicial", 21, 6, DATE),
            field("Fecha final", 27, 6, DATE),
            field("Clave debe o haber del saldo inicial", 33, 1, DEBIT_CREDIT),
            field("Saldo inicial", 34, 14, AMOUNT),
            field("Clave de divisa", 48, 3, CURRENCY),
            field("Modalidad de información", 51, 1, TEXT),
            field("Nombre abreviado", 52, 26, TEXT)),

    MOVEMENT("22", "Movimiento",
            field("Clave de oficina origen", 7, 4, NUMBER),
            field("Fecha de operación", 11, 6, DATE),
            field("Fecha valor", 17, 6, DATE),
            field("Concepto común", 23, 2, COMMON_CONCEPT),
            field("Concepto propio", 25, 3, TEXT),
            field("Clave debe o haber", 28, 1, DEBIT_CREDIT),
            field("Importe", 29, 14, AMOUNT),
            field("Número de documento", 43, 10, TEXT),
            field("Referencia 1", 53, 12, TEXT),
            field("Referencia 2", 65, 16, TEXT)),

    CONCEPT("23", "Concepto complementario",
            field("Código de dato", 3, 2, NUMBER),
            field("Concepto 1", 5, 38, TEXT),
            field("Concepto 2", 43, 38, TEXT)),

    CURRENCY_EQUIVALENCE("24", "Equivalencia de divisa",
            field("Código de dato", 3, 2, NUMBER),
            field("Clave de divisa origen", 5, 3, CURRENCY),
            field("Importe en divisa origen", 8, 14, AMOUNT)),

    ACCOUNT_FOOTER("33", "Final de cuenta",
            field("Clave de la entidad", 3, 4, NUMBER),
            field("Clave de oficina", 7, 4, NUMBER),
            field("Número de cuenta", 11, 10, NUMBER),
            field("Número de apuntes debe", 21, 5, NUMBER),
            field("Total importes debe", 26, 14, AMOUNT),
            field("Número de apuntes haber", 40, 5, NUMBER),
            field("Total importes haber", 45, 14, AMOUNT),
            field("Clave debe o haber del saldo final", 59, 1, DEBIT_CREDIT),
            field("Saldo final", 60, 14, AMOUNT),
            field("Clave de divisa", 74, 3, CURRENCY)),

    FILE_END("88", "Fin de fichero",
            field("Número de registros", 21, 6, NUMBER));

    public static final int LINE_LENGTH = 80;

    private final String code;
    private final String description;
    private final List<FieldDefinition> fields;

    RecordType(String code, String description, FieldDefinition... fields) {
        this.code = code;
        this.description = description;
        this.fields = List.of(fields);
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    public List<FieldDefinition> fields() {
        return fields;
    }

    /** Concept (23) and currency equivalence (24) records complete the movement right above them. */
    public boolean complementsMovement() {
        return this == CONCEPT || this == CURRENCY_EQUIVALENCE;
    }

    /** The record type of a line, told by its first two characters. */
    public static Optional<RecordType> of(String line) {
        return Arrays.stream(values())
                .filter(type -> line.startsWith(type.code))
                .findFirst();
    }

    private static FieldDefinition field(String name, int start, int length, FieldType type) {
        return new FieldDefinition(name, start, length, type);
    }
}
