package processing.ksketch.natureofcode.chp01

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.FX2D

// The Nature of Code
// Daniel Shiffman
// http://natureofcode.com

// Example 1-1: Bouncing Ball, no vectors
internal val chp01_01_bouncingball_novectors get() = ksketch {

	var x = 100f
	var y = 100f
	var xspeed = 2.5f
	var yspeed = 2f

	settings {
		renderer = FX2D(800, 200, 1)
	}

	draw {
		background = color(1f)
		paintBackground()

		// Add the current speed to the position
		x += xspeed
		y += yspeed

		if (x > width || x < 0)
			xspeed *= -1

		if (y > height || y < 0)
			yspeed *= -1

		stroke = color(0f)
		strokeWeight = 2f
		fill = color(0.5f)
		ellipse(x, y, 48f, 48f)
	}

}
