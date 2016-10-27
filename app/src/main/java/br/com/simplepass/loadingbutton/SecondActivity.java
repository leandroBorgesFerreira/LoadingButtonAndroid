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

        ObjectAnimator animator = ObjectAnimator.ofInt(findViewById(R.id.mask),
                "backgroundColor",
                ContextCompat.getColor(this, R.color.color_button_default),
                Color.TRANSPARENT)
                .setDuration(700);

        animator.setEvaluator(new ArgbEvaluator());
        animator.setStartDelay(800);
        animator.start();
    }
}
