package com.programming.kotlin.chapter01

private class BehavioralPatterns {

    //===strategy pattern===
    enum class Direction {
        LEFT,
        RIGHT
    }

    object Weapons {
        val peashooter = fun(x: Int, y: Int, direction: Direction) { // can assign a function to a variable
            // Fly straight
        }

        val banana = fun(x: Int, y: Int, direction: Direction) {
            // Return when you hit screen border
        }

        val pomegranate = fun(x: Int, y: Int, direction: Direction) {
            // Explode when you hit first enemy
        }
    }

    abstract class Projectile(
        private val x: Int,
        private val y: Int,
        private val direction: Direction
    )

    class OurHero {
        private var direction = Direction.LEFT
        private var x: Int = 42
        private var y: Int = 173
        var currentWeapon = Weapons.peashooter
        val shoot = fun() {
            currentWeapon(x, y, direction)
        }
    }

    fun mainStrategy() {
        val hero = OurHero()
        hero.shoot()
        hero.currentWeapon = Weapons.banana
        hero.shoot()
    }

    //===iterator pattern===
    interface InfantryUnit
    class Soldier(val name: String) : InfantryUnit {

    }

    class Sergeant(val name: String = "Sergeant") : InfantryUnit
    class Lieutenant(val name: String = "Lieutenant") : InfantryUnit
    class Squad(val infantryUnits: MutableList<InfantryUnit> = mutableListOf()) {
        val commander = Sergeant()
        operator fun iterator() = object : Iterator<InfantryUnit> {
            var i = 0
            override fun hasNext(): Boolean {
                return i < infantryUnits.size + 1
            }

            override fun next() =
                when (i) {
                    0 -> commander
                    else -> infantryUnits[i - 1]
                }.also { i++ }
        }

        fun reverseIterator() = object : Iterator<InfantryUnit> {
            var i = 0
            override fun hasNext(): Boolean {
                return i < infantryUnits.size + 1
            }

            override fun next() =
                when (i) {
                    infantryUnits.size -> commander
                    else -> infantryUnits[infantryUnits.size - i - 1]
                }.also { i++ }
        }
    }


    class Platoon(val squads: MutableList<Squad> = mutableListOf()) {
        var commander = Lieutenant()
        operator fun iterator() = object : Iterator<InfantryUnit> {
            var unitIndex = 0
            var squadIndex = 0
            var squadIterator = squads[squadIndex].iterator()
            override fun hasNext(): Boolean {
                return unitIndex < squads.sumBy { it.infantryUnits.size + 1 } + 1
            }

            override fun next() =
                when (unitIndex) {
                    0 -> commander
                    else -> squadIterator.next()
                }.also {
                    unitIndex++
                    if (!squadIterator.hasNext() && hasNext()) squadIterator = squads[++squadIndex].iterator()
                }
        }
    }

    fun mainIterator() {
        var rangers = Squad(mutableListOf(Soldier("Josh"), Soldier("Eric"), Soldier("Tom")))
        var deltaForce = Squad(mutableListOf(Soldier("Sam"), Soldier("Will"), Soldier("Ham")))
        val blackHawk = Platoon(mutableListOf(rangers, deltaForce))

    }
}