package com.programming.kotlin.chapter01

class Person constructor(val firstName: String, val lastName: String, val age: Int?) {
    init {
        require(firstName.trim().isNotEmpty()) { "Invalid first name argument" } //IllegalArgumentException
        if (age != null) {
            require(age in 1..149) { "Invalid age argument" }
        }
    }

    constructor(firstName: String, lastName: String) : this(firstName, lastName, null)
}
