/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-15 The Processing Foundation
  Copyright (c) 2004-11 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

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

typealias PColor = Int

interface IPImage {

	var width: Int
	var height: Int
	//var format: Int

	//var pixels: IntArray?
	//var pixelDensity: Int

	//var pixelWidth: Int
	//var pixelHeight: Int

}

/** Contains the current graphics settings */
interface IPGraphicsContext {

	var doTint: Boolean
	var tint: PColor

	var doFill: Boolean
	var fill: PColor

	var doStroke: Boolean
	var stroke: PColor
	var strokeWeight: Float
	var strokeJoin: Int
	var strokeCap: Int

	var rectMode: Int
	var ellipseMode: Int
	var shapeMode: Int
	var imageMode: Int

	var ambient: PColor
	var specular: PColor
	var emissive: PColor
	var shininess: Float

	var background: PColor

	var blendMode: Int

	var textureMode: Int
	var edge: Boolean

}

interface IPGraphicsEnv {

	//var parent: PApplet?
	//var primaryGraphics: Boolean
	//var path: String

	// Require KSketch to prevent users
	// from calling lifecycle methods

	// Should set s.onUserQuit()!
	fun start(s: KSketch, onStart: () -> Unit, onError: (Exception) -> Unit)
	fun stop(s: KSketch)

}

interface IPGraphics : IPGraphicsEnv, IPGraphicsContext, IPImage {

	var setupFunc: (IPGraphics.() -> Unit)?
	var drawFunc: IPGraphics.() -> Unit

	val mouseX: Float
	val mouseY: Float
	val pmouseX: Float
	val pmouseY: Float

	//var shader: PShader

	fun clear()

	fun point(x: Float, y: Float)
	fun line(x1: Float, y1: Float, x2: Float, y2: Float)

	fun rect(x1: Float, y1: Float, x2: Float, y2: Float)
	fun ellipse(x: Float, y: Float, w: Float, h: Float)
	fun arc(x: Float, y: Float, w: Float, h: Float, start: Float, stop: Float, mode: Int)

	// Matrix stack

	fun pushMatrix()
	fun popMatrix()

	// Matrix transforms

	fun translate(x: Float, y: Float)

	fun rotate(angle: Float)

	fun scale(s: Float)
	fun scale(sx: Float, sy: Float)

	fun shearX(angle: Float)
	fun shearY(angle: Float)

	// Matrix more

	fun resetMatrix()

	fun applyMatrix(
		n00: Float, n01: Float, n02: Float,
		n10: Float, n11: Float, n12: Float
	)

	fun getMatrix(target: PMatrix2D): PMatrix2D
	fun setMatrix(source: PMatrix2D)

	// Screen and model transforms

	fun screenX(x: Float, y: Float): Float
	fun screenY(x: Float, y: Float): Float


	//fun beginShape()
	//fun beginShape(kind: Int)
	//fun endShape()
	//fun endShape(mode: Int)
	//
	//fun attrib(name: String, vararg values: Float)
	//fun attrib(name: String, vararg values: Int)
	//fun attrib(name: String, vararg values: Boolean)
	//
	//fun vertex(x: Float, y: Float)
	//fun vertex(x: Float, y: Float, z: Float)
	//fun vertex(x: Float, y: Float, u: Float, v: Float)
	//fun vertex(x: Float, y: Float, z: Float, u: Float, v: Float)
	//fun vertex(v: FloatArray)
	//
	//fun normal(x: Float, y: Float, z: Float)
	//
	//fun beginContour()
	//fun endContour()

	//fun loadShape(path: Path): PShape?
	//	= loadShape(path, null)
	//fun loadShape(path: Path, options: String?): PShape?
	//
	//fun createShape(): PShape
	//fun createShape(type: Int): PShape
	//fun createShape(kind: Int, vararg p: Float)
	//
	//fun loadShader(fragPath: Path): PShader
	//fun loadShader(fragPath: Path, vertPath: Path): PShader

	//fun clip(a: Float, b: Float, c: Float, d: Float)

}
