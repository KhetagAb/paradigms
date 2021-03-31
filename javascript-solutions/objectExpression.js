"use strict";

const zero = new Const(0)
const one = new Const(1)

function Const(val) {
    const value = val
    return {
        evaluate:   () => value,
        diff:       () => zero,
        toString:   () => value.toString()
    }
}

const varIndexes = {
    'x': 0,
    'y': 1,
    'z': 2
}

function Variable(name) {
    const index = varIndexes[name]
    return {
        evaluate:   (...vars) => vars[index],
        diff:       (variable) => varIndexes[variable] === index ? one : zero,
        toString:   () => name
    }
}

function Operator(...operands) {
    this.operands = operands
}
Operator.prototype = {
    evaluate:   function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
    diff:       function (variable) { return this.differ(variable)(...this.operands) },
    toString:   function () { return this.operands.concat(this.symbol).join(" ") }
}

function operatorPrototypeInit(constructor, symbol, operate, differ) {
    constructor.prototype = Object.create(Operator.prototype);
    constructor.prototype.symbol = symbol
    constructor.prototype.operate = operate
    constructor.prototype.differ = differ
    constructor.prototype.constructor = constructor
}

function Add(left, right) {
    Operator.call(this, left, right);
}
operatorPrototypeInit(Add,
    "+",
    (x, y) => x + y,
    d => (x, y) => new Add(x.diff(d), y.diff(d)));

function Subtract(left, right) {
    Operator.call(this, left, right);
}
operatorPrototypeInit(Subtract,
    "-",
    (x, y) => x - y,
    d => (x, y) => new Subtract(x.diff(d), y.diff(d)));

function Multiply(left, right) {
    Operator.call(this, left, right);
}
operatorPrototypeInit(Multiply,
    "*",
    (x, y) => x * y,
    d => (x, y) => new Add(new Multiply(x.diff(d), y), new Multiply(x, y.diff(d))))

function Divide(left, right) {
    Operator.call(this, left, right);
}
operatorPrototypeInit(Divide,
    "/",
    (x, y) => x / y,
    d => (x, y) => new Divide(new Subtract(new Multiply(x.diff(d), y), new Multiply(y.diff(d), x)), new Multiply(y, y)))

function Hypot(left, right) {
    Operator.call(this, left, right)
}
operatorPrototypeInit(Hypot,
    "hypot",
    (x, y) => x * x + y * y,
    d => (x, y) => new Add(new Multiply(x, x), new Multiply(y, y)).diff(d)
)

function HMean(left, right) {
    Operator.call(this, left, right)
}4
operatorPrototypeInit(HMean,
    "hmean",
    (x, y) => 2 / (1 / x + 1 / y),
    d => (x, y) => new Divide(new Const(2), new Add(new Divide(one, x), new Divide(one, y))).diff(d)
)

function Negate(value) {
    Operator.call(this, value);
}
operatorPrototypeInit(Negate,
    "negate",
    x => -x,
    d => x => new Negate(x.diff(d)));

const operators = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'hypot': Hypot,
    'hmean': HMean
}

const parse = input => {
    return input.split(" ").filter(e => e !== "").reduce((stack, token) => {
        let top
        if (token in operators) {
            let operator = operators[token]
            top = new operator(...stack.splice(stack.length - operator.length))
        } else if (token in varIndexes) {
            top = new Variable(token)
        } else {
            top = new Const(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}