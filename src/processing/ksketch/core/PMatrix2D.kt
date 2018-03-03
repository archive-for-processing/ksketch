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

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class PMatrix2D : PMatrix {

	var m00 = 1f; var m01 = 0f; var m02 = 0f
	var m10 = 0f; var m11 = 1f; var m12 = 0f

	constructor()

	constructor(matrix: PMatrix) {
		set(matrix)
	}

	constructor(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float) {
		set(m00, m01, m02, m10, m11, m12)
	}

	override fun reset() {
		m00 = 1f; m01 = 0f; m02 = 0f
		m10 = 0f; m11 = 1f; m12 = 0f
	}

	override fun get(): PMatrix2D {
		val outgoing = PMatrix2D()
		outgoing.set(this)
		return outgoing
	}

	override fun get(target: FloatArray): FloatArray {
		var tgt = target
		if (tgt.size != 6)
			tgt = FloatArray(6)
		tgt[0] = m00
		tgt[1] = m01
		tgt[2] = m02

		tgt[3] = m10
		tgt[4] = m11
		tgt[5] = m12

		return tgt
	}

	override fun set(src: PMatrix) {
		if (src is PMatrix2D)
			set(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12)
		else
			throw IllegalArgumentException("PMatrix2D.set() only accepts PMatrix2D objects.")
	}

	override fun set(source: FloatArray) {
		m00 = source[0]
		m01 = source[1]
		m02 = source[2]

		m10 = source[3]
		m11 = source[4]
		m12 = source[5]
	}

	override fun set(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float) {
		this.m00 = m00
		this.m01 = m01
		this.m02 = m02
		this.m10 = m10
		this.m11 = m11
		this.m12 = m12
	}

	// PMatrix3D set
	override fun set(m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float, m20: Float, m21: Float, m22: Float, m23: Float, m30: Float, m31: Float, m32: Float, m33: Float) {}

	override fun translate(tx: Float, ty: Float) {
		m02 += tx * m00 + ty * m01
		m12 += tx * m10 + ty * m11
	}

	override fun translate(tx: Float, ty: Float, tz: Float) =
		throw IllegalArgumentException("Cannot use translate(x, y, z) on a PMatrix2D.")

	// Implementation roughly based on AffineTransform.
	override fun rotate(angle: Float) {
		val s = sin(angle)
		val c = cos(angle)

		var temp1 = m00
		var temp2 = m01
		m00 = c * temp1 + s * temp2
		m01 = -s * temp1 + c * temp2
		temp1 = m10
		temp2 = m11
		m10 = c * temp1 + s * temp2
		m11 = -s * temp1 + c * temp2
	}

	override fun rotateX(angle: Float) =
		throw IllegalArgumentException("Cannot use rotateX() on a PMatrix2D.")

	override fun rotateY(angle: Float) =
		throw IllegalArgumentException("Cannot use rotateY() on a PMatrix2D.")

	override fun rotateZ(angle: Float) {
		rotate(angle)
	}

	override fun rotate(angle: Float, v0: Float, v1: Float, v2: Float) =
		throw IllegalArgumentException("Cannot use this version of rotate() on a PMatrix2D.")

	operator fun times(s: Float): PMatrix2D =
		this.apply { scale(s) }

	override fun scale(s: Float) {
		scale(s, s)
	}

	override fun scale(sx: Float, sy: Float) {
		m00 *= sx; m01 *= sy
		m10 *= sx; m11 *= sy
	}

	override fun scale(x: Float, y: Float, z: Float) =
		throw IllegalArgumentException("Cannot use this version of scale() on a PMatrix2D.")

	override fun shearX(angle: Float) {
		apply(1f, 0f, 1f, tan(angle), 0f, 0f)
	}

	override fun shearY(angle: Float) {
		apply(1f, 0f, 1f, 0f, tan(angle), 0f)
	}

	override fun apply(source: PMatrix) {
		if (source is PMatrix2D)
			apply(source)
		else if (source is PMatrix3D)
			apply(source as PMatrix3D)
	}

	override fun apply(source: PMatrix2D) {
		apply(source.m00, source.m01, source.m02,
			source.m10, source.m11, source.m12)
	}


	override fun apply(source: PMatrix3D) =
		throw IllegalArgumentException("Cannot use apply(PMatrix3D) on a PMatrix2D.")

	override fun apply(n00: Float, n01: Float, n02: Float, n10: Float, n11: Float, n12: Float) {
		var t0 = m00
		var t1 = m01
		m00 = n00 * t0 + n10 * t1
		m01 = n01 * t0 + n11 * t1
		m02 += n02 * t0 + n12 * t1

		t0 = m10
		t1 = m11
		m10 = n00 * t0 + n10 * t1
		m11 = n01 * t0 + n11 * t1
		m12 += n02 * t0 + n12 * t1
	}

	override fun apply(
		n00: Float, n01: Float, n02: Float, n03: Float,
		n10: Float, n11: Float, n12: Float, n13: Float,
		n20: Float, n21: Float, n22: Float, n23: Float,
		n30: Float, n31: Float, n32: Float, n33: Float) {
		throw IllegalArgumentException("Cannot use this version of apply() on a PMatrix2D.")
	}

	override fun preApply(left: PMatrix) {
		if (left is PMatrix2D) {
			preApply(left)
		} else if (left is PMatrix3D) {
			preApply(left)
		}
	}

	override fun preApply(left: PMatrix2D) {
		preApply(left.m00, left.m01, left.m02,
			left.m10, left.m11, left.m12)
	}

	override fun preApply(left: PMatrix3D) {
		throw IllegalArgumentException("Cannot use preApply(PMatrix3D) on a PMatrix2D.")
	}


	override fun preApply(n00: Float, n01: Float, n02: Float, n10: Float, n11: Float, n12: Float) {
		var o02 = n02
		var o12 = n12
		var t0 = m02
		var t1 = m12
		o02 += t0 * n00 + t1 * n01
		o12 += t0 * n10 + t1 * n11

		m02 = o02
		m12 = o12

		t0 = m00
		t1 = m10
		m00 = t0 * n00 + t1 * n01
		m10 = t0 * n10 + t1 * n11

		t0 = m01
		t1 = m11
		m01 = t0 * n00 + t1 * n01
		m11 = t0 * n10 + t1 * n11
	}

	override fun preApply(n00: Float, n01: Float, n02: Float, n03: Float, n10: Float, n11: Float, n12: Float, n13: Float, n20: Float, n21: Float, n22: Float, n23: Float, n30: Float, n31: Float, n32: Float, n33: Float) =
		throw IllegalArgumentException("Cannot use this version of preApply() on a PMatrix2D.")

	override fun transpose() {}

	override fun mult(source: PVector, target: PVector?): PVector {
		var tgt = target
		if (tgt == null)
			tgt = PVector()
		tgt.x = m00 * source.x + m01 * source.y + m02
		tgt.y = m10 * source.x + m11 * source.y + m12
		return tgt
	}

	override fun mult(source: FloatArray, target: FloatArray): FloatArray {
		var out = target
		if (out.size != 2) {
			out = FloatArray(2)
		}

		if (source.contentEquals(out)) {
			val tx = m00 * source[0] + m01 * source[1] + m02
			val ty = m10 * source[0] + m11 * source[1] + m12

			out[0] = tx
			out[1] = ty

		} else {
			out[0] = m00 * source[0] + m01 * source[1] + m02
			out[1] = m10 * source[0] + m11 * source[1] + m12
		}

		return out
	}

	fun multX(x: Float, y: Float): Float {
		return m00 * x + m01 * y + m02
	}

	fun multY(x: Float, y: Float): Float {
		return m10 * x + m11 * y + m12
	}

	// Implementation stolen from OpenJDK.
	override fun invert(): Boolean {
		val determinant = determinant()
		if (Math.abs(determinant) <= java.lang.Float.MIN_VALUE) {
			return false
		}

		val t00 = m00
		val t01 = m01
		val t02 = m02
		val t10 = m10
		val t11 = m11
		val t12 = m12

		m00 = t11 / determinant
		m10 = -t10 / determinant
		m01 = -t01 / determinant
		m11 = t00 / determinant
		m02 = (t01 * t12 - t11 * t02) / determinant
		m12 = (t10 * t02 - t00 * t12) / determinant

		return true
	}

	override fun determinant(): Float {
		return m00 * m11 - m01 * m10
	}

	protected val isIdentity: Boolean
		get() =
			m00 == 1f && m01 == 0f && m02 == 0f &&
				m10 == 0f && m11 == 1f && m12 == 0f

	protected val isWarped: Boolean
		get() = m00 != 1f || m01 != 0f && m10 != 0f || m11 != 1f

	fun print() {
		var big = arrayOf(abs(m00), abs(m01), abs(m02), abs(m10), abs(m11), abs(m12)).max()!!

		var digits = 1
		if (big.isNaN() || big.isInfinite()) {  // avoid infinite loop
			digits = 5
		} else {
			// cheap log()
			while (true) {
				big /= 10
				if (big == 0f)
					break;
				digits++
			}
		}

		println(nfs(m00, digits, 4) + " " +
			nfs(m01, digits, 4) + " " +
			nfs(m02, digits, 4))

		println(nfs(m10, digits, 4) + " " +
			nfs(m11, digits, 4) + " " +
			nfs(m12, digits, 4))

		println()
	}

}
