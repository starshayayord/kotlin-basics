package com.programming.kotlin.chapter01

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

//===prototype pattern===
data class PC(
    val motherboard: String = "Terassus XZ27",
    val cpu: String = "Atom K500",
    val graphCard: String = "Nvidia CG150"
)

val pcFromWarehouse = PC()
var powerPC = pcFromWarehouse.copy(cpu = "Atom K 600")

//===builder pattern===

data class Mail(
    private var _message: String = "",
    private var _cc: List<String> = listOf(),
    private var _title: String? = null

) {
    fun message(message: String): Mail {
        _message = message
        return this
    }

    fun title(title: String?): Mail {
        _title = title
        return this
    }

    fun cc(cc: List<String>): Mail {
        _cc = cc
        return this
    }
}

//alternative way
data class Mail2(
    val to: String,
    var message: String = ""
)

class MailBuilder(val to: String) {
    private var mail: Mail2 = Mail2(to)
    fun message(message: String): MailBuilder {
        mail.message = message
        return this
    }

    fun build(): Mail2 {
        return mail
    }
}


//===abstract factory (a factory of factories)===
interface Building<in UnitType, out ProducedUnit>
        where UnitType : Enum<*>, ProducedUnit : Unit {
    fun build(type: UnitType): ProducedUnit
}

interface Unit
interface Vehicle : Unit
interface Infantry : Unit

class Rifleman : Infantry
class RockerSoldier : Infantry
enum class InfantryUnits {
    RIFLEMEN,
    ROCKER_SOLDIER
}

class APC : Vehicle
class Tank : Vehicle

enum class VehicleUnits {
    APC,
    TANK
}

interface HQ {
    val buildings: MutableList<Building<*, Unit>>
        get() = mutableListOf()

    fun buildBarracks(): Building<InfantryUnits, Infantry> {
        val b = object : Building<InfantryUnits, Infantry> {
            override fun build(type: InfantryUnits): Infantry {
                return when (type) {
                    InfantryUnits.RIFLEMEN -> Rifleman()
                    InfantryUnits.ROCKER_SOLDIER -> RockerSoldier()
                }
            }
        }
        buildings.add(b)
        return b
    }

    fun buildVehicleFactory(): Building<VehicleUnits, Vehicle> {
        val vf = object : Building<VehicleUnits, Vehicle> {
            override fun build(type: VehicleUnits): Vehicle {
                return when (type) {
                    VehicleUnits.APC -> APC()
                    VehicleUnits.TANK -> Tank()
                }
            }
        }
        buildings.add(vf)
        return vf
    }
}

//===static-factory pattern (more obvious naming, no exceptions from a constructor)===
class NumberMaster {
    companion object { //<- companion instead of static keyword
        fun valueOf(hopefullyNumber: String): Long {
            return hopefullyNumber.toLong()
        }
    }
}

//===generics===
class Box<T> {
    private var inside: T? = null
    fun put(t: T) {
        inside = t
    }

    fun get(): T? = inside
}

//===factory pattern===
interface Animal {
    val name: String
}

class CatFactory {
    fun createCat(breed: String, name: String) = when (breed.trim().toLowerCase()) {
        "persian" -> Persian(name)
        else -> throw RuntimeException("unknown cat breed $breed")
    }
}

open class Cat(override val name: String) : Animal {
}

class Persian(name: String) : Cat(name) {
}

class DogFactory {
    fun createDog(breed: String, name: String) = when (breed.trim().toLowerCase()) {
        "bulldog" -> Persian(name)
        else -> throw RuntimeException("unknown dog breed $breed")
    }
}

open class Dog(override val name: String) : Animal {
}

class Bulldog(name: String) : Dog(name) {
}

class AnimalFactory {
    private var counter = 0
    private val dogFactory = DogFactory()
    private val catFactory = CatFactory()
    fun createAnimal(animalType: String, animalBreed: String): Animal {
        return when (animalType.trim().toLowerCase()) {
            "cat" -> catFactory.createCat(animalBreed, counter++.toString())
            "dog" -> dogFactory.createDog(animalBreed, counter++.toString())
            else -> throw RuntimeException("unknown animal type $animalType")
        }
    }
}

//===singleton-object, can't have a constructor, but can have an init function instead===
object MySingleton {
    init {
        println("I was accessed for the first time")
    }

    private val counter = AtomicInteger(0)
    fun increment() = counter.incrementAndGet()
}

//===extension function===
inline fun String.reversed(): String {
    return (this as CharSequence).reversed().toString();
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