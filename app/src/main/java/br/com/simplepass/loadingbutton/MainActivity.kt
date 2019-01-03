package br.com.simplepass.loadingbutton

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.customViews.ProgressButton
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressImageButton.onClick { progressImageButton.morphAndRevert() }
        progressButtonNoPadding.onClick { progressButtonNoPadding.morphAndRevert() }
    }

    private fun ProgressButton.morphAndRevert() {
        animateButtonAndRevert(
            this,
            ContextCompat.getColor(this@MainActivity, android.R.color.black),
            BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp)
        )
    }
}

private fun animateButtonAndRevert(
    circularProgressButton: ProgressButton,
    fillColor: Int,
    bitmap: Bitmap
) {
    circularProgressButton.run {
        startAnimation()
        Handler().run {
            postDelayed({ doneLoadingAnimation(fillColor, bitmap) }, 7000)
            postDelayed({ revertAnimation() }, 8000)
        }
    }
}
