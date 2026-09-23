package com.github.victormpcmun.c43spanishbankprocessor.model;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Matches text against patterns where "*" stands for zero or more characters of any kind and
 * everything else is literal, so "*Hola*test*" matches "Hola mundotest kk". The pattern has to
 * match the whole text.
 *
 * <p>A definition holds one or more patterns separated by "|", and the text matches the
 * definition when it matches at least one of them. A blank definition matches nothing.
 */
final class WildcardPattern {

    private static final String PATTERN_SEPARATOR = "|";
    private static final String ANY_CHARACTERS = "*";

    private WildcardPattern() {
    }

    static boolean matchesAny(String definition, String text) {
        if (definition == null || text == null || definition.isBlank()) {
            return false;
        }
        return Arrays.stream(definition.split(Pattern.quote(PATTERN_SEPARATOR), -1))
                .filter(pattern -> !pattern.isBlank())
                .anyMatch(pattern -> matches(pattern, text));
    }

    private static boolean matches(String pattern, String text) {
        return Pattern.compile(regexOf(pattern), Pattern.DOTALL).matcher(text).matches();
    }

    /** Every part between asterisks is literal; the asterisks become "any characters". */
    private static String regexOf(String pattern) {
        return Arrays.stream(pattern.split(Pattern.quote(ANY_CHARACTERS), -1))
                .map(Pattern::quote)
                .reduce((left, right) -> left + ".*" + right)
                .orElse("");
    }
}
