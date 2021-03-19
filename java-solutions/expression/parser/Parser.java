package expression.parser;

import expression.Expression;
import expression.exceptions.ParserException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser<T> {
    Expression<T> parse(String expression) throws ParserException;
}
