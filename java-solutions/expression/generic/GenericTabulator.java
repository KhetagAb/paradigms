package expression.generic;

import expression.Expression;
import expression.exceptions.ExpressionException;
import expression.exceptions.ParserException;
import expression.parser.ExpressionParser;

public class GenericTabulator implements Tabulator {
    
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParserException {
        Calculator<?> calculator;

        switch (mode) {
            case "i":
                calculator = new CheckedIntegerCalculator();
                break;
            case "d":
                calculator = new DoubleCalculator();
                break;
            case "bi":
                calculator = new BigIntegerCalculator();
                break;
            case "u":
                calculator = new IntegerCalculator();
                break;
            case "b":
                calculator = new ByteCalculator();
                break;
            case "p":
                calculator = new PCalculator();
                break;
            default:
                throw new IllegalStateException("Unknown mode: " + mode);
        }

        Expression<?> ex = new ExpressionParser<>(calculator).parse(expression);
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int i = 0; x1 + i <= x2; i++) {
            for (int j = 0; y1 + j <= y2; j++) {
                for (int k = 0; z1 + k <= z2; k++) {
                    try {
                        result[i][j][k] = ex.evaluate( x1 + i, y1 + j, z1 + k);
                    } catch (ArithmeticException | ExpressionException e) {
                        result[i][j][k] = null;
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalStateException("There are should be 2 parameters: <mode> <expression>");
        }

        String mode = args[0], expression = args[1];
        if ((!mode.equals("-u") && !mode.equals("-b") && !mode.equals("-p") && !mode.equals("-i") && !mode.equals("-d") && !mode.equals("-bi")) || expression == null) {
            throw new IllegalStateException("Invalid parameters found. Available modes [-i, -d, -bi]. Non-null expression required");
        } else {
            mode = mode.substring(1);
        }

        Tabulator tab = new GenericTabulator();
        int x1 = -2, x2 = 2, y1 = -2, y2 = 2, z1 = -2, z2 = 2;
        Object[][][] result;

        try {
            result = tab.tabulate(mode, expression, x1, x2, y1, y2, z1, z2);

            for (int i = 0; x1 + i <= x2; i++) {
                for (int j = 0; y1 + j <= y2; j++) {
                    for (int k = 0; z1 + k <= z2; k++) {
                        System.out.printf("(x = %d, y = %d, z = %d) -> %s%n", (x1 + i), (y1 + j), (z1 + k), result[i][j][k]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
