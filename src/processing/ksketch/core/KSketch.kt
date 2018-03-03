/*
  Part of KSketch

  Copyright (c) 2018 Richard Patel

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation, version 2.1.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package processing.ksketch.core

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

// Quick access to setup {} and draw {}

fun ksketch(func: KSketch.() -> Unit): KSketch = KSketch().apply {
	func()
	setupArgs ?: throw IllegalStateException("settings() not called.")

	renderer = setupArgs!!.renderer!!
	renderer.drawFunc = draw
}

class KSketch internal constructor() {

	internal var setupArgs: KSketchSetup? = null
	internal var setup: (IPGraphics.() -> Unit)? = null
	internal var draw: (IPGraphics.() -> Unit) = {}
	internal lateinit var renderer: IPGraphics

	// Defines a setup method
	fun settings(func: KSketchSetup.() -> Unit) {
		val setup = KSketchSetup()
		setup.func()
		setup.renderer ?: throw IllegalStateException("renderer not set")
		setupArgs = setup
	}

	fun setup(func: IPGraphics.() -> Unit) {
		if (this.setup == null)
			setup = func
		else
			throw IllegalArgumentException("setup() method already defined.")
	}

	// Defines a draw method
	private var drawDefined = false
	fun draw(func: IPGraphics.() -> Unit) {
		if (!drawDefined) {
			draw = func
			drawDefined = true
		} else {
			throw IllegalArgumentException("draw() method already defined.")
		}
	}

	// Starts the sketch (asnyc)
	private var started = false
	fun start() {
		// Let's look into this…
		if (started)
			throw IllegalArgumentException("Sketch must not be started more than once.")

		started = true
		val waitForAction = CountDownLatch(1)
		var error: Exception? = null
		thread(start = true, name = "KSketch Renderer Thread") {
			renderer.run(s = this,
				onStart = {
					// Successfully started
					waitForAction.countDown()
				},
				onError = { ex ->
					error = ex
					waitForAction.countDown()
				}
			)
		}
		waitForAction.await()

		if (error != null)
			throw error!!
	}

	// Terminates the sketch
	fun stop() {
		renderer.stop(this)
	}

	// Runs the sketch
	fun run(milliseconds: Int = -1) {
		start()
		if (milliseconds > 0) {
			Thread.sleep(milliseconds.toLong())
			stop()
		}
	}

}

class KSketchSetup internal constructor() {

	var renderer: IPGraphics? = null
		set(value) {
			field = value ?: throw IllegalArgumentException("renderer cannot be null.")
		}

}
