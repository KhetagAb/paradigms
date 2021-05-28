"use strict";

const variableIndexes = { 'x': 0, 'y': 1, 'z': 2 }
const Operation = {
    evaluate: function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
    diff: function (variable) { return this.diffImpl(variable, ...(this.operands.map(e => [e, e.diff(variable)]))); },
    prefix: function () { return "(" + [].concat(this.symbol).concat(this.operands.map(e => e.prefix())).join(" ") + ")" },
    postfix: function () { return "(" + [].concat(this.operands.map(e => e.postfix())).concat(this.symbol).join(" ") + ")"},
    toString: function () { return this.operands.concat(this.symbol).join(" "); }
}

function createOperation(symbol, operate, diffImpl) {
    const constructor = function (...operands) {
        this.operands = operands;
    }
    constructor.prototype = Object.create(Operation);
    constructor.prototype.symbol  = symbol;
    constructor.prototype.operate = operate;
    constructor.prototype.diffImpl = diffImpl;
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
        this.index = variableIndexes[value];
    }
    constructor.prototype = Object.create(NoArityOperation);
    constructor.prototype.evaluate  = evaluate;
    constructor.prototype.diff = diff;
    return constructor;
}

const Const = createNoArityOperation(function () { return this.value; }, () => Const.zero)
Const.one = new Const(1)
Const.zero = new Const(0)
const Variable = createNoArityOperation(function (...vars) { return vars[this.index]; },
    function(variable) { return this.value === variable ? Const.one : Const.zero })

const Add = createOperation(
    "+",
    (x, y) => x + y,
    (d, x, y) => new Add(x[1], y[1]));
const Subtract = createOperation(
    "-",
    (x, y) => x - y,
    (d, x, y) => new Subtract(x[1], y[1]));
const Multiply = createOperation(
    "*",
    (x, y) => x * y,
    (d, x, y) => new Add(new Multiply(x[1], y[0]), new Multiply(x[0], y[1])));
const Divide = createOperation(
    "/",
    (x, y) => x / y,
    (d, x, y) => new Divide(new Subtract(new Multiply(x[1], y[0]), new Multiply(y[1], x[0])), new Multiply(y[0], y[0])));
const Hypot     = createOperation(
    "hypot",
    (x, y) => x * x + y * y,
    (d, x, y) => new Add(new Multiply(new Const(2), new Multiply(x[0], x[1])),
        new Multiply(new Const(2), new Multiply(y[0], y[1]))))
const HMean     = createOperation(
    "hmean",
    (x, y) => 2 / (1 / x + 1 / y),
    (d, x, y) => new Divide(new Const(2), new Add(new Divide(Const.one, x[0]), new Divide(Const.one, y[0]))).diff(d));
const Negate = createOperation(
    "negate",
    x => -x,
    (d, x) => new Negate(x[1]));
const Abs = createOperation(
    "abs",
    x => Math.abs(x),
    (d, x) => new Multiply(new Sign(x[0]), x[1]));
const Sign = createOperation(
    "sign",
    x => Math.sign(x),
    () => Const.zero);
const Log = createOperation(
    "log",
    x => Math.log(x),
    (d, x) => new Divide(x[1], x[0]));
const Pow  = createOperation(
    "^",
    (x, y) => Math.pow(x, y),
    (d, x, y) => new Multiply(new Pow(x[0], new Subtract(y[0], Const.one)),
        new Add(new Multiply(y[0], x[1]), new Multiply(x[0], new Multiply(new Log(x[0]), y[1])))));
const ArithMean = createOperation(
    "arith-mean",
    (...args) => args.reduce((sum, term) => sum + term, 0) / args.length,
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)),
        args.reduce((sum, term) => new Add(sum, term[1]), Const.zero)));
const GeomMean  = createOperation(
    "geom-mean",
    (...args) => Math.pow(Math.abs(args.reduce((prod, term) => prod * term, 1)), 1 / args.length),
    (d, ...args) => new Multiply(new Divide(Const.one, new Const(args.length)),
        new Multiply(new Pow(new GeomMean(...args.map(e => e[0])), new Subtract(Const.one, new Const(args.length))) ,
        new Abs(args.reduce((prod, multiplier) => new Multiply(prod, multiplier[0]), Const.one)).diff(d))));
