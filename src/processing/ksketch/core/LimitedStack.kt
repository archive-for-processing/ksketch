package processing.ksketch.core

import java.util.*

open class LimitedStack<E>(val limit: Int) : Stack<E>() {

	override fun push(item: E): E? {
		return if (count() > limit) null
			else super.push(item)
	}

}