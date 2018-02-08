package br.com.simplepass.loadingbutton

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val animator = ObjectAnimator.ofFloat(mask, "alpha", 0f).setDuration(600)

        animator.startDelay = 400
        animator.start()

        button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
