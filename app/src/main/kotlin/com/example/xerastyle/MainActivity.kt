package com.example.xerastyle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.*
import kotlin.math.roundToInt

class MainActivity : Activity() {

    private val freeFireMaxPackage = "com.dts.freefiremax"

    private val prefs by lazy {
        getSharedPreferences("xera_settings", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun baseLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 22, 22, 22)
            setBackgroundColor(Color.rgb(7, 7, 7))
        }
    }

    private fun title(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 25f
            setTextColor(Color.RED)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 18)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun makeButton(
        text: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            this.text = text
            textSize = 15f
            setOnClickListener {
                isEnabled = false
                action()
                postDelayed({
                    isEnabled = true
                }, 700)
            }
        }
    }

    // =========================
    // HOME
    // =========================

    private fun showHome() {

        val root = baseLayout()

        root.addView(
            title("XERA STYLE COMPANION")
        )

        root.addView(
            makeButton("🎮 OPEN FREE FIRE MAX") {
                openFreeFireMax()
            }
        )

        root.addView(
            makeButton("🎯 SENSITIVITY PRESETS") {
                showSensitivity()
            }
        )

        root.addView(
            makeButton("🎯 CUSTOM CROSSHAIR") {
                showCrosshair()
            }
        )

        root.addView(
            makeButton("📱 DEVICE INFO") {
                showDeviceInfo()
            }
        )

        root.addView(
            makeButton("💾 SAVE / LOAD PRESET") {
                showSaveLoad()
            }
        )

        root.addView(
            makeButton("📊 AIM TRAINING STATS") {
                showStats()
            }
        )

        val info = TextView(this).apply {
            text = """
                
                XERA STYLE COMPANION
                
                Standalone companion utility.
                It does not modify Free Fire files
                or game memory.
            """.trimIndent()

            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 0)
        }

        root.addView(info)

        setContentView(root)
    }

    // =========================
    // OPEN FREE FIRE MAX
    // =========================

    private fun openFreeFireMax() {

        val launchIntent =
            packageManager.getLaunchIntentForPackage(
                freeFireMaxPackage
            )

        if (launchIntent != null) {

            launchIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            startActivity(launchIntent)

        } else {

            Toast.makeText(
                this,
                "Free Fire MAX install नहीं है।",
                Toast.LENGTH_LONG
            ).show()

            openPlayStore()
        }
    }

    private fun openPlayStore() {

        try {

            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "market://details?id=$freeFireMaxPackage"
                )
            )

            startActivity(marketIntent)

        } catch (e: Exception) {

            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://play.google.com/store/apps/details?id=$freeFireMaxPackage"
                )
            )

            startActivity(webIntent)
        }
    }

    // =========================
    // SENSITIVITY
    // =========================

    private fun showSensitivity() {

        val root = baseLayout()

        root.addView(
            title("SENSITIVITY")
        )

        val valueText = TextView(this).apply {
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 15)
        }

        root.addView(valueText)

        val seekBar = SeekBar(this).apply {

            max = 200

            progress =
                prefs.getInt(
                    "sensitivity",
                    120
                )
        }

        root.addView(seekBar)

        fun updateValue(value: Int) {

            valueText.text =
                "REFERENCE VALUE: $value"

            prefs.edit()
                .putInt(
                    "sensitivity",
                    value
                )
                .apply()
        }

        seekBar.setOnSeekBarChangeListener(
            object :
                SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    bar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    updateValue(progress)
                }

                override fun onStartTrackingTouch(
                    bar: SeekBar?
                ) {}

                override fun onStopTrackingTouch(
                    bar: SeekBar?
                ) {}
            }
        )

        updateValue(seekBar.progress)

        root.addView(
            makeButton("BALANCED - 120") {
                seekBar.progress = 120
            }
        )

        root.addView(
            makeButton("HIGH - 160") {
                seekBar.progress = 160
            }
        )

        root.addView(
            makeButton("MAX REFERENCE - 190") {
                seekBar.progress = 190
            }
        )

        root.addView(
            makeButton("← BACK") {
                showHome()
            }
        )

        setContentView(root)
    }

    // =========================
    // CROSSHAIR
    // =========================

    private fun showCrosshair() {

        val root = baseLayout()

        root.addView(
            title("CUSTOM CROSSHAIR")
        )

        val preview = TextView(this).apply {

            text =
                prefs.getString(
                    "crosshair",
                    "⊕"
                )

            textSize = 70f

            setTextColor(Color.RED)

            gravity = Gravity.CENTER

            setPadding(
                0,
                15,
                0,
                20
            )
        }

        root.addView(preview)

        val shapes = listOf(
            "+",
            "×",
            "⊕",
            "•",
            "◉",
            "△"
        )

        for (shape in shapes) {

            root.addView(
                makeButton(shape) {

                    preview.text = shape

                    prefs.edit()
                        .putString(
                            "crosshair",
                            shape
                        )
                        .apply()
                }
            )
        }

        root.addView(
            makeButton("← BACK") {
                showHome()
            }
        )

        setContentView(root)
    }

    // =========================
    // DEVICE INFO
    // =========================

    private fun showDeviceInfo() {

        val root = baseLayout()

        root.addView(
            title("DEVICE INFO")
        )

        val metrics =
            DisplayMetrics()

        @Suppress("DEPRECATION")
        windowManager
            .defaultDisplay
            .getRealMetrics(metrics)

        val memory =
            Runtime.getRuntime()
                .maxMemory() /
                (1024L * 1024L)

        val info = TextView(this).apply {

            text = """
                
                RAM (APP LIMIT): ${memory} MB
                
                DPI: ${metrics.densityDpi}
                
                Resolution:
                ${metrics.widthPixels} × ${metrics.heightPixels}
                
                Android:
                ${Build.VERSION.RELEASE}
                
                API:
                ${Build.VERSION.SDK_INT}
            """.trimIndent()

            textSize = 17f

            setTextColor(Color.WHITE)

            gravity = Gravity.CENTER

            setPadding(
                0,
                15,
                0,
                20
            )
        }

        root.addView(info)

        root.addView(
            makeButton("← BACK") {
                showHome()
            }
        )

        setContentView(root)
    }

    // =========================
    // SAVE / LOAD
    // =========================

    private fun showSaveLoad() {

        val root = baseLayout()

        root.addView(
            title("PRESET MANAGER")
        )

        root.addView(
            makeButton("💾 SAVE CURRENT PRESET") {

                val sensitivity =
                    prefs.getInt(
                        "sensitivity",
                        120
                    )

                val crosshair =
                    prefs.getString(
                        "crosshair",
                        "⊕"
                    )

                prefs.edit()
                    .putInt(
                        "saved_sensitivity",
                        sensitivity
                    )
                    .putString(
                        "saved_crosshair",
                        crosshair
                    )
                    .apply()

                Toast.makeText(
                    this,
                    "Preset saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        root.addView(
            makeButton("📂 LOAD SAVED PRESET") {

                val sensitivity =
                    prefs.getInt(
                        "saved_sensitivity",
                        120
                    )

                val crosshair =
                    prefs.getString(
                        "saved_crosshair",
                        "⊕"
                    )

                prefs.edit()
                    .putInt(
                        "sensitivity",
                        sensitivity
                    )
                    .putString(
                        "crosshair",
                        crosshair
                    )
                    .apply()

                Toast.makeText(
                    this,
                    "Preset loaded!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        root.addView(
            makeButton("← BACK") {
                showHome()
            }
        )

        setContentView(root)
    }

    // =========================
    // AIM TRAINING
    // =========================

    private fun showStats() {

        val root = baseLayout()

        root.addView(
            title("AIM TRAINING")
        )

        val hits =
            prefs.getInt(
                "hits",
                0
            )

        val shots =
            prefs.getInt(
                "shots",
                0
            )

        val accuracy =
            if (shots == 0) {
                0
            } else {
                (
                    hits.toDouble() /
                    shots.toDouble() *
                    100
                ).roundToInt()
            }

        val stats = TextView(this).apply {

            text = """
                
                SHOTS: $shots
                
                HITS: $hits
                
                ACCURACY: $accuracy%
            """.trimIndent()

            textSize = 19f

            setTextColor(Color.WHITE)

            gravity = Gravity.CENTER

            setPadding(
                0,
                15,
                0,
                20
            )
        }

        root.addView(stats)

        root.addView(
            makeButton("＋ HIT") {

                prefs.edit()
                    .putInt(
                        "hits",
                        hits + 1
                    )
                    .putInt(
                        "shots",
                        shots + 1
                    )
                    .apply()

                showStats()
            }
        )

        root.addView(
            makeButton("MISS") {

                prefs.edit()
                    .putInt(
                        "shots",
                        shots + 1
                    )
                    .apply()

                showStats()
            }
        )

        root.addView(
            makeButton("RESET STATS") {

                prefs.edit()
                    .putInt(
                        "hits",
                        0
                    )
                    .putInt(
                        "shots",
                        0
                    )
                    .apply()

                showStats()
            }
        )

        root.addView(
            makeButton("← BACK") {
                showHome()
            }
        )

        setContentView(root)
    }
}
