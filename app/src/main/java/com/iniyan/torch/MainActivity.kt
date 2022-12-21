package com.iniyan.torch

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import com.iniyan.torch.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : Activity() {
    companion object {
        private var currentScreenBrightnessMode: Int = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        private var currentScreenBrightnessValue: Int = 0
        private var isTorchOn = true
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var settingsCanWrite: Boolean = Settings.System.canWrite(applicationContext)

        while (!settingsCanWrite) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.data = Uri.parse("package:com.iniyan.torch")

            applicationContext.startActivity(intent)
            settingsCanWrite = Settings.System.canWrite(applicationContext)
        }

        currentScreenBrightnessMode = Settings.System.getInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE
        )
        currentScreenBrightnessValue = Settings.System.getInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
    }

    override fun onStart() {
        super.onStart()

        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )

        Settings.System.putInt(
            applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255
        )

        var alpha = if (isTorchOn) 255 else 0
        val red = 255
        val green = 255
        val blue = 255
        var rotation = 24

        val button: Button = findViewById(R.id.button)

        button.isFocusableInTouchMode = true
        button.focusable = View.FOCUSABLE
        button.requestFocus()

        button.setBackgroundColor(Color.argb(alpha, red, green, blue))

        button.setOnGenericMotionListener { _, ev ->
            if (ev.action == MotionEvent.ACTION_SCROLL && ev.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)) {
                val delta: Int =
                    -(ev.getAxisValue(MotionEventCompat.AXIS_SCROLL) / abs(
                        ev.getAxisValue(
                            MotionEventCompat.AXIS_SCROLL
                        )
                    )).toInt()

                rotation += delta

                if (rotation < 0) {
                    rotation = 0
                } else if (rotation > 24) {
                    rotation = 24
                }

                alpha = ((rotation / 24.0) * 255).toInt()

                if (alpha == 0) {
                    isTorchOn = false
                } else if (alpha in 1..255) {
                    isTorchOn = true
                } else if (alpha < 0) {
                    alpha = 0
                } else {
                    alpha = 255
                }

                button.setBackgroundColor(Color.argb(alpha, red, green, blue))
                true
            } else {
                false
            }
        }

        button.setOnClickListener {
            button.setBackgroundColor(Color.argb(if (isTorchOn) 0 else 255, red, green, blue))
            rotation = if (isTorchOn) 0 else 24
            isTorchOn = !isTorchOn
        }
    }

    override fun onResume() {
        super.onResume()

        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(
            applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255
        )
    }

    override fun onPause() {
        super.onPause()
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            currentScreenBrightnessMode
        )
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            currentScreenBrightnessValue
        )
    }

    override fun onStop() {
        super.onStop()
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            currentScreenBrightnessMode
        )
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            currentScreenBrightnessValue
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            currentScreenBrightnessMode
        )
        Settings.System.putInt(
            applicationContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            currentScreenBrightnessValue
        )
    }
}