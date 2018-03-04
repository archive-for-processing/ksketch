package processing.ksketch.core

import java.util.Random

fun Int.clamp(min: Int, max: Int): Int = Math.max(min, Math.min(this, max))
fun Float.clamp(min: Float, max: Float): Float = Math.max(min, Math.min(this, max))
fun lerp(start: Float, stop: Float, amt: Float) = start + (stop-start) * amt
fun maxl(vararg list: Int): Int {
	return list.max() ?: 0
}

val Float.radToDeg get() = this * RAD_TO_DEG
val Float.degToRad get() = this * DEG_TO_RAD

private val rng by lazy { Random() }

fun random(high: Float): Float {
	// Avoid an infinite loop when 0 or NaN are passed in
	if (high == 0f || high.isNaN())
		return 0f

	var value: Float
	do {
		value = rng.nextFloat() * high
	} while (value == high)

	return value
}

fun random(low: Float, high: Float): Float {
	if (low >= high)
		return low

	val diff = high - low
	var value: Float
	do {
		value = random(diff) + low
	} while (value == high)

	return value
}

fun randomGaussian(): Float =
	rng.nextGaussian().toFloat()

fun randomSeed(seed: Long): Unit =
	rng.setSeed(seed)
