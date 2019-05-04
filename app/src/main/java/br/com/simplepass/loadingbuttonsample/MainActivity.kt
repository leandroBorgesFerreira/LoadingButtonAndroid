package br.com.simplepass.loadingbuttonsample

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgBtnTest1.run { setOnClickListener { morphDoneAndRevert(this@MainActivity) } }
        buttonTest1.run { setOnClickListener { morphDoneAndRevert(this@MainActivity) } }

        buttonTest2.run { setOnClickListener { morphAndRevert() } }

        buttonTest3.run { setOnClickListener { morphAndRevert(100) } }

        buttonTest4.run {
            setOnClickListener {
                morphDoneAndRevert(this@MainActivity, doneTime = 100)
            }
        }

        buttonTest5.run {
            setOnClickListener {
                morphDoneAndRevert(this@MainActivity, doneTime = 100, revertTime = 200)
            }
        }

        buttonTest6.run {
            setOnClickListener {
                progressType = ProgressType.INDETERMINATE
                startAnimation()
                progressAnimator(this).start()
                Handler().run {
                    postDelayed({
                        doneLoadingAnimation(defaultColor(context), defaultDoneImage(context.resources))
                    }, 2500)
                    postDelayed({ revertAnimation() }, 3500)
                }
            }
        }

        buttonTest7.run { setOnClickListener { morphStopRevert() } }
        buttonTest8.run { setOnClickListener { morphStopRevert(100, 1000) } }

        buttonTest9.run {
            setOnClickListener {
                morphAndRevert {
                    Toast.makeText(this@MainActivity, getString(R.string.start_done),
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonTest10.run {
            setOnClickListener {
                morphDoneAndRevert(this@MainActivity)
            }
        }
    }
}

private fun defaultColor(context: Context) = ContextCompat.getColor(context, android.R.color.black)

private fun defaultDoneImage(resources: Resources) =
        BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp)

internal fun ProgressButton.morphDoneAndRevert(
    context: Context,
    fillColor: Int = defaultColor(context),
    bitmap: Bitmap = defaultDoneImage(context.resources),
    doneTime: Long = 3000,
    revertTime: Long = 4000
) {
    progressType = ProgressType.INDETERMINATE
    startAnimation()
    Handler().run {
        postDelayed({ doneLoadingAnimation(fillColor, bitmap) }, doneTime)
        postDelayed({ revertAnimation() }, revertTime)
    }
}

private fun ProgressButton.morphAndRevert(revertTime: Long = 3000, startAnimationCallback: () -> Unit = {}) {
    startAnimation(startAnimationCallback)
    Handler().postDelayed({ revertAnimation() }, revertTime)
}

private fun ProgressButton.morphStopRevert(stopTime: Long = 1000, revertTime: Long = 2000) {
    startAnimation()
    Handler().postDelayed({ stopAnimation() }, stopTime)
    Handler().postDelayed({ revertAnimation() }, revertTime)
}

private fun progressAnimator(progressButton: ProgressButton) = ValueAnimator.ofFloat(0F, 100F).apply {
    duration = 1500
    startDelay = 500
    addUpdateListener { animation ->
        progressButton.setProgress(animation.animatedValue as Float)
    }
}
