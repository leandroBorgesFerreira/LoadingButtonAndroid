package br.com.simplepass.loadingbutton

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.View
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import br.com.simplepass.loading_button_lib.customViews.CircularProgressImageButton
import br.com.simplepass.loading_button_lib.interfaces.AnimatedButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressButton.setOnClickListener { view -> animateAndDoneFast(view as CircularProgressImageButton) }

        progressButtonNoPadding.setOnClickListener { _ ->
            animateButtonAndRevert(progressButtonNoPadding,
                    ContextCompat.getColor(this@MainActivity, R.color.black),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on_white_48dp))
        }

        progressButton2.setOnClickListener { _ ->
            animateButtonAndRevert(progressButton2,
                    ContextCompat.getColor(this@MainActivity, R.color.transparent),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_cloud_upload_white_24dp))
        }

        progressButtonNoPadding2.setOnClickListener { _ ->
            animateButtonAndRevert(progressButtonNoPadding2,
                    ContextCompat.getColor(this@MainActivity, R.color.colorAccent),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp))
        }

        progressButtonChangeActivity.setOnClickListener { view ->
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

    private fun animateButtonAndRevert(circularProgressButton: CircularProgressButton, fillColor: Int, bitmap: Bitmap) {
        val handler = Handler()

        val runnable = {
            circularProgressButton.doneLoadingAnimation(
                    fillColor,
                    bitmap)
        }

        val runnableRevert = { circularProgressButton.revertAnimation { circularProgressButton.text = "Seu texto aqui!" } }

        circularProgressButton.revertAnimation()

        circularProgressButton.startAnimation()
        handler.postDelayed(runnable, 3000)
        handler.postDelayed(runnableRevert, 4000)
        handler.postDelayed(runnableRevert, 4100)
    }

    private fun animateTwice(circularProgressButton: CircularProgressButton) {
        val handler = Handler()

        val runnable = {
            circularProgressButton.revertAnimation { circularProgressButton.text = "Animation reverted" }
            circularProgressButton.startAnimation()

            // new Handler().postDelayed(circularProgressButton::revertAnimation, 2000);
        }

        circularProgressButton.startAnimation()
        handler.postDelayed(runnable, 3000)
    }

    private fun animateAndRevert(animatedButton: AnimatedButton) {
        val handler = Handler()

        animatedButton.startAnimation()
        handler.postDelayed({ animatedButton.revertAnimation() }, 3000)
    }

    private fun animateAndDoneFast(animatedButton: CircularProgressImageButton) {
        val handler = Handler()

        animatedButton.startAnimation()
        handler.postDelayed({
            animatedButton.doneLoadingAnimation(
                    ContextCompat.getColor(this@MainActivity, R.color.black),
                    BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on_white_48dp))
        },
                100)

    }

    /*private void animateButton(final CircularProgressButton circularProgressButton){
        Handler handler = new Handler();

        Runnable runnable = () -> circularProgressButton.doneLoadingAnimation(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

        circularProgressButton.startAnimation();
        handler.postDelayed(runnable, 3000);
    }*/
}
