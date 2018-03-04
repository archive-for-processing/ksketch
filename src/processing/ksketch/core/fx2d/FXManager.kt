package processing.ksketch.core.fx2d

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

import processing.ksketch.core.KSketch

object FXManager {

	private val initLock = CountDownLatch(1)
	var isFxRunning = false

	class SketchInfo(
		val stage: Stage,
		val drawTimer: AnimationTimer
	)

	internal class FXMainApp : Application() {

		companion object {
			internal val sketches = HashMap<FX2D, SketchInfo>()
		}

		override fun start(primaryStage: Stage) {
			// Initialized!
			initLock.countDown()
		}

	}

	fun addSketch(k: KSketch, s: FX2D) {
		val block = CountDownLatch(1)

		if (!isFxRunning) {
			isFxRunning = true
			thread(start = true, isDaemon = true, name = "App Launch thread") {
				Application.launch(FXMainApp::class.java)
			}
		}

		FXManager.initLock.await()

		Platform.runLater {
			synchronized(this) {
				if (FXMainApp.sketches[s] != null)
					throw IllegalArgumentException("Sketch already added")

				val stage = Stage()
				stage.onCloseRequest = EventHandler {
					k.onUserQuit()
					synchronized(this) {
						FXMainApp.sketches.remove(s)
					}
				}

				val root = Group()
				val w = s.width.toDouble()
				val h = s.height.toDouble()

				val canvas = Canvas(w, h)
				s.gc = canvas.graphicsContext2D
				root.children.add(canvas)

				val smooth = when(s.smooth) {
					0 -> SceneAntialiasing.DISABLED
					else -> SceneAntialiasing.BALANCED
				}

				stage.scene = Scene(root, w, h, true, smooth)
				stage.isResizable = false
				stage.show()

				root.onMouseClicked = EventHandler { event ->
					s._emouseX = event.sceneX.toFloat()
					s._emouseY = event.sceneY.toFloat()
				}

				val drawTimer = object : AnimationTimer() {
					override fun handle(now: Long) {
						s.prepareFrame()
						s.drawFunc(s)
					}
				}

				s.setupFunc?.invoke(s)
				s.onStart()
				drawTimer.start()

				FXMainApp.sketches[s] = SketchInfo(stage, drawTimer)
				block.countDown()
			}
		}

		// Wait until sketch started
		block.await()
	}

	fun removeSketch(s: FX2D) {
		val block = CountDownLatch(1)

		Platform.runLater {
			synchronized(this) {
				val info = FXMainApp.sketches[s]
					?: throw IllegalArgumentException("Sketch not running")

				info.drawTimer.stop()
				info.stage.close()

				FXMainApp.sketches.remove(s)
				block.countDown()
			}
		}

		// Wait until sketch removed
		block.await()
	}
}
