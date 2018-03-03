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

package processing.ksketch.core.fx2d

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

class FXApp : Application() {
	companion object {
		// Cannot be an inner class of PGraphicsFX2D (created through reflection)
		// but needs to access the outer class
		internal lateinit var outer: PGraphicsFX2D
		internal lateinit var instance: FXApp
	}

	override fun start(primaryStage: Stage) {
		try {
			instance = this

			val root = Group()
			val w = outer.w.toDouble()
			val h = outer.h.toDouble()
			val canvas = Canvas(w, h)
			outer.gc = canvas.graphicsContext2D
			root.children.add(canvas)

			val smooth = when(outer.smooth) {
				0 -> SceneAntialiasing.DISABLED
				else -> SceneAntialiasing.BALANCED
			}

			primaryStage.scene = Scene(root, w, h, true, smooth)
			primaryStage.show()

			outer.drawTimer = object : AnimationTimer() {
				override fun handle(now: Long) {
					outer.resetMatrix()
					outer.drawFunc(outer)
				}
			}
			outer.drawTimer.start()

			outer.setupFunc?.invoke(outer)
			outer.onStart()
		} catch (e: Exception) {
			outer.onError(e)
		}
	}
}
