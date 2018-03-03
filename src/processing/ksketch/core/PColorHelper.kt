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

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// TODO Documentation
class PColorHelper(val withAlpha: Boolean = true) {

	private var _color: PColor = 0
	var color: PColor
		get() = _color
		set(value) {
			_color = value
			ai = ((value shr 24) and 0xFF)
			ri = ((value shr 16) and 0xFF)
			gi = ((value shr 8)  and 0xFF)
			bi = ( value         and 0xFF)
		}

	private var _red = PColorPart()
	var r: Float by _red.mapped
	var ri: Int  by _red.real

	private var _green = PColorPart()
	var g: Float by _green.mapped
	var gi: Int  by _green.real

	private var _blue = PColorPart()
	var b: Float by _blue.mapped
	var bi: Int  by _blue.real

	var hasAlpha = true
	private var _alpha = PColorPart({ real, _ -> hasAlpha = withAlpha && real < 0xFF })
	var a: Float by _alpha.mapped
	var ai: Int  by _alpha.real

	// Set the color value
	private fun recalc() {
		_color = (ai shl 24) or (ri shl 16) or (gi shl 8) or bi
	}

	fun copy(): PColorHelper {
		val p = PColorHelper()
		p.color = color
		return p
	}

	fun delegate(changed: ((PColor) -> Unit)? = null) = object : ReadWriteProperty<Any, PColor> {
		override operator fun getValue(thisRef: Any, property: KProperty<*>): PColor =
			color

		override operator fun setValue(thisRef: Any, property: KProperty<*>, value: PColor) {
			color = value
			changed?.invoke(value)
		}
	}


	// Provides delegates for mapping int color values to float color values
	inner class PColorPart(
		var intercept: ((real: Int, mapped: Float) -> Unit)? = null
	) {
		private var mappedValue: Float = 0f
		private var realValue: Int = 0

		val real = object : ReadWriteProperty<Any, Int> {
			override operator fun getValue(thisRef: Any, property: KProperty<*>): Int {
				return realValue
			}

			override operator fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
				realValue = value.clamp(0, 255)
				mappedValue = (realValue / 255f)
				recalc()
				intercept?.invoke(realValue, mappedValue)
			}
		}

		val mapped = object : ReadWriteProperty<Any, Float> {
			override fun getValue(thisRef: Any, property: KProperty<*>): Float {
				return mappedValue
			}

			override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
				mappedValue = value.clamp(0f, 1f)
				realValue = (255 * mappedValue).toInt()
				recalc()
				intercept?.invoke(realValue, mappedValue)
			}
		}
	}

}
