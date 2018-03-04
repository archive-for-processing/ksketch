package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-3: Vector subtraction
internal val chp01_03_vector_subtraction get() = ksketch {
	settings {
		renderer = FX2D(640, 360)
	}

	draw {
		background = color(255f)

		val w = width.toFloat()
		val h = height.toFloat()

		val mouse = PVector(mouseX, mouseY)
		val center = PVector(w / 2, h / 2)
		mouse -= center

		translate(w / 2, h / 2)
		strokeWeight = 2f
		stroke = color(0f)
		line(0f, 0f, mouse.x, mouse.y)
	}
}
