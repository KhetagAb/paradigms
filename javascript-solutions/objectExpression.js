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
    () => zero,
    function () { return this.value.toString() })
const Variable = tokenFactory(
    function (variable) { this.index = varIndexes[variable]; this.value = variable;  },
    function (...vars) { return vars[this.index] },
    function (variable) { return this.value === variable ? one : zero },
    function () { return this.value.toString() })
const Operator =  tokenFactory(
    function (...operands) { this.operands = operands },
    function (...vars) { return this.operate(...this.operands.map(ex => ex.evaluate(...vars))) },
    function (variable) { return this.differ(variable)(...this.operands) },
    function () { return this.operands.concat(this.symbol).join(" ") })

const one = new Const(1)
const zero = new Const(0)

function operatorFactory(symbol, operate, differ) {
    const operator = function (...operands) {
        Operator.call(this, ...operands)
    }
    operator.prototype = Object.create(Operator.prototype);
    operator.prototype.symbol = symbol;
    operator.prototype.operate = operate;
    operator.prototype.differ = differ
    operator.prototype.constructor = operator;
    return operator;
}

const Add =     operatorFactory("+", (x, y) => x + y, d => (x, y) => new Add(x.diff(d), y.diff(d)));
const Subtract = operatorFactory("-", (x, y) => x - y, d => (x, y) => new Subtract(x.diff(d), y.diff(d)));
const Multiply = operatorFactory("*", (x, y) => x * y, d => (x, y) => new Add(new Multiply(x.diff(d), y), new Multiply(x, y.diff(d))))
const Divide =  operatorFactory("/", (x, y) => x / y, d => (x, y) => new Divide(new Subtract(new Multiply(x.diff(d), y), new Multiply(y.diff(d), x)), new Multiply(y, y)))
const Hypot =   operatorFactory("hypot", (x, y) => x * x + y * y, d => (x, y) => new Add(new Multiply(x, x), new Multiply(y, y)).diff(d))
const HMean =   operatorFactory("hmean", (x, y) => 2 / (1 / x + 1 / y), d => (x, y) => new Divide(new Const(2), new Add(new Divide(one, x), new Divide(one, y))).diff(d))
const Negate =  operatorFactory("negate", x => -x, d => x => new Negate(x.diff(d)));

const operators = {
    '+': [Add, 2],
    '-': [Subtract, 2],
    '*': [Multiply, 2],
    '/': [Divide, 2],
    'negate': [Negate, 1],
    'hypot': [Hypot, 2],
    'hmean': [HMean, 2]
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