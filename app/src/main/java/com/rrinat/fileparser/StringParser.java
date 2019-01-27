package com.rrinat.fileparser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rrinat.fileparser.Consts.REG_EXPR_SYMBOLS_ANY;
import static com.rrinat.fileparser.Consts.REG_EXPR_SYMBOL_LINE_END;
import static com.rrinat.fileparser.Consts.REX_EXPR_SYMBOL_ANY_ONE;
import static com.rrinat.fileparser.Consts.SYMBOL_LINE_CARRIAGE_RETURN;
import static com.rrinat.fileparser.Consts.SYMBOL_LINE_END;

class StringParser {

    private final Charset characterSet = Charset.forName("US-ASCII");

    private final String regExpr;

    private int currentSymbolIndex = 0;
    private byte[] data;
    private int dataLength = 0;
    private int regExprIndex = 0;
    private byte regChar;

    private int beginLine = -1;
    private List<String> lines = new ArrayList<>();
    private boolean isAcceptableLine = true;
    private String prevLine = "";

    StringParser(String regExpr) {
        this.regExpr = regExpr;
    }

    public void parse(byte[] data, int dataLength) {
        prevLine = copyLastLine();
        beginLine = 0;
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
            regChar = (byte)regExpr.charAt(regExprIndex);
            switch (regChar) {
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
                    currentSymbolIndex += 1;
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
            currentSymbolIndex += 1;
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
                    currentSymbolIndex += 1;
                } else {
                    currentSymbolIndex += 1;
                    resetLineParsing();
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
                currentSymbolIndex += 1;
                break;
            }
            currentSymbolIndex += 1;
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
        if (beginLine < 0) {
            beginLine = currentSymbolIndex;
        }
    }

    private void addNewLine() {
        if (isAcceptableLine && beginLine >= 0) {
            lines.add(prevLine + copyLine());
            prevLine = "";
        }
    }

    private String copyLastLine() {
        if (beginLine > 0) {
            return copyLine();
        } else {
            return "";
        }
    }

    private String copyLine() {
        if (beginLine == currentSymbolIndex) {
            return "";
        } else {
            return new String(Arrays.copyOfRange(data, beginLine, currentSymbolIndex), characterSet);
        }
    }

    private void resetLineParsing() {
        beginLine = -1;
        regExprIndex = -1;
        isAcceptableLine = true;
    }

    private void skipUntilNotLineEndSymbols() {
        while (currentSymbolIndex < dataLength && isAchievedLineEnd()) {
            currentSymbolIndex += 1;
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
        return data[currentSymbolIndex] == regChar;
    }

    private boolean isAchievedLineEnd() {
        byte symbol = data[currentSymbolIndex];
        return symbol == SYMBOL_LINE_END || symbol == SYMBOL_LINE_CARRIAGE_RETURN;
    }

    private void loopUntilLineEnd() {
        while (!isAchievedDataEnd()) {
            if (isAchievedLineEnd()) {
                resetLineParsing();
                skipUntilNotLineEndSymbols();
                break;
            }
            currentSymbolIndex += 1;
        }
    }

    private boolean isPrevAnyRegExprSymbols() {
        return regExprIndex > 0 && regExpr.charAt(regExprIndex - 1) == REG_EXPR_SYMBOLS_ANY;
    }
}
