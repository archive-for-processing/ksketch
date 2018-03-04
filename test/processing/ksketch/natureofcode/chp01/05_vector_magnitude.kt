package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-5: Vector magnitude
internal val chp01_05_vector_magnitude get() = ksketch {
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

		val m = mouse.mag()
		fill = color(0f)
		doStroke = false
		rect(0f, 0f, m, 10f)

		translate(w / 2, h / 2)
		strokeWeight = 2f
		stroke = color(0f)
		line(0f, 0f, mouse.x, mouse.y)
	}
}
