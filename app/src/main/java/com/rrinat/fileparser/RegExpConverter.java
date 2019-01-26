package com.rrinat.fileparser;

import java.util.regex.Pattern;

public class RegExpConverter {

    private static final String INPUT_REG_EXP_ANY_SYMBOLS = "*";
    private static final String INPUT_REG_EXP_ONE_ANY_SYMBOL = "\\?";

    private static final String JAVA_REG_EXP_ANY_SYMBOLS = ".*";
    private static final String JAVA_REG_EXP_ONE_ANE_SYMBOL = ".{1}";

    public Pattern convertPattern(final String regex) {
        String start = "";
        String end = "";

        if (!regex.startsWith(INPUT_REG_EXP_ANY_SYMBOLS)
                && !regex.startsWith(INPUT_REG_EXP_ONE_ANY_SYMBOL)) {
            start = "^";
        }
        if (!regex.endsWith(INPUT_REG_EXP_ANY_SYMBOLS)
                && !regex.endsWith(INPUT_REG_EXP_ONE_ANY_SYMBOL)) {
            end = "$";
        }

        String pattern = regex.replaceAll("\\*{1,}", JAVA_REG_EXP_ANY_SYMBOLS)
                .replaceAll(INPUT_REG_EXP_ONE_ANY_SYMBOL, JAVA_REG_EXP_ONE_ANE_SYMBOL);

        return Pattern.compile(start + pattern + end);
    }
}
