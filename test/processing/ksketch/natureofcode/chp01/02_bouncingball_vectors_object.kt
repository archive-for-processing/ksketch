package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-2: Bouncing Ball, with PVector!
internal object chp01_02_bouncingball_vectors_object {

	val sketch get() = ksketch {
		val b = Ball()

		settings {
			renderer = FX2D(800, 200, 1)
		}

		draw {
			background = color(255f)
			b.update()
			b.draw()
		}
	}

	class Ball {
		val position = PVector(100f, 100f)
		val velocity = PVector(2.5f, 5f)

		fun update() { with(sketch.renderer) {
			// Add the current speed to the position.
			position += velocity

			if (position.x > width || position.x < 0)
				velocity.x *= -1

			if (position.y > height || position.y < 0)
				velocity.y *= -1
		}}

		fun draw() { with(sketch.renderer) {
			// Display circle at x position
			stroke = color(0f)
			strokeWeight = 2f
			fill = color(0.5f)
			ellipse(position.x, position.y, 16f, 16f)
		}}
	}

}
