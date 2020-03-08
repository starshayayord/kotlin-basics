package com.programming.kotlin.chapter01

import java.io.BufferedOutputStream
import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

typealias PointsOfDamage = Long
typealias Meters = Long

const val GRENADE_DAMAGE: PointsOfDamage = 5L
const val RIFLE_DAMAGE: PointsOfDamage = 3L
const val REGULAR_SPEED: Meters = 1

private class DesignPatterns {
    //===proxy pattern===
    data class CatImage(
        private val thumbnailUrl: String,
        private val url: String
    ) {
        val image: File by lazy { // lazy() synchronized by default
            println("Fetching image over network")
            val f = File.createTempFile("cat", ".jpg")
            java.net.URI.create(url).toURL().openStream().use {
                it.copyTo(BufferedOutputStream(f.outputStream()))
            }.also { println("Done fetching") }
            f
        }
    }

    //===facade pattern===
    interface GameWorld {
        fun loadGame(file: File): GameWorld // static factory method
    }

    val sprites = List(8) { i ->
        File(
            when (i) {
                0 -> "snail-left.jpg"
                1 -> "snail-right.jpg"
                in 2..4 -> "snail-move-left-${i - 1}.jpg"
                else -> "snail-move-right-${4 - i}.jpg"
            }
        )
    }

    class TansanianSnail() {
        val directionFacing = Direction.LEFT

        fun TansanianSnail.getCurrentSprite(sprites: List<File>): File {
            return when (directionFacing) {
                Direction.LEFT -> sprites[0]
                Direction.RIGHT -> sprites[1]
            }
        }
    }

    enum class Direction {
        LEFT,
        RIGHT
    }


    //===composite pattern===
    interface InfantryUnit : CanCountBullets
    class RiflemanUnit(initialMagazines: Int = 3) : InfantryUnit {
        private val magazines = List<Magazine>(initialMagazines) { Magazine(5) }
        override fun bulletsLeft() = magazines.sumBy { it.bulletsLeft() }
    }

    class Sniper(initialBullets: Int = 50) : InfantryUnit {
        private val bullets = List(initialBullets) { Bullet() }
        override fun bulletsLeft() = bullets.size
    }

    interface CanCountBullets {
        fun bulletsLeft(): Int
    }

    class Bullet
    class Magazine(capacity: Int) : CanCountBullets {
        private val bullets = List(capacity) { Bullet() }
        override fun bulletsLeft() = bullets.size
    }

    class Squad(val infantryUnits: MutableList<InfantryUnit> = mutableListOf()) : CanCountBullets {
        constructor(first: InfantryUnit) : this(mutableListOf()) {
            this.infantryUnits.add(first)
        }

        constructor(first: InfantryUnit, second: InfantryUnit) : this(first) {
            this.infantryUnits.add(second)
        }

        constructor(first: InfantryUnit, second: InfantryUnit, third: InfantryUnit) : this(first, second) {
            this.infantryUnits.add(third)
        }

        //alternative way
        constructor(vararg units: InfantryUnit) : this(mutableListOf()) {
            for (u in units) {
                this.infantryUnits.add(u)
            }
        }

        override fun bulletsLeft() = infantryUnits.sumBy { it.bulletsLeft() }


    }

    //===bridging changes pattern===


    class Soldier(
        private val weapon: Weapon,
        private val legs: Legs
    ) : InfantryUpg {
        override fun move(x: Long, y: Long) {
            legs.move()
        }

        override fun attack(x: Long, y: Long) {
            weapon.causeDamage()
        }
    }

    interface Weapon {
        fun causeDamage(): PointsOfDamage
    }

    interface Legs {
        fun move(): Meters
    }

    class Grenade : Weapon {
        override fun causeDamage() = GRENADE_DAMAGE
    }

    class GrenadePack : Weapon {
        override fun causeDamage() = GRENADE_DAMAGE * 3
    }

    class Rifle : Weapon {
        override fun causeDamage() = RIFLE_DAMAGE
    }

    class MachineGun : Weapon {
        override fun causeDamage() = RIFLE_DAMAGE * 2
    }

    class RegularLegs : Legs {
        override fun move() = REGULAR_SPEED
    }

    class AthleticLegs : Legs {
        override fun move() = REGULAR_SPEED * 2
    }

    var rifleman = Soldier(Rifle(), RegularLegs())
//===bridge pattern===

    interface InfantryUpg {
        fun move(x: Long, y: Long)

        fun attack(x: Long, y: Long)
    }

    open class Rifleman1 : InfantryUpg {
        override fun move(x: Long, y: Long) {
        }

        override fun attack(x: Long, y: Long) {
        }
    }

    class Rifleman2 : Rifleman1() {
        override fun attack(x: Long, y: Long) {
        }
    }

    class Rifleman3 : Rifleman1() {
        override fun move(x: Long, y: Long) {
        }
    }

    open class Grenadier1 : InfantryUpg {
        override fun move(x: Long, y: Long) {
        }

        override fun attack(x: Long, y: Long) {
        }
    }

    open class Grenadier2 : Grenadier1() {
        override fun attack(x: Long, y: Long) {
        }
    }

    open class Grenadier3 : Grenadier1() {
        override fun move(x: Long, y: Long) {
        }
    }

    //===adapter (wrapper) pattern===
    interface UsbTypeC
    interface UsbMini
    interface EUPlug
    interface USPlug

    fun powerOutlet(): USPlug {
        return object : USPlug {}
    }

    fun cellPhone(chargeCable: UsbTypeC) {
    }

    fun charger(plug: EUPlug): UsbMini {
        return object : UsbMini {}
    }

    fun USPlug.toEUPlug(): EUPlug {
        return object : EUPlug {
            //to something to convert
        }
    }

    fun UsbMini.toUsbTypeC(): UsbTypeC {
        return object : UsbTypeC {
            //to something to convert
        }
    }

    var cellPhone = cellPhone(charger(powerOutlet().toEUPlug()).toUsbTypeC())

    //===decorator pattern
    class HappyMap<K, V>(private val map: MutableMap<K, V> = mutableMapOf()) : MutableMap<K, V> by map {
        override fun put(key: K, value: V): V? {
            return map.put(key, value).apply {
                this?.let { println("Yay!$key") }
            }
        }
    }

    class SadMap<K, V>(private val map: MutableMap<K, V> = mutableMapOf()) : MutableMap<K, V> by map {
        override fun remove(key: K): V? {
            println("Okay...")
            return map.remove(key)
        }
    }

    var sadHappyMap = SadMap(HappyMap<String, String>())
    var superSadMap = SadMap(SadMap<String, String>())

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
}