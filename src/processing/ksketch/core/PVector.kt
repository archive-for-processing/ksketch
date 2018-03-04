/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-17 The Processing Foundation
  Copyright (c) 2008-12 Ben Fry and Casey Reas
  Copyright (c) 2008 Dan Shiffman

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

data class PVector(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

	@Transient
	protected var array: FloatArray? = null

	fun set(x: Float, y: Float, z: Float): PVector {
		this.x = x
		this.y = y
		this.z = z
		return this
	}

	fun set(x: Float, y: Float): PVector {
		this.x = x
		this.y = y
		this.z = 0f
		return this
	}

	fun set(v: PVector): PVector {
		x = v.x
		y = v.y
		z = v.z
		return this
	}

	fun set(source: FloatArray): PVector {
		if (source.size >= 2) {
			x = source[0]
			y = source[1]
		}
		z = if (source.size >= 3) {
			source[2]
		} else {
			0f
		}
		return this
	}

	fun copy(): PVector {
		return PVector(x, y, z)
	}

	fun get(target: FloatArray?): FloatArray {
		if (target == null) {
			return floatArrayOf(x, y, z)
		}
		if (target.size >= 2) {
			target[0] = x
			target[1] = y
		}
		if (target.size >= 3) {
			target[2] = z
		}
		return target
	}

	fun mag(): Float {
		return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
	}

	fun magSq(): Float {
		return x * x + y * y + z * z
	}

	infix fun add(v: PVector) = plusAssign(v)

	operator fun plus(v: PVector) =
		PVector( x + v.x, y + v.y, z + v.z)

	operator fun plusAssign(v: PVector)
		{ x += v.x; y += v.y; z += v.z }

	fun add(x: Float, y: Float) =
		this.apply { this.x += x; this.y += y }

	fun add(x: Float, y: Float, z: Float) =
		this.apply { this.x += x; this.y += y; this.z += z }

	fun sub(v: PVector) = minusAssign(v)

	operator fun minus(v: PVector) =
		PVector( x - v.x, y - v.y, z - v.z)

	operator fun minusAssign(v: PVector)
		{ x -= v.x; y -= v.y; z -= v.z }

	fun minus(x: Float, y: Float) =
		this.apply { this.x -= x; this.y -= y }

	fun minus(x: Float, y: Float, z: Float) =
		this.apply { this.x -= x; this.y -= y; this.z -= z }

	fun mult(n: Float) = timesAssign(n)

	operator fun times(n: Float) =
		PVector(x * n, y * n, z * n)

	operator fun timesAssign(n: Float)
		{ x *= n; y *= n; z *= n }

	operator fun div(n: Float) =
		PVector(x / n, y / n, z / n)

	operator fun divAssign(n: Float)
		{ x /= n; y /= n; z /= n }

	infix fun dist(v: PVector): Float {
		val dx = x - v.x
		val dy = y - v.y
		val dz = z - v.z
		return sqrt((dx * dx + dy * dy + dz * dz))
	}

	infix fun dot(v: PVector) =
		x * v.x + y * v.y + z * v.z

	operator fun rangeTo(v: PVector) = dot(v)

	fun dot(x: Float, y: Float, z: Float) =
		this.x * x + this.y * y + this.z * z

	fun cross(v: PVector, target: PVector): PVector {
		val crossX = y * v.z - v.y * z
		val crossY = z * v.x - v.z * x
		val crossZ = x * v.y - v.x * y

		target.set(crossX, crossY, crossZ)
		return target
	}

	fun normalize(): PVector {
		val m = mag()
		if (m != 0f && m != 1f)
			div(m)
		return this
	}

	fun normalize(target: PVector): PVector {
		val m = mag()
		if (m > 0)
			target.set(x / m, y / m, z / m)
		else
			target.set(x, y, z)
		return target
	}

	fun limit(max: Float) = this.apply {
		if (magSq() > max * max) {
			normalize()
			mult(max)
		}
	}

	fun setMag(len: Float) = this.apply {
		normalize()
		mult(len)
	}

	fun setMag(target: PVector, len: Float): PVector {
		var tgt = target
		tgt = normalize(tgt)
		tgt.mult(len)
		return tgt
	}

	fun heading() = atan2(y, x)

	fun rotate(theta: Float): PVector {
		val temp = x
		// Might need to check for rounding errors like with angleBetween function?
		x = x * cos(theta) - y * sin(theta)
		y = temp * sin(theta) + y * cos(theta)
		return this
	}

	fun lerp(v: PVector, amt: Float): PVector {
		x = lerp(x, v.x, amt)
		y = lerp(y, v.y, amt)
		z = lerp(z, v.z, amt)
		return this
	}

	fun lerp(x: Float, y: Float, z: Float, amt: Float): PVector {
		this.x = lerp(this.x, x, amt)
		this.y = lerp(this.y, y, amt)
		this.z = lerp(this.z, z, amt)
		return this
	}

	override fun toString(): String {
		return "[ $x, $y, $z ]"
	}

	fun array(): FloatArray {
		if (array == null)
			array = FloatArray(3)
		array!![0] = x
		array!![1] = y
		array!![2] = z
		return array!!
	}

	companion object {
		fun fromAngle(angle: Float, target: PVector) = target.apply {
			set(cos(angle), sin(angle), 0f)
		}

		fun add(v1: PVector, v2: PVector, target: PVector) = target.apply {
			set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z)
		}

		fun dist(v1: PVector, v2: PVector): Float {
			val dx = v1.x - v2.x
			val dy = v1.y - v2.y
			val dz = v1.z - v2.z
			return sqrt(dx * dx + dy * dy + dz * dz)
		}

		fun dot(v1: PVector, v2: PVector) =
			v1.x * v2.x + v1.y * v2.y + v1.z * v2.z

		fun sub(v1: PVector, v2: PVector, target: PVector) = target.apply {
			set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)
		}

		fun mult(v: PVector, n: Float, target: PVector) = target.apply {
			set(v.x * n, v.y * n, v.z * n)
		}

		fun div(v: PVector, n: Float, target: PVector) = target.apply {
			set(v.x / n, v.y / n, v.z / n)
		}

		fun cross(v1: PVector, v2: PVector, target: PVector) = target.apply {
			val crossX = v1.y * v2.z - v2.y * v1.z
			val crossY = v1.z * v2.x - v2.z * v1.x
			val crossZ = v1.x * v2.y - v2.x * v1.y

			set(crossX, crossY, crossZ)
		}

		fun lerp(v1: PVector, v2: PVector, amt: Float): PVector {
			val v = v1.copy()
			v.lerp(v2, amt)
			return v
		}

		fun angleBetween(v1: PVector, v2: PVector): Float {
			if (v1.x == 0f && v1.y == 0f && v1.z == 0f) return 0.0f
			if (v2.x == 0f && v2.y == 0f && v2.z == 0f) return 0.0f

			val dot = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
			val v1mag = sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z)
			val v2mag = sqrt(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z)
			// This should be a number between -1 and 1, since it's "normalized"
			val amt = dot / (v1mag * v2mag)
			return when {
				amt <= -1 -> PI
				amt >= 1 -> 0f
				else -> acos(amt)
			}
		}

		fun random2D(): PVector =
			random2D(PVector())

		fun random2D(target: PVector): PVector =
			fromAngle(random(TAU), target)

		fun random3D(): PVector =
			random3D(PVector())

		fun random3D(target: PVector): PVector =
			target.apply {
				val angle = random(TAU)
				val vz = random(-1f, 1f)

				val vx = sqrt((1 - vz * vz)) * cos(angle)
				val vy = sqrt((1 - vz * vz)) * sin(angle)

				set(vx, vy, vz)
			}
	}

}
