package br.com.simplepass.loadingbutton

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.verticalLayout

class AnkoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                verticalLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
//                    padding = dip(50)
//                    circularProgressButton {
//                        setOnClickListener {
//                            startAnimation()
//                            Handler().postDelayed(this::revertAnimation, 2000)
//                        }
//                        setSpinningBarColor(Color.WHITE)
//                        setSpinningBarWidth(dip(4).toFloat())
//                        setPaddingProgress(dip(4).toFloat())
//                        setFinalCornerRadius(0F)
//                        setFinalCornerRadius(1000F)
//                    }
                }
    }

//    private fun ViewManager.circularProgressButton(
//        theme: Int = 0,
//        init: CircularProgressButton.() -> Unit = {}
//    ) = ankoView({ CircularProgressButton(it) }, theme, init)
//
//    private fun ViewManager.circularProgressButton(
//        text: CharSequence, theme: Int = 0,
//        init: CircularProgressButton.() -> Unit = {}
//    ) = circularProgressButton(theme) { init(); setText(text) }
//
//    private fun ViewManager.circularProgressButton(
//        @StringRes textRes: Int, theme: Int = 0,
//        init: CircularProgressButton.() -> Unit = {}
//    ) = circularProgressButton(theme) { init(); setText(textRes) }
}
