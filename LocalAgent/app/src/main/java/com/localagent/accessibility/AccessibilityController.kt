package com.localagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Bitmap
import android.graphics.Path
import android.os.Build
import android.util.Base64
import android.view.Display
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import com.localagent.accessibility.ScreenSerializer

@Singleton
class AccessibilityController @Inject constructor() {
    private var service: AccessibilityService? = null

    fun setService(service: AccessibilityService?) {
        this.service = service
    }

    fun getScreenContent(): String {
        val root = service?.rootInActiveWindow
        return if (root != null) {
            try {
                val json = ScreenSerializer.serialize(root)
                json.toString()
            } finally {
                root.recycle()
            }
        } else {
            "{}"
        }
    }

    fun takeScreenshot(callback: (String?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val s = service ?: return callback(null)

            s.takeScreenshot(Display.DEFAULT_DISPLAY, Executors.newSingleThreadExecutor(), object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(screenshotResult: AccessibilityService.ScreenshotResult) {
                    val hardwareBuffer = screenshotResult.hardwareBuffer
                    val colorSpace = screenshotResult.colorSpace
                    val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, colorSpace)

                    if (bitmap != null) {
                        try {
                            val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                            val stream = ByteArrayOutputStream()
                            softwareBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            val byteArray = stream.toByteArray()
                            val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                            callback(base64)
                            softwareBitmap.recycle()
                        } catch (e: Exception) {
                            callback(null)
                        } finally {
                            bitmap.recycle()
                        }
                    } else {
                        callback(null)
                    }
                    hardwareBuffer.close()
                }

                override fun onFailure(errorCode: Int) {
                    callback(null)
                }
            })
        } else {
            callback(null) // Not supported below Android 11 (API 30) for accessibility service screenshot
        }
    }

    fun tap(x: Float, y: Float) {
        service?.let {
            val path = Path().apply { moveTo(x, y) }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()
            it.dispatchGesture(gesture, null, null)
        }
    }

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float) {
        service?.let {
            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x2, y2)
            }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
                .build()
            it.dispatchGesture(gesture, null, null)
        }
    }

    fun performGlobalAction(action: Int): Boolean {
        return service?.performGlobalAction(action) ?: false
    }
}
