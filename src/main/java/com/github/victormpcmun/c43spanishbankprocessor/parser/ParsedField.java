package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.layout.FieldDefinition;

import java.util.Optional;

/**
 * A field as found in a line: its definition and its characters.
 */
public record ParsedField(FieldDefinition definition, String rawValue) {

    public String name() {
        return definition.name();
    }

    /** The characters of the field without the trailing blanks that pad it. */
    public String value() {
        return rawValue.stripTrailing();
    }

    /** A field made only of blanks carries no information. */
    public boolean isInformed() {
        return !rawValue.isBlank();
    }

    /** What the value means (a date, an amount, a currency...), when it needs explaining. */
    public Optional<String> meaning() {
        return FieldInterpreter.meaningOf(definition.type(), rawValue);
    }
}
