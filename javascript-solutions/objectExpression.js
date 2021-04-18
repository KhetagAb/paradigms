"use strict";

const varIndexes = { 'x': 0, 'y': 1, 'z': 2 }
const Operation = {
    evaluate: function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
    diff: function (variable) { return this.differ(variable, ...(this.operands.map(e => [e, e.diff(variable)]))); },
    prefix: function () { return "(" + [].concat(this.symbol).concat(this.operands.map(e => e.prefix())).join(" ") + ")" },
    postfix: function () { return "(" + [].concat(this.operands.map(e => e.postfix())).concat(this.symbol).join(" ") + ")"},
    toString: function () { return this.operands.concat(this.symbol).join(" "); }
}

function createOperation(symbol, operate, differ) {
    const constructor = function (...operands) {
        this.operands = operands;
    }
    constructor.prototype = Object.create(Operation);
    constructor.prototype.symbol  = symbol;
    constructor.prototype.operate = operate;
    constructor.prototype.differ = differ;
    return constructor;
}

const NoArityOperation = {
    prefix() { return this.toString(); },
    postfix() { return this.toString(); },
    toString: function () { return this.value.toString() }
}

function createNoArityOperation(evaluate, diff) {
    const constructor = function (value) {
        this.value = value;
    }
    constructor.prototype = Object.create(NoArityOperation);
    constructor.prototype.evaluate  = evaluate;
    constructor.prototype.diff = diff;
    return constructor;
}

const Const = createNoArityOperation(function () { return this.value; }, () => Const.zero)
Const.one = new Const(1)
Const.zero = new Const(0)
const Variable = createNoArityOperation(function (...vars) { return vars[varIndexes[this.value]]; },
    function(variable) { return this.value === variable ? Const.one : Const.zero })

const Add       = createOperation("+", (x, y) => x + y, (d, x, y) => new Add(x[1], y[1]));
const Subtract  = createOperation("-", (x, y) => x - y, (d, x, y) => new Subtract(x[1], y[1]));
const Multiply  = createOperation("*", (x, y) => x * y, (d, x, y) => new Add(new Multiply(x[1], y[0]), new Multiply(x[0], y[1])));
const Divide    = createOperation("/", (x, y) => x / y, (d, x, y) => new Divide(new Subtract(new Multiply(x[1], y[0]), new Multiply(y[1], x[0])), new Multiply(y[0], y[0])));
const Hypot     = createOperation("hypot", (x, y) => x * x + y * y, (d, x, y) => new Add(new Multiply(x[0], x[0]), new Multiply(y[0], y[0])).diff(d));
const HMean     = createOperation("hmean", (x, y) => 2 / (1 / x + 1 / y), (d, x, y) => new Divide(new Const(2), new Add(new Divide(Const.one, x[0]), new Divide(Const.one, y[0]))).diff(d));
const Negate    = createOperation("negate", x => -x, (d, x) => new Negate(x[1]));
const Abs       = createOperation("abs", x => Math.abs(x), (d, x) => new Multiply(new Sign(x[0]), x[1]));
const Sign      = createOperation("sign", x => Math.sign(x), (d, x) => Const.zero);
const Log       = createOperation("log", x => Math.log(x), (d, x) => new Divide(x[1], x[0]));
const Pow       = createOperation("^", (x, y) => Math.pow(x, y),
    (d, x, y) => new Multiply(new Pow(x[0], new Subtract(y[0], Const.one)), new Add(new Multiply(y[0], x[1]), new Multiply(x[0], new Multiply(new Log(x[0]), y[1])))));
const ArithMean = createOperation("arith-mean", (...args) => args.reduce((sum, term) => sum + term, 0) / args.length,
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)), args.reduce((sum, term) => new Add(sum, term[1]), Const.zero)));
const GeomMean  = createOperation("geom-mean", (...args) => Math.pow(Math.abs(args.reduce((prod, term) => prod * term, 1)), 1 / args.length),
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)), new Multiply(new Pow(new GeomMean(...args.map(e => e[0])), new Subtract(Const.one, new Const(args.length))) ,
        new Abs(args.reduce((prod, multiplier) => new Multiply(prod, multiplier[0]), Const.one)).diff(d))));
