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
import javafx.scene.effect.BlendMode
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.transform.Affine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates
import kotlin.math.tan

import processing.ksketch.core.*
import kotlin.math.max
import kotlin.math.min

class FX2D(width: Int, height: Int, val smooth: Int = 0) : IPGraphics {

	override var width: Int = width
		set(value) {
			if (resize())
				field = value
		}

	override var height: Int = height
		set(value) {
			if (resize())
				field = value
		}

	companion object {
		var appLaunched = AtomicBoolean()
	}

	internal lateinit var gc: GraphicsContext
	internal lateinit var drawTimer: AnimationTimer
	internal lateinit var onStart: () -> Unit
	internal lateinit var onError: (Exception) -> Unit

	internal val transformStack = LimitedStack<Affine>(32)

	override var setupFunc: (IPGraphics.() -> Unit)? = null
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

	// Current mouse
	internal var _mouseX = 0f;  internal var _mouseY = 0f
	// Previous mouse
	internal var _pmouseX = 0f; internal var _pmouseY = 0f
	// Event mouse
	internal var _emouseX = 0f; internal var _emouseY = 0f

	override val mouseX  get() = _mouseX
	override val mouseY  get() = _mouseY
	override val pmouseX get() = _pmouseX
	override val pmouseY get() = _pmouseY

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
			strokeColor.r.toDouble(),
			strokeColor.g.toDouble(),
			strokeColor.b.toDouble(),
			strokeColor.a.toDouble()
		)
	}
	override var strokeWeight = 1f
		set(value) {
			doStroke = strokeWeight != 0f
			gc.lineWidth = value.toDouble()
			field = value
		}
	override var strokeJoin = MITER
		set(value) {
			gc.lineJoin = when (value) {
				MITER -> StrokeLineJoin.MITER
				ROUND -> StrokeLineJoin.ROUND
				else  -> StrokeLineJoin.BEVEL
			}
			field = value
		}
	override var strokeCap = ROUND
		set(value) {
			gc.lineCap = when(value) {
				ROUND   -> StrokeLineCap.ROUND
				PROJECT -> StrokeLineCap.SQUARE
				else    -> StrokeLineCap.BUTT
			}
			field = value
		}

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

	// Blend Mode

	override var blendMode = 0 // FIXME Init

	override var textureMode = 0 // FIXME Init

	override var edge = false // FIXME Init

	override fun clear() {
		gc.save()
		gc.transform = Affine()

		val w = width.toDouble(); val h = height.toDouble()
		gc.clearRect(0.0, 0.0, w, h)

		gc.restore()
	}

	override fun paintBackground() {
		gc.save()
		gc.transform = Affine()

		fill = background
		gc.globalBlendMode = BlendMode.SRC_OVER
		val w = width.toDouble(); val h = height.toDouble()
		gc.fillRect(0.0, 0.0, w, h)

		gc.restore()
	}

	// Primitives

	override fun point(x: Float, y: Float) {
		if (doStroke)
			line(x, y, x + EPSILON, y + EPSILON)
	}

	override fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
		gc.strokeLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
	}

	override fun rect(x1: Float, y1: Float, x2: Float, y2: Float) {
		var a = x1; var b = y1; var c = x2; var d = y2
		when (rectMode) {
			CORNER -> {
				c += a
				d += b
			}
			RADIUS -> {
				val hr = c
				val vr = d
				c = a + hr
				d = b + vr
				a -= hr
				b -= vr
			}
			CENTER -> {
				val hr = c / 2f
				val vr = d / 2f
				c = a + hr
				d = b + vr
				a -= hr
				b -= vr
			}
		}

		a = min(a, c)
		c = max(a, c)
		b = min(b, d)
		d = max(b, d)

		rectImpl(a, b, c, d)
	}

	internal fun rectImpl(x1: Float, y1: Float, x2: Float, y2: Float) {
		val x = x1.toDouble(); val y = y1.toDouble()
		val w = (x2-x1).toDouble(); val h = (y2-y1).toDouble()

		if (doFill)
			gc.fillRect(x, y, w, h)
		if (doStroke)
			gc.strokeRect(x, y, w, h)
	}

	override fun ellipse(x: Float, y: Float, w: Float, h: Float) {
		val xd = x.toDouble(); val yd = y.toDouble()
		val wd = w.toDouble(); val hd = h.toDouble()

		if (doFill)
			gc.fillOval(xd, yd, wd, hd)
		if (doStroke)
			gc.strokeOval(xd, yd, wd, hd)
	}

	override fun arc(x: Float, y: Float, w: Float, h: Float, start: Float, stop: Float, mode: Int) {
		val _start = -start
		val _stop = -stop

		val sweep = _stop - _start

		var fillMode = ArcType.ROUND
		var strokeMode = ArcType.OPEN

		when (mode) {
			OPEN -> fillMode = ArcType.OPEN
			PIE  -> strokeMode = ArcType.ROUND
			CHORD -> {
				fillMode = ArcType.CHORD
				strokeMode = ArcType.CHORD
			}
		}

		val xd = x.toDouble(); val yd = y.toDouble()
		val wd = w.toDouble(); val hd = h.toDouble()

		val degStart = start.radToDeg.toDouble()
		val degSweep = sweep.radToDeg.toDouble()

		if (doFill)
			gc.fillArc(xd, yd, wd, hd, degStart, degSweep, fillMode)
		if (doStroke)
			gc.strokeArc(xd, yd, wd, hd, degStart, degSweep, strokeMode)
	}

	// Matrix stack

	override fun pushMatrix() {
		val transform = gc.transform
		if (transformStack.push(transform) == null)
			throw RuntimeException("Transform matrix overflow")
	}

	override fun popMatrix() {
		if (transformStack.count() <= 0)
			throw RuntimeException("Transform matrix underflow")
		gc.transform = transformStack.pop()
	}

	// Matrix transforms

	override fun translate(x: Float, y: Float) {
		gc.translate(x.toDouble(), y.toDouble())
	}

	override fun rotate(angle: Float) {
		gc.rotate(angle.radToDeg.toDouble())
	}

	override fun scale(s: Float) =
		scale(s, s)

	override fun scale(sx: Float, sy: Float) {
		gc.scale(sx.toDouble(), sy.toDouble())
	}

	override fun shearX(angle: Float) {
		gc.transform(Affine()
			.apply { appendScale(tan(angle).toDouble(), 0.0) })
	}

	override fun shearY(angle: Float) {
		gc.transform(Affine()
			.apply { appendScale(0.0, tan(angle).toDouble()) })
	}

	// Matrix more

	override fun resetMatrix() {
		gc.transform = Affine()
	}

	override fun applyMatrix(n00: Float, n01: Float, n02: Float, n10: Float, n11: Float, n12: Float) {
		val o00 = n00.toDouble(); val o01 = n01.toDouble(); val o02 = n02.toDouble()
		val o10 = n10.toDouble(); val o11 = n11.toDouble(); val o12 = n12.toDouble()
		gc.transform(o00, o10, o01, o11, o02, o12)
	}

	override fun getMatrix(target: PMatrix2D) = target.apply {
		val t = gc.transform
		set(
			t.mxx.toFloat(), t.mxy.toFloat(), t.tx.toFloat(),
			t.myx.toFloat(), t.myy.toFloat(), t.ty.toFloat()
		)
	}

	override fun setMatrix(source: PMatrix2D) {
		gc.setTransform(
			source.m00.toDouble(), source.m10.toDouble(),
			source.m01.toDouble(), source.m11.toDouble(),
			source.m02.toDouble(), source.m12.toDouble()
		)
	}

	// Screen and model transforms

	override fun screenX(x: Float, y: Float) =
		gc.transform.transform(x.toDouble(), y.toDouble()).x.toFloat()

	override fun screenY(x: Float, y: Float) =
		gc.transform.transform(x.toDouble(), y.toDouble()).y.toFloat()

	// Private

	internal fun prepareFrame() {
		resetMatrix()
		_pmouseX =  _mouseX; _pmouseY =  _mouseY
		 _mouseX = _emouseX;  _mouseY = _emouseY
	}

	private fun resize(): Boolean {
		throw RuntimeException("Resizing not implemented")
	}

}
