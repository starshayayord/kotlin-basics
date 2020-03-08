package com.programming.kotlin.chapter01

class Foo(val name: String) {
    inner class Bar(phone: String) {
        fun printName() = println(this@Foo.name)
    }
    fun printMe() = println(this)
}
fun bar(): String = "bar"