const HarmMean  = createOperation("harm-mean", (...args) => args.length / args.reduce((sum, term) => sum + 1 / term, 0),
    (d, ...args) => new Multiply(new Const(args.length), new Divide(args.reduce((sum, term) => new Add(sum, new Divide(term[1], new Multiply(term[0], term[0]))), Const.zero),
        new Pow(args.reduce((sum, term) => new Add(sum, new Divide(Const.one, term[0])), Const.zero), new Const(2)))));

const operators = {
    '+': Add, '-': Subtract, '*': Multiply, '/': Divide, 'negate': Negate,
    'hypot': Hypot, 'hmean': HMean,
    'arith-mean': ArithMean, 'geom-mean': GeomMean, 'harm-mean': HarmMean
}

const parse = input => {
    return input.split(" ").filter(e => e !== "").reduce((stack, token) => {
        let top
        if (token in operators) {
            let operator = operators[token]
            top = new operator(...stack.splice(stack.length - operator.prototype.operate.length))
        } else if (token in varIndexes) {
            top = new Variable(token)
        } else {
            top = new Const(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}

class ParserError extends Error {
    constructor(message) {
        super(message);
        this.name = "ParserError";
    }
}

// ex = (@ <ex> <ex> <ex> ...)
// ex = (<ex> <ex> <ex> ... @)
// ex = const | variable
function parser(input, mode) {
    const source = {
        pointer: 0,
        input: input.replace(/[(]/g, " ( ").replace(/[)]/g, " ) ").split(" ").filter(e => e !== ""),
        current: function () { return this.input[this.pointer] },
        next: function () { return this.pointer < this.input.length ? this.input[this.pointer++] : this.current() },
        isEOF: function () { return this.pointer === this.input.length },
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
                throw new ParserError(`Mismatch exception: expect '${expected}'.\n` + this.getErrorPos());
            }
        },
        getErrorPos: function () {
            let errorIndex = this.input.slice(0, this.pointer).reduce((sum, token) => sum + token.length + 1, 0)
            return this.input.join(' ') + '\n' +
                '-'.repeat(errorIndex) + "^";
        }
    }

    const parser = {
        parse: () => {
            let parsed
            if (source.test('(')) {
                parsed = parser.parseExpression();
                source.expect(')');
            } else {
                parsed = parser.parseOperands();
                if (parsed.length > 1) {
                    source.pointer = 1;
                } else {
                    parsed = parsed[0]
                }
            }

            if (!source.isEOF()) {
                parser.err("Unexpected symbols");
            } else {
                return parsed;
            }
        },
        parseExpression: function () {
            let parsed = this.parseIn.map(e => this.parsingOrder[e]())
            let operator = parsed[this.parseIn[0]], operands = parsed[this.parseIn[1]]
            const operatorLen = operator.prototype.operate.length;
            if (operatorLen !== 0 && operatorLen !== operands.length) {
                source.pointer--;
                this.err("Operands arity mismatch"); // toDO
            } else {
                return new operator(...operands);
            }
        },
        parseOperator: () => {
            if (source.current() in operators) {
                return operators[source.next()]
            } else {
                parser.err(`Illegal operator found: '${source.current()}'`)
            }
        },
        parseOperands: () => {
            let operands = []
            while (!source.isEOF()) if (isFinite(source.current())) {
                operands.push(new Const(Number(source.next())));
            } else if (source.current() in varIndexes) {
                operands.push(new Variable(source.next()));
            } else if (source.test('(')) {
                operands.push(parser.parseExpression());
                source.expect(')');
            } else {
                break;
            }

            if (operands.length === 0) {
                this.err('No operands found');
            }

            return operands
        },
        err: (message) => {
            throw new ParserError(message + '.\n' + source.getErrorPos());
        }
    }

    parser.parsingOrder = [parser.parseOperator, parser.parseOperands];
    switch (mode) {
        case "prefix":
            parser.parseIn = [0, 1];
            break;
        case "postfix":
            parser.parseIn = [1, 0];
            break;
        default:
            parser.err(`Illegal parsing mode: ${mode}`);
    }

    return parser;
}

function parsePrefix(input) {
    return parser(input, "prefix").parse();
}

function parsePostfix(input) {
    return parser(input, "postfix").parse();
}

let ex = parsePostfix('((x negate) 2 /)')