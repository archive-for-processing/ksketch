package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-2: Bouncing Ball, with PVector!
internal val chp01_02_bouncingball_vectors get() = ksketch {

	val position = PVector()
	val velocity = PVector()

	settings {
		renderer = FX2D(800, 200, 1)
	}

	setup {
		background = color(255f)
		position.set(100f, 100f)
		velocity.set(2.5f, 5f)
	}

	draw {
		doStroke = false
		fill = color(255f, 10f)
		rect(0f, 0f, width.toFloat(), height.toFloat())

		// Add the current speed to the position
		position += velocity

		if (position.x > width || position.x < 0)
			velocity.x *= -1

		if (position.y > height || position.y < 0)
			velocity.y *= -1

		// Display circle at x position
		stroke = color(0f)
		strokeWeight = 2f
		fill = color(0.5f)
		ellipse(position.x, position.y, 16f, 16f)
	}

}
