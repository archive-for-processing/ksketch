package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-6: Vector normalize
internal val chp01_06_vector_normalize get() = ksketch {
	settings {
		renderer = FX2D(640, 360)
	}

	draw {
		background = color(255f)
		paintBackground()

		val w = width.toFloat()
		val h = height.toFloat()

		// A vector that points to the mouse position
		val mouse = PVector(mouseX, mouseY)
		// A vector that points to the center of the window
		val center = PVector(w / 2, h / 2)
		// Subtract center from mouse which results in a vector that points from center to mouse
		mouse -= center

		// Normalize the vector
		mouse.normalize()

		// Multiply its length by 50
		mouse *= 150f

		translate(w / 2, h / 2)
		// Draw the resulting vector
		strokeWeight = 2f
		stroke = color(0f)
		line(0f, 0f, mouse.x, mouse.y)
	}
}
