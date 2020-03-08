package com.programming.kotlin.chapter01

import java.io.BufferedOutputStream
import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

fun main(args: Array<String>) {

    val explicitType: Int = 12
    val modifiedVal = plusOne(explicitType)
    val myArray = arrayOf(1, 2, 3)
    var myGenArray = Array(10) { k -> k * k }
    val rawStr = """THIS is @ raw " string"""
    var intArr = intArrayOf(1, 2, 3)
    val a = "a".."z"
    val isTest = "x" in "a".."z"
    val testDownTo = 10.downTo(5)
    val testUpTo = 10.rangeTo(50)
    val myArr = arrayOf(10, 20, 30)
    for (c in myArr.indices) {
        println("Element $c is ${myArr[c]}")
    }
    var str: String? = null // the way to create nullable string
    printUntilStop()
    var student = Student.create("Jill Williams")
    val panel = Panel(Rectangle(10, 100, 30, 300))
    println("Height: ${panel.getHeight()}, Width: ${panel.getWidth()}")
}

//===basic syntax===
fun printUntilStop() {
    val list = listOf("a", "b", "stop", "c")
    list.forEach {
        if (it == "stop") return@forEach // is not return from printUntilStop
        else println(it)
    }
}

fun printLessThanTwo() {
    val list = listOf(1, 2, 3, 4)
    list.forEach(fun(x) {
        if (x < 2) println(x)
        //else return
    })
}

fun largestNumber(a: Int, b: Int, c: Int): Int { //nested function
    fun largest(a: Int, b: Int): Int {
        return if (a > b) a else b
    }
    return largest(largest(a, b), largest(b, c))
}

fun whatNumber(x: Int) {
    when (x) {
        0 -> println("is zero")
        1 -> println("is one")
        else -> println("huge number")
    }
}

fun isMinOrMaxOrIn9rPositive(x: Int): Boolean {
    return when (x) {
        Int.MAX_VALUE -> true
        Int.MIN_VALUE -> true
        in -9..9 -> true
        abs(x) -> true
        else -> false
    }
}

fun isString(any: Any): Boolean {
    return any is String //SMART cast
}

fun length(any: Any): Int {
    var str = any as String //EXPLICIT cast, throw ClassCastException
    return str.length
}

fun lengthSafe(any: Any): Int {
    val string: String? = any as String
    return string?.length ?: 0

}

fun plusOne(x: Int) = x + 1