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
        function toString() { return this.operands.concat(symbol).join(" "); })
}

const Add = abstractOperation("+", (x, y) => x + y, (d, x, y) => new Add(x.diff(d), y.diff(d)))
const Subtract = abstractOperation("-", (x, y) => x - y, (d, x, y) => new Subtract(x.diff(d), y.diff(d)));
const Multiply = abstractOperation("*", (x, y) => x * y, (d, x, y) => new Add(new Multiply(x.diff(d), y), new Multiply(x, y.diff(d))))
const Divide =   abstractOperation("/", (x, y) => x / y, (d, x, y) => new Divide(new Subtract(new Multiply(x.diff(d), y), new Multiply(y.diff(d), x)), new Multiply(y, y)))
const Hypot =    abstractOperation("hypot", (x, y) => x * x + y * y, (d, x, y) => new Add(new Multiply(x, x), new Multiply(y, y)).diff(d))
const HMean =    abstractOperation("hmean", (x, y) => 2 / (1 / x + 1 / y), (d, x, y) => new Divide(new Const(2), new Add(new Divide(Const.one, x), new Divide(Const.one, y))).diff(d))
const Negate =   abstractOperation("negate", x => -x, (d, x) => new Negate(x.diff(d)));

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

// (- (* 2 x) 3)
// (+ ...)

// ex = @ <ex> <ex> <ex> <ex>
// ex = <ex> <ex> <ex> @
// ex = 1
// ex = x
function parser(input) {
    let source = {
        index: 0,
        input: input.replace(/[(]/g, " ( ").replace(/[)]/g, " ) ").split(" ").filter(e => e !== "");
        hasNext: index < this.input.length,
        next: this.input[++index]
    }

    return {
        parse: function (mode) {
            if (Number.isFinite(input[index])) {
                return new Const(input[index])
            }

            if (mode === "prefix") {
                return [].concat(this.parseOperator()).concat(this.parseOperands);
            } else {
                return [].concat(this.parseOperands()).concat(this.parseOperator());
            }
        },
        parseOperator: () => 1,
        parseOperands: () => 1
    }
}
function parsePrefix(input) {
    return parser("prefix").parse(input);
}


let ex = new Subtract(new Multiply(new Const(2), new Variable('x')), new Const(3));

// console.log(ex.toString())
// console.log(ex.prefix())

console.log(parsePrefix("(-    (*  2   x)  3)"))
