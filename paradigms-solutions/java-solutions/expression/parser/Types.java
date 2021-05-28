package expression.parser;

import expression.exceptions.InvalidConstValue;
import expression.exceptions.InvalidOperatorException;
import expression.exceptions.InvalidVariableException;
import expression.exceptions.ParserException;

import java.util.function.Function;

enum Types {
    VARIABLE("variable", InvalidVariableException::new),
    UNARY("unary operator", InvalidOperatorException::new),
    BINARY("binary operator", InvalidOperatorException::new),
    CONST("constant", InvalidConstValue::new);

    Types(String expected, Function<String, ParserException> factory) {
        this.factory = factory;
        this.expected = expected;
    }

    private final String expected;
    private final Function<String, ParserException> factory;

    public Function<String, ParserException> instanceException() {
        return factory;
    }

    public String getExpected() {
        return expected;
    }
}