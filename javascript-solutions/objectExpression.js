"use strict";

const varIndexes = {
    'x': 0,
    'y': 1,
    'z': 2
}

const AbstractExpression = {
    abstractPrototype: {
        evaluate: function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
        diff: function (variable) { return this.differ(variable, ...this.operands); },
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

const Const = AbstractExpression.init(
    function evaluate() { return this.operands[0]; },
    function diff() { return Const.zero; }
)
Const.one = new Const(1)
Const.zero = new Const(0)
const Variable = AbstractExpression.init(
    function evaluate(...vars) { return vars[varIndexes[this.operands[0]]]; },
    function diff(variable) { return this.operands[0] === variable ? Const.one : Const.zero }
)

function abstractOperation(symbol, operateFun, differFun) {
    return AbstractExpression.init(
        function operate(...args) { return operateFun(...args); },
        function differ(variable, ...args) { return differFun(variable, ...(args.map(e => [e, e.diff(variable)]))); },
        function prefix() { return "(" + [].concat(symbol).concat(this.operands.map(e => e.prefix())).join(" ") + ")" },
        function postfix() { return "(" + [].concat(this.operands.map(e => e.postfix())).concat(symbol).join(" ") + ")"},
        function toString() { return this.operands.concat(symbol).join(" "); })
}

const Add       = abstractOperation("+", (x, y) => x + y, (d, x, y) => new Add(x[1], y[1]));
const Subtract  = abstractOperation("-", (x, y) => x - y, (d, x, y) => new Subtract(x[1], y[1]));
const Multiply  = abstractOperation("*", (x, y) => x * y, (d, x, y) => new Add(new Multiply(x[1], y[0]), new Multiply(x[0], y[1])));
const Divide    = abstractOperation("/", (x, y) => x / y, (d, x, y) => new Divide(new Subtract(new Multiply(x[1], y[0]), new Multiply(y[1], x[0])), new Multiply(y[0], y[0])));
const Hypot     = abstractOperation("hypot", (x, y) => x * x + y * y, (d, x, y) => new Add(new Multiply(x[0], x[0]), new Multiply(y[0], y[0])).diff(d));
const HMean     = abstractOperation("hmean", (x, y) => 2 / (1 / x + 1 / y), (d, x, y) => new Divide(new Const(2), new Add(new Divide(Const.one, x[0]), new Divide(Const.one, y[0]))).diff(d));
const Negate    = abstractOperation("negate", x => -x, (d, x) => new Negate(x[1]));
const Abs       = abstractOperation("abs", x => Math.abs(x), (d, x) => new Multiply(new Sign(x[0]), x[1]));
const Sign      = abstractOperation("sign", x => Math.sign(x), (d, x) => Const.zero);
const Log       = abstractOperation("log", x => Math.log(x), (d, x) => new Divide(x[1], x[0]));
const Pow       = abstractOperation("^", (x, y) => Math.pow(x, y),
    (d, x, y) => new Multiply(new Pow(x[0], new Subtract(y[0], Const.one)), new Add(new Multiply(y[0], x[1]), new Multiply(x[0], new Multiply(new Log(x[0]), y[1])))));
const ArithMean = abstractOperation("arith-mean", (...args) => args.reduce((sum, term) => sum + term, 0) / args.length,
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)), args.reduce((sum, term) => new Add(sum, term[1]), Const.zero)));
const GeomMean  = abstractOperation("geom-mean", (...args) => Math.pow(Math.abs(args.reduce((prod, term) => prod * term, 1)), 1 / args.length),
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)), new Multiply(new Pow(new GeomMean(...args.map(e => e[0])), new Subtract(Const.one, new Const(args.length))) ,
        new Abs(args.reduce((prod, multiplier) => new Multiply(prod, multiplier[0]), Const.one)).diff(d))));
const HarmMean  = abstractOperation("harm-mean", (...args) => args.length / args.reduce((sum, term) => sum + 1 / term, 0),
    (d, ...args) => new Multiply(new Const(args.length), new Divide(args.reduce((sum, term) => new Add(sum, new Divide(term[1], new Multiply(term[0], term[0]))), Const.zero),
        new Pow(args.reduce((sum, term) => new Add(sum, new Divide(Const.one, term[0])), Const.zero), new Const(2)))));

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
// ex = const | variable
function parser(input) {
    let source = {
        EOF: 0,
        pointer: 0,
        input: input.replace(/[(]/g, " ( ").replace(/[)]/g, " ) ").split(" ").filter(e => e !== ""),
        current: function () { return this.input[this.pointer] },
        hasNext: function () { return this.pointer < this.input.length },
        next: function () { return this.hasNext() ? this.input[this.pointer++] : this.EOF },
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
                throw new Error("mismatch exception "); // toDo
            }
        },
        getErrorPos: function () {
            return this.input.join(' ')
        }
    }

    return {
        parse: function (mode) {
            let parsed
            if (source.test('(')) {
                parsed = this.parseExpression(mode);
                source.expect(')');
            } else {
                parsed = this.parseOperands()[0];
            }

            if (source.hasNext()) {
                throw new Error("Unexpected symbols"); // toDo
            } else {
                return parsed;
            }
        },
        parseExpression: function (mode) {
            let operator, operands
            switch (mode) {
                case "prefix":
                    operator = this.parseOperator(mode);
                    operands = this.parseOperands(mode);
                    break;
                case "postfix":
                    operands = this.parseOperands(mode);
                    operator = this.parseOperator(mode);
                    break;
                default:
                    throw new Error("Illegal mode");
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
            while (source.hasNext()) if (isFinite(source.current())) {
                operands.push(new Const(Number(source.next())));
            } else if (source.current() in varIndexes) {
                operands.push(new Variable(source.next()));
            } else if (source.test('(')) {
                operands.push(this.parseExpression(mode));
                source.expect(')');
            } else {
                break;
            }

            if (operands.length === 0) {
                throw new Error("no operands found");
            }

            return operands
        }
    }
}

function parsePrefix(input) {
    return parser(input).parse("prefix");
}

function parsePostfix(input) {
    return parser(input).parse("postfix");
}