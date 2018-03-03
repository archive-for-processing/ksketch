package processing.ksketch.sample

import processing.ksketch.core.*
import processing.ksketch.core.fx2d.PGraphicsFX2D
import processing.ksketch.core.ksketch

val sketch = ksketch {
	settings {
		renderer = PGraphicsFX2D(200, 200)
	}

	var i = 0.0f

	draw {
		i += 0.01f
		background = color(i % 1f)
		paintBackground()
	}
}

fun main(args: Array<String>) {
	sketch.run(milliseconds = 5000)
}
