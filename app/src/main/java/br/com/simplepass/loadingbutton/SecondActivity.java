package br.com.simplepass.loadingbutton;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.mask),
                "alpha",
                0f).setDuration(600);

        animator.setStartDelay(400);
        animator.start();
    }
}
