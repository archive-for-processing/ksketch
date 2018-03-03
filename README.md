# KSketch

_Embedded Processing for Kotlin!_

This project aims to rewrite the Java mode of Processing 3 for Kotlin.
To keep this project lightweight, as little reflection is used as possible.
Everything is kept in vanilla Kotlin.

Also, everything declared deprecated in Processing is dropped for simplicity.

#### Usage

**Starting up**

Define KSketches with the `ksketch` method:

```kotlin
val mySketch = ksketch {
	settings {
		renderer = PGraphicsFX2D(200, 200)
	}
	setup {}
	draw {}
}
```

`settings{}` and `renderer` are required.

KSketches won't take control of your whole program so they can be started
and stopped. If `stop()` is never called and the end of `main()` is reached,
Java stays open until you close the window.

```kotlin
fun main(args: Array<String>) {
	mySketch.start()
	println("Sketch is running in the background")
	doSomethingElse()
	mySkethch.stop()
}
```

You can also use the blocking `run()` function.
`milliseconds` is optional and stops the sketch automatically.

```kotlin
fun main(args: Array<String>) {
	mySketch.run(milliseconds = 5000)
	println("Sketch just finished running")
}
```

**API**

Most functions are replaced by property-access-syntax.
Here is a list of changes.

| Function | Processing | KSketch |
| -------- | ---------- | ------- |
| Color by parts | `fill(255, 255, 0)` | `fill = color(1f, 1f, 0f)` |
| Color by RGB | Not possible | `0xFF_FF_00L.rgb`/`.argb`/`.rgba` |
| Enable/disable | `noFill()`/`fill(…)` | `doFill = true`/`false` |

**Constants**

Processing comes with a lot of constants in an interface.
The code you write in its IDE is eventually placed inside a PGraphics that
implements this interface monster. Clever hack, but Kotlin can do better.
We define them as package-level fields in `processing.ksketch.core`.
They can be wildcard imported from there.

**Interfaces**

I prefer readable code over tons of Javadoc. The public variables and
interfaces are all together in `Interfaces.kt`

**TODO**

The following functions are currently unsupported but will be implemented later:
 * Everything except setting the background
 * GL renderer
 * HSB color mode / custom color ranges
 * Objects (PVector/PShape/PMatrix)
   (a lot harder than one might think, this is the first vector class I've seen
   that references a controller (PApplet))
 * Image utility

#### Why tho?

I started this because all the sacrifices to make Processing nice and clean
in its IDE make it incredibly painful to use Processing outside its comfort
zone. It is impossible to use Processing in IntelliJ IDEA for example while
still maintaining sanity. I saw this as a great opportunity to use Kotlin's
flexibility.

### Copyright

The code of this project is based on [Part of the Processing project](http://processing.org).

Modified by Richard Patel.
