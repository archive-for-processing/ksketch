/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2015 The Processing Foundation

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

package processing.ksketch.core.fx2d

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

import processing.ksketch.core.*

class PGraphicsFX2D(val w: Int, val h: Int, val smooth: Int = 0) : IPGraphics {

	companion object {
		var appLaunched = AtomicBoolean()
	}

	internal lateinit var gc: GraphicsContext
	internal lateinit var drawTimer: AnimationTimer
	internal lateinit var onStart: () -> Unit
	internal lateinit var onError: (Exception) -> Unit

	override var drawFunc: IPGraphics.() -> Unit by Delegates.notNull()

	override fun run(s: KSketch, onStart: () -> Unit, onError: (Exception) -> Unit) {
		if (!appLaunched.getAndSet(true)) {
			this.onStart = onStart
			this.onError = onError
			FXApp.outer = this

			try {
				Application.launch(FXApp::class.java)
			} catch (e: Exception) {
				onError(e)
			}
		} else {
			onError(IllegalStateException("Only one FX2D sketch can run at a time."))
		}
	}

	override fun stop(s: KSketch) {
		drawTimer.stop()
		FXApp.instance.stop()
		Platform.exit()
	}

	override var doTint = false
	var tintColor = PColorHelper()
	override var tint: PColor by tintColor.delegate()

	override var doFill = true
	var fillColor = PColorHelper()
	override var fill: PColor by fillColor.delegate {
		gc.fill = Color(
			fillColor.r.toDouble(),
			fillColor.g.toDouble(),
			fillColor.b.toDouble(),
			fillColor.a.toDouble()
		)
	}

	override var doStroke = true
	var strokeColor = PColorHelper()
	override var stroke: PColor by strokeColor.delegate {
		gc.stroke = Color(
			fillColor.r.toDouble(),
			fillColor.g.toDouble(),
			fillColor.b.toDouble(),
			fillColor.a.toDouble()
		)
	}
	override var strokeWeight = 1f
	override var strokeJoin = MITER
	override var strokeCap = ROUND

	override var rectMode = CORNER
	override var ellipseMode = DIAMETER
	override var shapeMode = CORNER
	override var imageMode = CORNER

	var ambientColor = PColorHelper()
	override var ambient by ambientColor.delegate()

	var specularColor = PColorHelper()
	override var specular by specularColor.delegate()

	var emissiveColor = PColorHelper()
	override var emissive by emissiveColor.delegate()

	override var shininess = 0f

	// Background

	var backgroundColor = PColorHelper()
	override var background: PColor by backgroundColor.delegate()

	override fun clear() {
		gc.clearRect(0.0, 0.0, w.toDouble(), h.toDouble())
	}

	override fun paintBackground() {
		fill = background
		gc.fillRect(0.0, 0.0, w.toDouble(), h.toDouble())
	}

	// Blend Mode

	override var blendMode = 0 // FIXME Init

	override var textureMode = 0 // FIXME Init

	override var edge = false // FIXME Init

}

