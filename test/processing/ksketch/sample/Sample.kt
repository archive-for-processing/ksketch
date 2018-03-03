package processing.ksketch.sample

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.PGraphicsFX2D
import processing.ksketch.core.ksketch
import kotlin.math.sin

val sketch = ksketch {
	settings {
		renderer = PGraphicsFX2D(200, 200)
	}

	var i = 0.0f

	draw {
		i += 0.01f
		background = color((sin(i) + 1) / 2)
		paintBackground()

		translate(100f, 100f)
		rotate(i)

		doFill = true
		fill = color(1f, 0f, 0f, 0.8f)
		doStroke = false
		rect(-50f, -50f, 100f, 100f)

		doFill = false
		doStroke = true
		stroke = color((-sin(i) + 1) / 2)
		strokeWeight = 2f
		ellipse(-100f, -100f, 200f, 200f)
	}
}

fun main(args: Array<String>) {
	sketch.run()
}
