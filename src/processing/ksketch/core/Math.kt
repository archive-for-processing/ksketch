package processing.ksketch.core

fun Int.clamp(min: Int, max: Int): Int = Math.max(min, Math.min(this, max))
fun Float.clamp(min: Float, max: Float): Float = Math.max(min, Math.min(this, max))
fun lerp(start: Float, stop: Float, amt: Float) = start + (stop-start) * amt
fun maxl(vararg list: Int): Int {
	return list.max() ?: 0
}
