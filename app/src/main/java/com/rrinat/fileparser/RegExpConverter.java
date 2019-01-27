package com.rrinat.fileparser;


public class RegExpConverter {

    public String normalize(final String regexp) {
        final String anySymbols = "" + Consts.REG_EXPR_SYMBOLS_ANY;

        String pattern = regexp.replaceAll("\\*{2,}", anySymbols);

        if (!regexp.endsWith(anySymbols)) {
            pattern += Consts.REG_EXPR_SYMBOL_LINE_END;
        }

        return pattern;
    }
}
