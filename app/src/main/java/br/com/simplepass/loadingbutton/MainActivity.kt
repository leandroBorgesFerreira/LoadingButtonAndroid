package br.com.simplepass.loadingbutton

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.View
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import br.com.simplepass.loading_button_lib.customViews.CircularProgressImageButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressButton.setOnClickListener { view -> animateAndDoneFast(view as CircularProgressImageButton) }

        progressButtonNoPadding.setBackgroundColor(Color.GREEN)

        progressButtonNoPadding.setOnClickListener {
            animateButtonAndRevert(progressButtonNoPadding,
                    ContextCompat.getColor(this@MainActivity, R.color.black),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on_white_48dp))
        }

        progressFix.setOnClickListener {
            animateButtonAndRevert(progressFix,
                    ContextCompat.getColor(this@MainActivity, R.color.transparent),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_cloud_upload_white_24dp))
        }

        progressButtonNoPadding2.setOnClickListener {
            animateButtonAndRevert(progressButtonNoPadding2,
                    ContextCompat.getColor(this@MainActivity, R.color.colorAccent),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp))
        }

        progressButtonChangeActivity.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                progressButtonChangeActivity.startAnimation()

                val runnable = {
                    progressButtonChangeActivity.stopAnimation()

                    val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            this,
                            Pair<View, String>(progressButtonChangeActivity, "transition"))

                    startActivity(intent, activityOptions.toBundle())

                }

                Handler().postDelayed(runnable, 3000)
            }
        }

    }

    private fun animateButtonAndRevert(circularProgressButton: CircularProgressButton,
                                       fillColor: Int,
                                       bitmap: Bitmap) {

        val doneAnimationRunnable = {
            circularProgressButton.doneLoadingAnimation(
                    fillColor,
                    bitmap)
        }

        circularProgressButton.startAnimation()

        with(Handler()) {
            postDelayed(doneAnimationRunnable, 3000)
            postDelayed({ circularProgressButton.revertAnimation() }, 4000)
        }
    }

    private fun animateAndDoneFast(animatedButton: CircularProgressImageButton) {
        animatedButton.startAnimation()
        Handler().postDelayed({
            animatedButton.doneLoadingAnimation(
                    ContextCompat.getColor(this@MainActivity, R.color.black),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on_white_48dp))
        }, 100)

        Handler().postDelayed({ animatedButton.revertAnimation {
            animatedButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on_white_48dp))
        } }, 700)

    }

    override fun onDestroy() {
        super.onDestroy()

        progressButtonNoPadding.dispose()
        progressFix.dispose()
        progressButtonNoPadding2.dispose()
        login.dispose()
    }
}
