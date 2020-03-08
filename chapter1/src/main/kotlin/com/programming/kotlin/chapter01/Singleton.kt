package com.programming.kotlin.chapter01

object Singleton {
    private var count = 0
    fun doSomething() {
        println("Calling a doSomething (${++count} call/-s in total)")
    }
}

open class SingletonParent(var x: Int) {
    fun something() {
        println("X=$x")
    }
}

object SingletonDerive : SingletonParent(10) {}

//the way to create a static function create()
interface StudentFactory {
    fun create(name: String): Student
}

class Student private constructor(val name: String) {
    companion object : StudentFactory {
        override fun create(name: String): Student {
            return Student(name)
        }
    }
}