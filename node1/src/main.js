import test1 from './test1.js';
import Person from './Person.js';

console.info("==================");
console.info("main.test1=" + test1);
console.info(...[1, 2, 3]);
let o = {a: 1, b: 2, c: 3};
console.info(o);

async function readFile(fileName = "aaa") {
    console.info("readFile start");
    let result;
    try {
        result = await timeout(-1000);
    } catch (e) {
        console.error("======" + e)
    }
    console.info("readFile end");
    return `readFile ${fileName} ${result}`
}

function timeout(delay) {
    return new Promise((resolve, reject) => {
        if (delay > 0) {
            setTimeout(resolve, delay, "ok");
        } else {
            reject("timeout delay error:" + delay)
        }
    })
}

console.info("readFile==================");
readFile().then(result => console.info("readFile result:" + result))
    .catch(reason => console.info("readFile catch:" + reason))
    .finally(() => console.info("readFile finally"));


async function* asyncIterate(n = 10) {
    for (let i = 0; i < n; i++) {
        try {
            let result = await timeout(100)
            yield result + i;
        } catch (e) {
            console.error("asyncIterate:" + e)
        }
    }
}

console.info("asyncIterate==================");
(async function () {
    let generator = asyncIterate();
    let promise = generator.next();
    promise.then(value => {
        console.info("value:" + value.value);
    });
    for await (let e of generator) {
        console.info(e);
    }
})();

console.info("Person==================");

let person = new Person();
person.name = "张三";

person.say();
console.info(person.name);

console.info("for of==================");
for (let p of person) {
    console.info(p)
}

console.info("Proxy==================");
let person2 = new Proxy(person, {
    get: function (target, prop, receiver) {
        console.info(`get ${prop} ${receiver === person2}`);
        return target[prop] ? target[prop] : "未定义属性:" + prop;
    },
    set: function (target, prop, value, receiver) {
        console.info(`set ${prop}=${value} ${receiver === person2}`);
        if (!target[prop]) {
            throw new Error("未定义属性:" + prop)
        }
    }
});
console.info(person2.name);
// console.info(person2.address);
// person2.address = "undefined address";


