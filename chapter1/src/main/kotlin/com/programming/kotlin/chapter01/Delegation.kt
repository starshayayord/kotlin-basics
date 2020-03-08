package com.programming.kotlin.chapter01

interface UIElement {
    fun getHeight(): Int
    fun getWidth(): Int
}

class Rectangle(val x1: Int, val x2: Int, val y1: Int, val y2: Int) : UIElement {
    override fun getHeight() = y2 - y1
    override fun getWidth() = x2 - x1
}

class Panel(val rectangle: Rectangle) : UIElement by rectangle