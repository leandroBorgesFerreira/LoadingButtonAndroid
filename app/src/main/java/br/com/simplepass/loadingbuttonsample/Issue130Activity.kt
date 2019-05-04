package br.com.simplepass.loadingbuttonsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_issue130.*

class Issue130Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue130)

        imgBtnTestIssue130.run { setOnClickListener { morphDoneAndRevert(this@Issue130Activity) } }
    }
}