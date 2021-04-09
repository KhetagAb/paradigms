"use strict";

const varIndexes = {
    'x': 0,
    'y': 1,
    'z': 2
}

function tokenFactory(constructor, evaluate, diff, toString) {
    constructor.prototype = {
        evaluate: evaluate,
        diff: diff,
        toString: toString,
        constructor: constructor
    }

    return constructor
}

const Const = tokenFactory(
    function (value) { this.value = value },
    function () { return this.value },
    () => Const.zero,
    function () { return this.value.toString() })
const Variable = tokenFactory(
    function (variable) { this.index = varIndexes[variable]; this.value = variable;  },
    function (...vars) { return vars[this.index] },
    function (variable) { return this.value === variable ? Const.one : Const.zero },
    function () { return this.value.toString() })
const Operator =  tokenFactory(
    function (...operands) { this.operands = operands },
    function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
    function (variable) { return this.differ(variable)(...this.operands) },
    function () { return this.operands.concat(this.symbol).join(" ") })

Const.one = new Const(1)
Const.zero = new Const(0)

function operatorFactory(symbol, operate, differImpl) {
    const operator = function (...operands) {
        Operator.call(this, ...operands) // можно ли от этого избавиться через абстракцию?
    }
    operator.prototype = Object.create(Operator.prototype); // вот тут без
    operator.prototype.symbol = symbol;
    operator.prototype.operate = operate;
    operator.prototype.differ = d => (...args) => differImpl(d, ...args, ...(args.map(e => e.diff(d))))
    operator.prototype.constructor = operator;
    return operator;
}

const Add =     operatorFactory("+", (x, y) => x + y, (d, x, y, dx, dy) => new Add(dx, dy));
const Subtract = operatorFactory("-", (x, y) => x - y, (d, x, y, dx, dy) => new Subtract(dx, dy));
const Multiply = operatorFactory("*", (x, y) => x * y, (d, x, y, dx, dy) => new Add(new Multiply(dx, y), new Multiply(x, dy)))
const Divide =  operatorFactory("/", (x, y) => x / y, (d, x, y, dx, dy) => new Divide(new Subtract(new Multiply(dx, y), new Multiply(dy, x)), new Multiply(y, y)))
const Hypot =   operatorFactory("hypot", (x, y) => x * x + y * y, (d, x, y, dx, dy) => new Add(new Multiply(x, x), new Multiply(y, y)).diff(d))
const HMean =   operatorFactory("hmean", (x, y) => 2 / (1 / x + 1 / y), (d, x, y, dx, dy) => new Divide(new Const(2), new Add(new Divide(Const.one, x), new Divide(Const.one, y))).diff(d))
const Negate =  operatorFactory("negate", x => -x, (d, x, dx) => new Negate(dx));

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
            top = new operator(...stack.splice(stack.length - operator.prototype.operate.length))
        } else if (token in varIndexes) {
            top = new Variable(token)
        } else {
            top = new Const(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}