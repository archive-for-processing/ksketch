/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2005-12 Ben Fry and Casey Reas

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

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

import kotlin.math.*

data class PMatrix3D(
	var m00: Float = 1f, var m01: Float = 0f, var m02: Float = 0f, var m03: Float = 0f,
	var m10: Float = 0f, var m11: Float = 1f, var m12: Float = 0f, var m13: Float = 0f,
	var m20: Float = 0f, var m21: Float = 0f, var m22: Float = 1f, var m23: Float = 0f,
	var m30: Float = 0f, var m31: Float = 0f, var m32: Float = 0f, var m33: Float = 1f
) : PMatrix {

	// locally allocated version to avoid allocating new memory
	protected val inverseCopy = PMatrix3D()

	constructor(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float) : this() {
		set(m00, m01, m02, 0f, m10, m11, m12, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
	}

	constructor(matrix: PMatrix) : this() {
		set(matrix)
	}

	override fun reset() {
		set(1f, 0f, 0f, 0f,
			0f, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun get(): PMatrix3D {
		val outgoing = PMatrix3D()
		outgoing.set(this)
		return outgoing
	}

	override fun get(target: FloatArray): FloatArray {
		var tgt = target
		if (tgt.size != 16)
			tgt = FloatArray(16)
		tgt[ 0] = m00; tgt[ 1] = m01; tgt[ 2] = m02; tgt[ 3] = m03;
		tgt[ 4] = m10; tgt[ 5] = m11; tgt[ 6] = m12; tgt[ 7] = m13;
		tgt[ 8] = m20; tgt[ 9] = m21; tgt[10] = m22; tgt[11] = m23;
		tgt[12] = m30; tgt[13] = m31; tgt[14] = m32; tgt[15] = m33

		return tgt
	}

	override fun set(src: PMatrix) {
		if (src is PMatrix3D) {
			set(src.m00, src.m01, src.m02, src.m03,
				src.m10, src.m11, src.m12, src.m13,
				src.m20, src.m21, src.m22, src.m23,
				src.m30, src.m31, src.m32, src.m33)
		} else {
			val _src = src as PMatrix2D
			set(_src.m00, _src.m01, 0f, _src.m02,
				_src.m10, _src.m11, 0f, _src.m12,
				0f, 0f, 1f, 0f,
				0f, 0f, 0f, 1f)
		}
	}

	override fun set(source: FloatArray) {
		if (source.size == 6) {
			set(source[0], source[1], source[2],
				source[3], source[4], source[5])
		} else if (source.size == 16) {
			m00 = source[ 0]; m01 = source[ 1]; m02 = source[ 2]; m03 = source[ 3]
			m10 = source[ 4]; m11 = source[ 5]; m12 = source[ 6]; m13 = source[ 7]
			m20 = source[ 8]; m21 = source[ 9]; m22 = source[10]; m23 = source[11]
			m30 = source[12]; m31 = source[13]; m32 = source[14]; m33 = source[15]
		}
	}

	override fun set(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float) {
		set(m00, m01, 0f, m02,
			m10, m11, 0f, m12,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun set(m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float, m20: Float, m21: Float, m22: Float, m23: Float, m30: Float, m31: Float, m32: Float, m33: Float) {
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23
		this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33
	}

	override fun translate(tx: Float, ty: Float) {
		translate(tx, ty, 0f)
	}

	override fun translate(tx: Float, ty: Float, tz: Float) {
		m03 += tx * m00 + ty * m01 + tz * m02
		m13 += tx * m10 + ty * m11 + tz * m12
		m23 += tx * m20 + ty * m21 + tz * m22
		m33 += tx * m30 + ty * m31 + tz * m32
	}

	override fun rotate(angle: Float) {
		rotateZ(angle)
	}

	override fun rotateX(angle: Float) {
		val c = cos(angle)
		val s = sin(angle)
		apply(1f, 0f, 0f, 0f, 0f, c, -s, 0f, 0f, s, c, 0f, 0f, 0f, 0f, 1f)
	}

	override fun rotateY(angle: Float) {
		val c = cos(angle)
		val s = sin(angle)
		apply(c, 0f, s, 0f, 0f, 1f, 0f, 0f, -s, 0f, c, 0f, 0f, 0f, 0f, 1f)
	}

	override fun rotateZ(angle: Float) {
		val c = cos(angle)
		val s = sin(angle)
		apply(c, -s, 0f, 0f, s, c, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
	}

	override fun rotate(angle: Float, v0: Float, v1: Float, v2: Float) {
		var p0 = v0; var p1 = v1; var p2 = v2
		val norm2 = p0 * p0 + p1 * p1 + p2 * p2
		if (norm2 < EPSILON) {
			// The vector is zero, cannot apply rotation.
			return
		}

		if (Math.abs(norm2 - 1) > EPSILON) {
			// The rotation vector is not normalized.
			val norm = sqrt(norm2)
			p0 /= norm
			p1 /= norm
			p2 /= norm
		}

		val c = cos(angle)
		val s = sin(angle)
		val t = 1.0f - c

		apply(t * p0 * p0 + c, t * p0 * p1 - s * p2, t * p0 * p2 + s * p1, 0f,
			t * p0 * p1 + s * p2, t * p1 * p1 + c, t * p1 * p2 - s * p0, 0f,
			t * p0 * p2 - s * p1, t * p1 * p2 + s * p0, t * p2 * p2 + c, 0f,
			0f, 0f, 0f, 1f)
	}

	operator fun times(s: Float)
		= this.apply { scale(s) }

	override fun scale(s: Float) {
		scale(s, s, s)
	}

	override fun scale(sx: Float, sy: Float) {
		scale(sx, sy, 1f)
	}

	override fun scale(x: Float, y: Float, z: Float) {
		m00 *= x; m01 *= y; m02 *= z
		m10 *= x; m11 *= y; m12 *= z
		m20 *= x; m21 *= y; m22 *= z
		m30 *= x; m31 *= y; m32 *= z
	}

	override fun shearX(angle: Float) {
		val t = Math.tan(angle.toDouble()).toFloat()
		apply(1f, t, 0f, 0f,
			0f, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun shearY(angle: Float) {
		val t = Math.tan(angle.toDouble()).toFloat()
		apply(1f, 0f, 0f, 0f,
			t, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun apply(source: PMatrix) {
		if (source is PMatrix2D) {
			apply(source)
		} else if (source is PMatrix3D) {
			apply(source)
		}
	}

	override fun apply(source: PMatrix2D) {
		apply(source.m00, source.m01, 0f, source.m02,
			source.m10, source.m11, 0f, source.m12,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun apply(source: PMatrix3D) {
		apply(source.m00, source.m01, source.m02, source.m03,
			source.m10, source.m11, source.m12, source.m13,
			source.m20, source.m21, source.m22, source.m23,
			source.m30, source.m31, source.m32, source.m33)
	}

	override fun apply(n00: Float, n01: Float, n02: Float,
					   n10: Float, n11: Float, n12: Float) {
		apply(n00, n01, 0f, n02,
			n10, n11, 0f, n12,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun apply(n00: Float, n01: Float, n02: Float, n03: Float,
					   n10: Float, n11: Float, n12: Float, n13: Float,
					   n20: Float, n21: Float, n22: Float, n23: Float,
					   n30: Float, n31: Float, n32: Float, n33: Float) {

		val r00 = m00 * n00 + m01 * n10 + m02 * n20 + m03 * n30
		val r01 = m00 * n01 + m01 * n11 + m02 * n21 + m03 * n31
		val r02 = m00 * n02 + m01 * n12 + m02 * n22 + m03 * n32
		val r03 = m00 * n03 + m01 * n13 + m02 * n23 + m03 * n33

		val r10 = m10 * n00 + m11 * n10 + m12 * n20 + m13 * n30
		val r11 = m10 * n01 + m11 * n11 + m12 * n21 + m13 * n31
		val r12 = m10 * n02 + m11 * n12 + m12 * n22 + m13 * n32
		val r13 = m10 * n03 + m11 * n13 + m12 * n23 + m13 * n33

		val r20 = m20 * n00 + m21 * n10 + m22 * n20 + m23 * n30
		val r21 = m20 * n01 + m21 * n11 + m22 * n21 + m23 * n31
		val r22 = m20 * n02 + m21 * n12 + m22 * n22 + m23 * n32
		val r23 = m20 * n03 + m21 * n13 + m22 * n23 + m23 * n33

		val r30 = m30 * n00 + m31 * n10 + m32 * n20 + m33 * n30
		val r31 = m30 * n01 + m31 * n11 + m32 * n21 + m33 * n31
		val r32 = m30 * n02 + m31 * n12 + m32 * n22 + m33 * n32
		val r33 = m30 * n03 + m31 * n13 + m32 * n23 + m33 * n33

		m00 = r00; m01 = r01; m02 = r02; m03 = r03
		m10 = r10; m11 = r11; m12 = r12; m13 = r13
		m20 = r20; m21 = r21; m22 = r22; m23 = r23
		m30 = r30; m31 = r31; m32 = r32; m33 = r33
	}

	override fun preApply(left: PMatrix2D) {
		preApply(left.m00, left.m01, 0f, left.m02,
			left.m10, left.m11, 0f, left.m12,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun preApply(left: PMatrix) {
		if (left is PMatrix2D) {
			preApply(left)
		} else if (left is PMatrix3D) {
			preApply(left)
		}
	}

	override fun preApply(left: PMatrix3D) {
		preApply(left.m00, left.m01, left.m02, left.m03,
			left.m10, left.m11, left.m12, left.m13,
			left.m20, left.m21, left.m22, left.m23,
			left.m30, left.m31, left.m32, left.m33)
	}

	override fun preApply(n00: Float, n01: Float, n02: Float,
						  n10: Float, n11: Float, n12: Float) {
		preApply(n00, n01, 0f, n02,
			n10, n11, 0f, n12,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f)
	}

	override fun preApply(n00: Float, n01: Float, n02: Float, n03: Float,
						  n10: Float, n11: Float, n12: Float, n13: Float,
						  n20: Float, n21: Float, n22: Float, n23: Float,
						  n30: Float, n31: Float, n32: Float, n33: Float) {

		val r00 = n00 * m00 + n01 * m10 + n02 * m20 + n03 * m30
		val r01 = n00 * m01 + n01 * m11 + n02 * m21 + n03 * m31
		val r02 = n00 * m02 + n01 * m12 + n02 * m22 + n03 * m32
		val r03 = n00 * m03 + n01 * m13 + n02 * m23 + n03 * m33

		val r10 = n10 * m00 + n11 * m10 + n12 * m20 + n13 * m30
		val r11 = n10 * m01 + n11 * m11 + n12 * m21 + n13 * m31
		val r12 = n10 * m02 + n11 * m12 + n12 * m22 + n13 * m32
		val r13 = n10 * m03 + n11 * m13 + n12 * m23 + n13 * m33

		val r20 = n20 * m00 + n21 * m10 + n22 * m20 + n23 * m30
		val r21 = n20 * m01 + n21 * m11 + n22 * m21 + n23 * m31
		val r22 = n20 * m02 + n21 * m12 + n22 * m22 + n23 * m32
		val r23 = n20 * m03 + n21 * m13 + n22 * m23 + n23 * m33

		val r30 = n30 * m00 + n31 * m10 + n32 * m20 + n33 * m30
		val r31 = n30 * m01 + n31 * m11 + n32 * m21 + n33 * m31
		val r32 = n30 * m02 + n31 * m12 + n32 * m22 + n33 * m32
		val r33 = n30 * m03 + n31 * m13 + n32 * m23 + n33 * m33

		m00 = r00; m01 = r01; m02 = r02; m03 = r03
		m10 = r10; m11 = r11; m12 = r12; m13 = r13
		m20 = r20; m21 = r21; m22 = r22; m23 = r23
		m30 = r30; m31 = r31; m32 = r32; m33 = r33
	}

	override fun mult(source: PVector, target: PVector?): PVector {
		var tgt = target
		if (tgt == null)
			tgt = PVector()
		tgt.set(m00 * source.x + m01 * source.y + m02 * source.z + m03,
			m10 * source.x + m11 * source.y + m12 * source.z + m13,
			m20 * source.x + m21 * source.y + m22 * source.z + m23)
		return tgt
	}

	override fun mult(source: FloatArray, target: FloatArray): FloatArray {
		var tgt = target
		if (tgt.size < 3) {
			tgt = FloatArray(3)
		}
		if (source === tgt) {
			throw RuntimeException("The source and target vectors used in " + "PMatrix3D.mult() cannot be identical.")
		}
		if (tgt.size == 3) {
			tgt[0] = m00 * source[0] + m01 * source[1] + m02 * source[2] + m03
			tgt[1] = m10 * source[0] + m11 * source[1] + m12 * source[2] + m13
			tgt[2] = m20 * source[0] + m21 * source[1] + m22 * source[2] + m23
		} else if (tgt.size > 3) {
			tgt[0] = m00 * source[0] + m01 * source[1] + m02 * source[2] + m03 * source[3]
			tgt[1] = m10 * source[0] + m11 * source[1] + m12 * source[2] + m13 * source[3]
			tgt[2] = m20 * source[0] + m21 * source[1] + m22 * source[2] + m23 * source[3]
			tgt[3] = m30 * source[0] + m31 * source[1] + m32 * source[2] + m33 * source[3]
		}
		return tgt
	}

	fun multX(x: Float, y: Float): Float =
		m00 * x + m01 * y + m03

	fun multY(x: Float, y: Float): Float =
		m10 * x + m11 * y + m13

	fun multX(x: Float, y: Float, z: Float): Float =
		m00 * x + m01 * y + m02 * z + m03

	fun multY(x: Float, y: Float, z: Float): Float =
		m10 * x + m11 * y + m12 * z + m13

	fun multZ(x: Float, y: Float, z: Float): Float =
		m20 * x + m21 * y + m22 * z + m23

	fun multW(x: Float, y: Float, z: Float): Float =
		m30 * x + m31 * y + m32 * z + m33

	fun multX(x: Float, y: Float, z: Float, w: Float): Float =
		m00 * x + m01 * y + m02 * z + m03 * w

	fun multY(x: Float, y: Float, z: Float, w: Float): Float =
		m10 * x + m11 * y + m12 * z + m13 * w

	fun multZ(x: Float, y: Float, z: Float, w: Float): Float =
		m20 * x + m21 * y + m22 * z + m23 * w

	fun multW(x: Float, y: Float, z: Float, w: Float): Float =
		m30 * x + m31 * y + m32 * z + m33 * w

	override fun transpose() {
		var temp: Float
		temp = m01; m01 = m10; m10 = temp
		temp = m02; m02 = m20; m20 = temp
		temp = m03; m03 = m30; m30 = temp
		temp = m12; m12 = m21; m21 = temp
		temp = m13; m13 = m31; m31 = temp
		temp = m23; m23 = m32; m32 = temp
	}

	override fun invert(): Boolean {
		val determinant = determinant()
		if (determinant == 0f) {
			return false
		}

		// first row
		val t00 = determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33)
		val t01 = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33)
		val t02 = determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33)
		val t03 = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32)

		// second row
		val t10 = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33)
		val t11 = determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33)
		val t12 = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33)
		val t13 = determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32)

		// third row
		val t20 = determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33)
		val t21 = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33)
		val t22 = determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33)
		val t23 = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32)

		// fourth row
		val t30 = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23)
		val t31 = determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23)
		val t32 = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23)
		val t33 = determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22)

		// transpose and divide by the determinant
		m00 = t00 / determinant
		m01 = t10 / determinant
		m02 = t20 / determinant
		m03 = t30 / determinant

		m10 = t01 / determinant
		m11 = t11 / determinant
		m12 = t21 / determinant
		m13 = t31 / determinant

		m20 = t02 / determinant
		m21 = t12 / determinant
		m22 = t22 / determinant
		m23 = t32 / determinant

		m30 = t03 / determinant
		m31 = t13 / determinant
		m32 = t23 / determinant
		m33 = t33 / determinant

		return true
	}

	private fun determinant3x3(t00: Float, t01: Float, t02: Float, t10: Float, t11: Float, t12: Float, t20: Float, t21: Float, t22: Float) =
		t00 * (t11 * t22 - t12 * t21) +
		t01 * (t12 * t20 - t10 * t22) +
		t02 * (t10 * t21 - t11 * t20)

	override fun determinant(): Float {
		var f = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32
			- m13 * m22 * m31
			- m11 * m23 * m32
			- m12 * m21 * m33)
		f -= m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32
			- m13 * m22 * m30
			- m10 * m23 * m32
			- m12 * m20 * m33)
		f += m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31
			- m13 * m21 * m30
			- m10 * m23 * m31
			- m11 * m20 * m33)
		f -= m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31
			- m12 * m21 * m30
			- m10 * m22 * m31
			- m11 * m20 * m32)
		return f
	}

	protected fun invTranslate(tx: Float, ty: Float, tz: Float) {
		preApply(1f, 0f, 0f, -tx,
			0f, 1f, 0f, -ty,
			0f, 0f, 1f, -tz,
			0f, 0f, 0f, 1f)
	}

	protected fun invRotateX(angle: Float) {
		val c = cos(-angle)
		val s = sin(-angle)
		preApply(1f, 0f, 0f, 0f, 0f, c, -s, 0f, 0f, s, c, 0f, 0f, 0f, 0f, 1f)
	}

	protected fun invRotateY(angle: Float) {
		val c = cos(-angle)
		val s = sin(-angle)
		preApply(c, 0f, s, 0f, 0f, 1f, 0f, 0f, -s, 0f, c, 0f, 0f, 0f, 0f, 1f)
	}

	protected fun invRotateZ(angle: Float) {
		val c = cos(-angle)
		val s = sin(-angle)
		preApply(c, -s, 0f, 0f, s, c, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
	}

	protected fun invRotate(angle: Float, v0: Float, v1: Float, v2: Float) {
		//TODO should make sure this vector is normalized

		val c = cos(-angle)
		val s = sin(-angle)
		val t = 1.0f - c

		preApply(t * v0 * v0 + c, t * v0 * v1 - s * v2, t * v0 * v2 + s * v1, 0f,
			t * v0 * v1 + s * v2, t * v1 * v1 + c, t * v1 * v2 - s * v0, 0f,
			t * v0 * v2 - s * v1, t * v1 * v2 + s * v0, t * v2 * v2 + c, 0f,
			0f, 0f, 0f, 1f)
	}

	protected fun invScale(x: Float, y: Float, z: Float) {
		preApply(1 / x, 0f, 0f, 0f, 0f, 1 / y, 0f, 0f, 0f, 0f, 1 / z, 0f, 0f, 0f, 0f, 1f)
	}

	protected fun invApply(n00: Float, n01: Float, n02: Float, n03: Float, n10: Float, n11: Float, n12: Float, n13: Float, n20: Float, n21: Float, n22: Float, n23: Float, n30: Float, n31: Float, n32: Float, n33: Float): Boolean {
		inverseCopy.set(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33)
		if (!inverseCopy.invert()) {
			return false
		}
		preApply(inverseCopy)
		return true
	}

	fun print() {
		var big = Math.abs(max(max(max(max(abs(m00), abs(m01)),
			max(abs(m02), abs(m03))),
			max(max(abs(m10), abs(m11)),
				max(abs(m12), abs(m13)))),
			max(max(max(abs(m20), abs(m21)),
				max(abs(m22), abs(m23))),
				max(max(abs(m30), abs(m31)),
					max(abs(m32), abs(m33)))))).toInt()

		var digits = 1
		if (java.lang.Float.isNaN(big.toFloat()) || java.lang.Float.isInfinite(big.toFloat())) {  // avoid infinite loop
			digits = 5
		} else {
			// cheap log()
			while (true) {
				big /= 10
				if (big == 0)
					break;
				digits++
			}
		}

		println(nfs(m00, digits, 4) + " " +
			nfs(m01, digits, 4) + " " +
			nfs(m02, digits, 4) + " " +
			nfs(m03, digits, 4))

		println(nfs(m10, digits, 4) + " " +
			nfs(m11, digits, 4) + " " +
			nfs(m12, digits, 4) + " " +
			nfs(m13, digits, 4))

		println(nfs(m20, digits, 4) + " " +
			nfs(m21, digits, 4) + " " +
			nfs(m22, digits, 4) + " " +
			nfs(m23, digits, 4))

		println(nfs(m30, digits, 4) + " " +
			nfs(m31, digits, 4) + " " +
			nfs(m32, digits, 4) + " " +
			nfs(m33, digits, 4))

		println()
	}

}