const HarmMean  = createOperation(
    "harm-mean",
    (...args) => args.length / args.reduce((sum, term) => sum + 1 / term, 0),
    (d, ...args) => new Multiply(new Const(args.length),
        new Divide(args.reduce((sum, term) => new Add(sum, new Divide(term[1], new Multiply(term[0], term[0]))), Const.zero),
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
        } else if (token in variableIndexes) {
            top = new Variable(token)
        } else {
            top = new Const(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}

function ParserErrorFactory(parent) {
    const ParserError = function (errorMessage, pos, input) {
        this.pos = pos;
        this.input = input;
        this.message = `Parser error at pos ${this.pos + 1}: ${errorMessage}.\n${this.input.join(' ')} \n${'-'.repeat(this.pos)}^\n`;;
    }
    ParserError.prototype = Object.create(parent.prototype);
    ParserError.prototype.name = "ParserError";
    ParserError.prototype.constructor = ParserError;
    return ParserError;
}

const ParserError = ParserErrorFactory(Error);

class ArityMismatchException extends ParserError {
    constructor(errorMessage, pos, input) {
        super(errorMessage, pos, input);
    }
}

class MismatchException extends ParserError {
    constructor(errorMessage, pos, input) {
        super(errorMessage, pos, input);
    }
}

class OperandsMismatchException extends MismatchException {
    constructor(errorMessage, pos, input) {
        super(errorMessage, pos, input);
    }
}

const charSource = function (input) {
    return {
        pointer: 0,
        input: input.replace(/[(]/g, " ( ").replace(/[)]/g, " ) ").split(" ").filter(e => e !== ""),
        current: function () { return this.input[this.pointer] },
        next: function () { return this.input[this.pointer++] },
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
                throw new MismatchException(`Mismatch exception: expect '${expected}'.\n`, this.getErrorPos(), this.input);
            }
        },
        getErrorPos: function (delta = 0) { return this.input.slice(0, this.pointer += delta).reduce((sum, token) => sum + token.length + 1, 0); }
    }
}

// ex = (@ <ex> <ex> <ex> ...), ex = (<ex> <ex> <ex> ... @)
// ex = const | variable
function parser(source, mode) {
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
            return (source.isEOF()) ? parsed : parser.err(MismatchException,"Unexpected symbols");
        },
        parseExpression: function () {
            let parsed = this.parseIn.map(e => this.parsingOrder[e]())
            let operator = parsed[this.parseIn[0]], operands = parsed[this.parseIn[1]]
            const operatorLen = operator.prototype.operate.length;
            if (operatorLen === 0 || operatorLen === operands.length) {
                return new operator(...operands);
            } else {
                return parser.err(ArityMismatchException,
                    `Operands arity mismatch: required ${operatorLen}, but found ${operands.length}`, -1);
            }
        },
        parseOperator: () => {
            if (source.current() in operators) {
                return operators[source.next()];
            } else {
                return parser.err(ParserError, `Unknown operator found: '${source.current()}'`);
            }
        },
        parseOperands: () => {
            let operands = []
            while (!source.isEOF()) if (isFinite(source.current())) {
                operands.push(new Const(Number(source.next())));
            } else if (source.current() in variableIndexes) {
                operands.push(new Variable(source.next()));
            } else if (source.test('(')) {
                operands.push(parser.parseExpression());
                source.expect(')');
            } else {
                break;
            }
            return operands.length === 0 ? parser.err(OperandsMismatchException, 'No operands found') : operands;
        },
        err: (constructor, message, tokenDelta) => { throw new constructor(message, source.getErrorPos(tokenDelta), source.input); }
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
            parser.err(ParserError,`Illegal parsing mode: ${mode}`);
    }

    return parser;
}

const parseMode = (mode, input) => parser(charSource(input), mode).parse()
function parsePrefix(input) { return parseMode("prefix", input); }
function parsePostfix(input) { return parseMode("postfix", input); }