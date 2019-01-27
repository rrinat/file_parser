package com.rrinat.fileparser;

import java.util.ArrayList;
import java.util.List;

import static com.rrinat.fileparser.Consts.REG_EXPR_SYMBOLS_ANY;
import static com.rrinat.fileparser.Consts.REG_EXPR_SYMBOL_LINE_END;
import static com.rrinat.fileparser.Consts.REX_EXPR_SYMBOL_ANY_ONE;
import static com.rrinat.fileparser.Consts.SYMBOL_LINE_CARRIAGE_RETURN;
import static com.rrinat.fileparser.Consts.SYMBOL_LINE_END;

class StringParser {

    private final String regExpr;

    private int currentSymbolIndex = 0;
    private byte[] data;
    private int dataLength = 0;
    private int regExprIndex = 0;

    private StringBuilder stringBuilder = new StringBuilder();
    private List<String> lines = new ArrayList<>();
    private boolean isAcceptableLine = true;

    StringParser(String regExpr) {
        this.regExpr = regExpr;
    }

    public void parse(byte[] data, int dataLength) {
        currentSymbolIndex = 0;
        this.data = data;
        this.dataLength = dataLength;
        
        loopRegExpr();
    }

    public List<String> getLines() {
        return lines;
    }

    public void clearLines() {
        lines.clear();
    }

    public void addLastLine() {
        if (regExpr.charAt(regExprIndex) == REG_EXPR_SYMBOL_LINE_END
                || regExpr.charAt(regExprIndex) == REG_EXPR_SYMBOLS_ANY) {
            addNewLine();
        }
    }

    private void loopRegExpr() {
        while (regExprIndex < regExpr.length() && currentSymbolIndex < dataLength) {
            switch (regExpr.charAt(regExprIndex)) {
                case REG_EXPR_SYMBOLS_ANY:
                    acceptAnySymbol();
                    break;
                case REX_EXPR_SYMBOL_ANY_ONE:
                    acceptAnyOneSymbol();
                    break;
                case REG_EXPR_SYMBOL_LINE_END:
                    acceptLineEndSymbol();
                    break;
                default: acceptExactSymbol();
            }
            regExprIndex += 1;
        }
    }

    private void acceptAnySymbol() {
        if (isAchieveRegExprEnd()) {
            while (!isAchievedDataEnd()) {
                if (isAchievedLineEnd()) {
                    addNewLine();
                    resetLineParsing();
                    skipUntilNotLineEndSymbols();
                    break;
                } else {
                    appendSymbol();
                    incrementCurrentSymbol();
                }
            }
        }
    }

    private void acceptAnyOneSymbol() {
        if (isAchievedDataEnd()) return;

        if (isAchievedLineEnd()) {
            resetLineParsing();
            skipUntilNotLineEndSymbols();
        } else {
            appendSymbol();
            incrementCurrentSymbol();
        }
    }

    private void acceptExactSymbol() {
        if (isAchievedDataEnd()) return;

        if (isAchievedLineEnd()) {
            resetLineParsing();
            skipUntilNotLineEndSymbols();
        } else {
            if (isPrevAnyRegExprSymbols()) {
                appendByExactSymbol();
            } else {
                if (isEqualExactSymbol()) {
                    appendSymbol();
                    incrementCurrentSymbol();
                } else {
                    resetLineParsing();
                    incrementCurrentSymbol();
                }
            }
        }
    }

    private void appendByExactSymbol() {
        while (!isAchievedDataEnd()) {
            if (isAchievedLineEnd()) {
                resetLineParsing();
                skipUntilNotLineEndSymbols();
                break;
            }

            appendSymbol();
            if (isEqualExactSymbol()) {
                incrementCurrentSymbol();
                break;
            }
            incrementCurrentSymbol();
        }
    }

    private void acceptLineEndSymbol() {
        if (isAchievedDataEnd()) return;

        if (isAchievedLineEnd()) {
            addNewLine();
            resetLineParsing();
            skipUntilNotLineEndSymbols();
        } else {
            isAcceptableLine = false;
            loopUntilLineEnd();
        }
    }

    private boolean isAchieveRegExprEnd() {
        return regExprIndex == regExpr.length() - 1;
    }

    private void appendSymbol() {
        stringBuilder.append((char)data[currentSymbolIndex]);
    }

    private void addNewLine() {
        if (isAcceptableLine) {
            lines.add(stringBuilder.toString());
        }
    }

    private void resetLineParsing() {
        stringBuilder.setLength(0);
        regExprIndex = -1;
        isAcceptableLine = true;
    }

    private void incrementCurrentSymbol() {
        currentSymbolIndex += 1;
    }

    private void skipUntilNotLineEndSymbols() {
        while (currentSymbolIndex < dataLength && isAchievedLineEnd()) {
            incrementCurrentSymbol();
        }
    }

    private boolean isAchievedDataEnd() {
        if (currentSymbolIndex == dataLength) {
            regExprIndex -= 1;
            return true;
        }
        return false;
    }

    private boolean isEqualExactSymbol() {
        return data[currentSymbolIndex] == regExpr.charAt(regExprIndex);
    }

    private boolean isAchievedLineEnd() {
        return data[currentSymbolIndex] == SYMBOL_LINE_END || data[currentSymbolIndex] == SYMBOL_LINE_CARRIAGE_RETURN;
    }

    private void loopUntilLineEnd() {
        while (!isAchievedDataEnd()) {
            if (isAchievedLineEnd()) {
                resetLineParsing();
                skipUntilNotLineEndSymbols();
                break;
            }
            incrementCurrentSymbol();
        }
    }

    private boolean isPrevAnyRegExprSymbols() {
        return regExprIndex > 0 && regExpr.charAt(regExprIndex - 1) == REG_EXPR_SYMBOLS_ANY;
    }
}
