package expression.parser;

import expression.*;
import expression.exceptions.ParserException;
import expression.generic.Calculator;

import java.util.List;
import java.util.Map;

public class ExpressionParser<T, C extends Calculator<T>> extends AbstractExpressionParser<T, C> implements Parser<T> {
    public ExpressionParser(C calculator) {
        List<Map<String, BinaryFactory<T>>> BINARY_OPERATORS = List.of(
                Map.of("+", Add::new, "-", Subtract::new),
                Map.of("*", Multiply::new, "/", Divide::new),
                Map.of("mod", Modulo::new)
        );
        Map<String, UnaryFactory<T>> UNARY_OPERATORS = Map.of(
                "-", Negate::new, "abs", Abs::new, "square", Square::new
        );
        List<String> VARIABLES = List.of(
                "x", "y", "z"
        );
        Map<Character, Character> BRACKETS = Map.of(
                '(', ')'
        );

        initialize(BINARY_OPERATORS, UNARY_OPERATORS, VARIABLES, BRACKETS, calculator);
    }

    @Override
    public Expression<T> parse(String expression) throws ParserException {
        setSource(new StringSource(expression));

        return parseExpression();
    }
}