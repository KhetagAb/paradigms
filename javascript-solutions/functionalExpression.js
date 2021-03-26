"use strict";

const varIndexes = {
    'x': 0,
    'y': 1,
    'z': 2
}

const cnst = value => () => value;
const variable = name => {
    const index = varIndexes[name]
    return (...vars) => vars[index]
}

let one = cnst(1)
let two = cnst(2)

const operation = f => (... exps) => (...vars) => f(...exps.map(ex => ex(...vars)))
const add = operation((x, y) => x + y)
const subtract = operation((x, y) => x - y)
const multiply = operation((x, y) => x * y)
const divide = operation((x, y) => x / y)
const negate = operation(x => -x)
const floor = operation(Math.floor)
const ceil = operation(Math.ceil)
const madd = operation((x, y, z) => x * y + z)

const cnsts = {
    "one": one,
    "two": two
}

const operators = {
    '+': [2, add],
    '-': [2, subtract],
    '*': [2, multiply],
    '/': [2, divide],
    'negate': [1, negate],
    '_': [1, floor],
    'floor': [1, floor],
    '^': [1, ceil],
    'ceil': [1, ceil],
    '*+': [3, madd],
    'madd': [3, madd],
}

const parse = input => {
    return input.split(" ").filter(e => e !== "").reduce((stack, token) => {
        let top
        if (token in operators) {
            let oper = operators[token]
            top = oper[1](...stack.splice(stack.length - oper[0]))
        } else if (token in varIndexes) {
            top = variable(token)
        } else if (token in cnsts) {
            top = cnsts[token]
        } else {
            top = cnst(Number(token))
        }
        return stack.concat(top)
    }, []).pop();
}

let test = parse("x x * 2 x * - 1 +")
for (let i = 0; i <= 10; i++) {
    console.log(i + " => " + test(i))
}