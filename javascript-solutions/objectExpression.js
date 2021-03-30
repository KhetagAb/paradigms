"use strict";

function Const(val) {
    const value = val
    return {
        evaluate:   () => value,
        diff:       () => new Const(0),
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
        diff:       (variable) => varIndexes[variable] === index ? new Const(1) : new Const(0),
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