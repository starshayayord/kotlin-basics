package com.programming.kotlin.chapter01

import com.programming.kotlin.chapter01.BehavioralPatterns.Mood as Mood1

typealias Command = () -> Unit

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
    interface InfantryUnit {
        val name: String
    }

    class Soldier(override val name: String) : InfantryUnit {
    }

    class Sergeant(override val name: String = "Sergeant") : InfantryUnit
    class Lieutenant(override val name: String = "Lieutenant") : InfantryUnit
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
        for (u in blackHawk) {
            println(u.name)
        }
    }

    //===state pattern ===
    interface WhatCanHappen {
        fun seeHero()
        fun getHit(pointsOfDmg: Int)
        fun timePassed()
    }

    abstract class Mood
    class Still : Mood()
    class Aggressive : Mood()
    class Retreating : Mood()
    class Dead : Mood()

    class Snail : WhatCanHappen {
        private var healthPoints = 10
        var mood: Mood = Still()
        override fun seeHero() {
            mood = when (mood) {
                is Still -> Aggressive()
                else -> mood
            }
        }

        override fun getHit(pointsOfDmg: Int) {
            healthPoints -= pointsOfDmg
            mood = when {
                healthPoints <= 0 -> Dead()
                mood is Aggressive -> Retreating()
                else -> mood
            }
        }

        override fun timePassed() {
            mood = when (mood) {
                is Retreating -> Aggressive()
                else -> mood
            }
        }
    }

//===command pattern ===

    class CatSoldier {
        private var orders = mutableListOf<Command>()
        val moveGenerator = fun(s: CatSoldier, x: Long, y: Long): Command {
            return fun() {
                s.move(x, y)
            }
        }

        fun appendMove(x: Long, y: Long) = apply { orders.add(moveGenerator(this, x, y)) }

        fun move(x: Long, y: Long) {
            println("Moving to ($x, $y)")
        }

        fun execute() {
            while (orders.isNotEmpty()) {
                var command = orders.removeAt(0)
                command.invoke()
            }
        }
    }

    fun mainCommand() {
        val s = CatSoldier()
        s.appendMove(20, 0)
            .appendMove(20, 20)
            .execute()
    }

    //===Mediator pattern===

    interface Manager {
        fun isAllGood(majorRelease: Boolean): Boolean
    }

    class MemManager {

        private var lastThought = "Should get some coffee"
        private var repeatThat = 3
        private var thenHesitate = "Or maybe tea?"
        private var secretThought = "No, coffee it is"
        fun whatAreYouThinking() {
            for (i in 1..repeatThat) {
                println(lastThought)
            }
            println(thenHesitate)
        }

        inner class Thought {
            fun captureThought(): CapturedThought {
                return CapturedThought(lastThought, repeatThat, thenHesitate, secretThought)
            }
        }

        data class CapturedThought(
            val thought: String,
            val repeat: Int,
            val hesitate: String,
            val secret: String
        )

        fun anotherThought() {
            lastThought = "Tea would be better"
            repeatThat = 2
            thenHesitate = "But coffee is also nice"
            secretThought = "Big latte would be great"
        }

        interface Canary {
        }

        interface QA {
            fun doesMyCodeWork(): Boolean
        }

        interface Parrot {
            fun isEating(): Boolean
            fun isSleeping(): Boolean
        }

        object Michael : Canary, Manager {
            val kenny = Kenny(this)
            val brad = Brad(this)

            override fun isAllGood(majorRelease: Boolean): Boolean {
                if (!kenny.isEating() && !kenny.isSleeping()) {
                    return kenny.doesMyCodeWork()
                } else if (!brad.isEating() && !brad.isSleeping()) {
                    return brad.doesMyCodeWork()
                }
                return false
            }
        }

        class Kenny(private val manager: Manager) : QA, Parrot {
            override fun isSleeping(): Boolean {
                return false
            }

            override fun isEating(): Boolean {
                return false
            }

            override fun doesMyCodeWork(): Boolean {
                return true
            }
        }

        class Brad(private val manager: Manager) : QA, Parrot {
            override fun isSleeping(): Boolean {
                return false
            }

            override fun isEating(): Boolean {
                return false
            }

            override fun doesMyCodeWork(): Boolean {
                return true
            }
        }

        class MyPeacefulMind(private val manager: Manager) {
            fun taskCompleted(isMajorRelease: Boolean) {
                println(manager.isAllGood(isMajorRelease))
            }
        }

        fun mainMediator(args: Array<String>) {
            val michael = MemManager()

            val captured = michael.Thought().captureThought()
            michael.anotherThought()
            michael.whatAreYouThinking()
            println(captured)
        }
    }
}