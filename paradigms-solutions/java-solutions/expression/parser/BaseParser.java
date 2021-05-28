package expression.parser;

import expression.exceptions.MismatchException;
import expression.exceptions.ParserException;

import java.util.function.Predicate;

public abstract class BaseParser {
    protected final static char EOF = '\0';
    protected char ch;
    private ExpressionSource source;

    protected void setSource(final ExpressionSource source) {
        this.source = source;
        nextChar();
    }

    protected void nextChar() {
        ch = source.hasNext() ? source.next() : EOF;
    }

    protected char forwardChar(int forward) {
        return source.hasNext(forward) ? source.getNext(forward) : EOF;
    }

    protected boolean test(final char expected) {
        return test(ch -> ch == expected);
    }

    protected boolean test(final Predicate<Character> tokenChecker) {
        if (tokenChecker.test(ch)) {
            nextChar();
            return true;
        }

        return false;
    }

    protected String parseToken(final Predicate<Character> tokenChecker) {
        final StringBuilder sb = new StringBuilder();

        while (ch != 0 && tokenChecker.test(ch)) {
            sb.append(ch);
            nextChar();
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    protected void expect(final char expected) throws ParserException {
        if (ch != expected) {
            throw new MismatchException(getPositionMessage("Expected: " + formatChar(expected) + ", found: " + formatChar(ch), ch == EOF ? 0 : -1));
        }

        nextChar();
    }

    protected boolean isDigit() {
        return isDigit(ch);
    }

    protected boolean isLetter() {
        return isLetter(ch);
    }

    protected String getPositionMessage(final String message, int delta) {
        return message + System.lineSeparator() + source.posExceptionMessage(delta);
    }

    protected static boolean isDigit(final char ch) {
        return '0' <= ch && ch <= '9';
    }

    protected static boolean isLetter(final char ch) {
        return Character.isLetter(ch);
    }

    protected static boolean isWhiteSpace(final char ch) {
        return ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t';
    }

    protected String formatChar(final char ch) {
        if (ch == EOF || isWhiteSpace(ch)) {
            return "nothing";
        } else {
            return "\"" + ch + "\"";
        }
    }
}
