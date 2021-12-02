import decorator from "./decorator.js";

// @decorator
class Person {

    #name = "person";
    #age = 20;

    constructor() {
    }

    /**
     * @param {string} value
     */
    set name(value) {
        this.#name = value;
    }

    get name() {
        return this.#name;
    }

    get age() {
        return this.#age;
    }

    set age(value) {
        this.#age = value;
    }

    say() {
        console.info(`我是${this.#name},今年${this.#age}岁`);
    }

    toString() {
        return JSON.stringify(this);
    }

    * [Symbol.iterator]() {
        for (let i = 0; i < 5; i++) {
            yield this.#name + ":" + i;
        }
    }

    // [Symbol.iterator]() {
    //     let i = 0;
    //     let name = this.#name
    //     return {
    //         next() {
    //             return {done: i > 5, value: name + ":" + i++};
    //         }
    //     }
    // }
}

export default Person