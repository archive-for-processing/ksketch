/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2005-08 Ben Fry and Casey Reas

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

interface PMatrix {

	fun reset()

	fun get(): PMatrix
	operator fun get(target: FloatArray): FloatArray

	fun set(src: PMatrix)
	fun set(source: FloatArray)
	fun set(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float)
	fun set(
		m00: Float, m01: Float, m02: Float, m03: Float,
		m10: Float, m11: Float, m12: Float, m13: Float,
		m20: Float, m21: Float, m22: Float, m23: Float,
		m30: Float, m31: Float, m32: Float, m33: Float
	)

	fun translate(tx: Float, ty: Float)
	fun translate(tx: Float, ty: Float, tz: Float)

	fun rotate(angle: Float)
	fun rotateX(angle: Float)
	fun rotateY(angle: Float)
	fun rotateZ(angle: Float)
	fun rotate(angle: Float, v0: Float, v1: Float, v2: Float)

	fun scale(s: Float)
	fun scale(sx: Float, sy: Float)
	fun scale(x: Float, y: Float, z: Float)

	fun shearX(angle: Float)
	fun shearY(angle: Float)

	fun apply(source: PMatrix)
	fun apply(source: PMatrix2D)
	fun apply(source: PMatrix3D)
	fun apply(n00: Float, n01: Float, n02: Float, n10: Float, n11: Float, n12: Float)
	fun apply(
		n00: Float, n01: Float, n02: Float, n03: Float,
		n10: Float, n11: Float, n12: Float, n13: Float,
		n20: Float, n21: Float, n22: Float, n23: Float,
		n30: Float, n31: Float, n32: Float, n33: Float
	)

	fun preApply(left: PMatrix)
	fun preApply(left: PMatrix2D)
	fun preApply(left: PMatrix3D)
	fun preApply(n00: Float, n01: Float, n02: Float, n10: Float, n11: Float, n12: Float)
	fun preApply(
		n00: Float, n01: Float, n02: Float, n03: Float,
		n10: Float, n11: Float, n12: Float, n13: Float,
		n20: Float, n21: Float, n22: Float, n23: Float,
		n30: Float, n31: Float, n32: Float, n33: Float
	)

	fun mult(source: PVector, target: PVector?): PVector
	fun mult(source: FloatArray, target: FloatArray): FloatArray

	fun transpose()

	fun invert(): Boolean

	fun determinant(): Float

}
