"use strict";

const varIndexes = {
    'x': 0,
    'y': 1,
    'z': 2
}

const AbstractExpression = {
    abstractPrototype: {
        evaluate: function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
        diff: function (variable) { return this.differ(variable, ...this.operands, ...(this.operands.map(e => e.diff(variable)))) },
        prefix() { return this.toString(); },
        postfix() { return this.toString(); },
        toString: function () { return this.operands[0].toString() }
    },
    init: (...params) => {
        function Operator(...operands) {
            this.operands = operands
        }
        Operator.prototype = Object.create(AbstractExpression.abstractPrototype)
        params.forEach(e => Operator.prototype[e.name] = e)
        return Operator
    }
}

const Const = AbstractExpression.init( function evaluate() { return this.operands[0]; },
    function diff() { return Const.zero; }
)
Const.one = new Const(1)
Const.zero = new Const(0)
const Variable = AbstractExpression.init(function evaluate(...vars) { return vars[varIndexes[this.operands[0]]]; },
    function diff(variable) { return this.operands[0] === variable ? Const.one : Const.zero }
)

function abstractOperation(symbol, operateFun, differFun) {
    return AbstractExpression.init(
        function operate(...args) { return operateFun(...args); },
        function differ(variable, ...args) { return differFun(variable, ...args); },
        function prefix() { return "(" + [].concat(symbol).concat(this.operands.map(e => e.prefix())).join(" ") + ")" },
        function postfix() { return "(" + [].concat(this.operands.map(e => e.postfix())).concat(symbol).join(" ") + ")"},
        function toString() { return this.operands.concat(symbol).join(" "); })
}

const Add = abstractOperation("+", (x, y) => x + y, (d, x, y) => new Add(x.diff(d), y.diff(d)))
const Subtract = abstractOperation("-", (x, y) => x - y, (d, x, y) => new Subtract(x.diff(d), y.diff(d)));
const Multiply = abstractOperation("*", (x, y) => x * y, (d, x, y) => new Add(new Multiply(x.diff(d), y), new Multiply(x, y.diff(d))))
const Divide =   abstractOperation("/", (x, y) => x / y, (d, x, y) => new Divide(new Subtract(new Multiply(x.diff(d), y), new Multiply(y.diff(d), x)), new Multiply(y, y)))
const Hypot =    abstractOperation("hypot", (x, y) => x * x + y * y, (d, x, y) => new Add(new Multiply(x, x), new Multiply(y, y)).diff(d))
const HMean =    abstractOperation("hmean", (x, y) => 2 / (1 / x + 1 / y), (d, x, y) => new Divide(new Const(2), new Add(new Divide(Const.one, x), new Divide(Const.one, y))).diff(d))
const Negate =   abstractOperation("negate", x => -x, (d, x) => new Negate(x.diff(d)));
const ArithMean = abstractOperation("arith-mean", (...args) => args.reduce((sum, summoned) => sum + summoned, 0) / args.length, );
const GeomMean = abstractOperation("geom-mean", (...args) => Math.pow(Math.abs(args.reduce((prod, multiplier) => prod * multiplier, 1)), 1 / args.length), ); // toDo
const HarmMean = abstractOperation("harm-mean", (...args) => args.length / args.reduce((sum, summoned) => sum + 1 / summoned, 0), );

const operators = {
    '+': [Add, 2],
    '-': [Subtract, 2],
    '*': [Multiply, 2],
    '/': [Divide, 2],
    'negate': [Negate, 1],
    'hypot': [Hypot, 2],
    'hmean': [HMean, 2],
    'arith-mean': [ArithMean, Infinity],
    'geom-mean': [GeomMean, Infinity],
    'harm-mean': [HarmMean, Infinity]
}

const parse = input => {
    return input.split(" ").filter(e => e !== "").reduce((stack, token) => {
        let top
        if (token in operators) {
            let operator = operators[token]
            top = new operator[0](...stack.splice(stack.length - operator[1]))
        } else if (token in varIndexes) {
            top = new Variable(token)
        } else {
            top = new Const(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}

// ex = (@ <ex> <ex> <ex> ...)
// ex = (<ex> <ex> <ex> ... @)
// ex = const
// ex = variable
function parser(input) {
    let source = {
        EOF: 0,
        index: 0,
        input: input.replace(/[(]/g, " ( ").replace(/[)]/g, " ) ").split(" ").filter(e => e !== ""),
        hasNext: function () { return this.index < this.input.length },
        current: function () { return this.input[this.index] },
        next: function () { return this.hasNext() ? this.input[this.index++] : this.EOF },
        test: function (expected) {
            if (expected === this.current()) {
                this.next();
                return true;
            } else {
                return false;
            }
        },
        expect: function (expected) {
            if (expected === this.current()) {
                this.next();
            } else {
                throw new Error("mismatch exception ");
            }
        }
    }

    return {
        parseExpression: function (mode) {
            let parsed
            if (source.test('(')) {
                parsed = this.parse(mode);
                source.expect(')');
            } else {
                parsed = this.parseOperands()[0];
            }

            if (source.hasNext()) {
                throw new Error("Unexpected symbols"); // toDo
            }

            return parsed;
        },
        parse: function (mode) {
            let operator
            let operands
            if (mode === "prefix") {
                operator = this.parseOperator(mode);
                operands = this.parseOperands(mode);
            } else if (mode === "postfix") {
                operands = this.parseOperands(mode);
                operator = this.parseOperator(mode);
            } else {
                throw new Error("Illegal mode"); // toDo
            }

            if (operator[1] !== Infinity && operator[1] !== operands.length) {
                throw new Error("operands mismatch exception"); // toDO
            } else {
                return new operator[0](...operands);
            }
        },
        parseOperator: () => {
            if (source.current() in operators) {
                return operators[source.next()]
            } else {
                throw new Error("no such operator") // TODO;
            }
        },
        parseOperands: function (mode) {
            let operands = []
            while (true) {
                if (isFinite(source.current())) {
                    operands.push(new Const(Number(source.next())));
                } else if (source.current() in varIndexes) {
                    operands.push(new Variable(source.next()));
                } else if (source.test('(')) {
                    operands.push(this.parse(mode));
                    source.expect(')');
                } else {
                    break;
                }
            }

            return operands
        }
    }
}

function parsePrefix(input) {
    return parser(input).parseExpression("prefix");
}

function parsePostfix(input) {
    return parser(input).parseExpression("postfix");
}