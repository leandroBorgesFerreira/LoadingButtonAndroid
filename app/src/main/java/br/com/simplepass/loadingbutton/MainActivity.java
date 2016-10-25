package br.com.simplepass.loadingbutton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.simplepass.loading_button_lib.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircularProgressButton progressButton = (CircularProgressButton) findViewById(R.id.progress_btn);

        progressButton.setOnClickListener(view -> animateTwice(progressButton));

        CircularProgressButton progressButtonNoPadding =
                (CircularProgressButton) findViewById(R.id.progress_btn_no_padding);

        progressButtonNoPadding.setOnClickListener((view) -> animateButtonAndRevert(progressButtonNoPadding,
                ContextCompat.getColor(MainActivity.this, R.color.black),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_alarm_on_white_48dp)));

        CircularProgressButton progressButton2 = (CircularProgressButton) findViewById(R.id.progress_btn2);

        progressButton2.setOnClickListener((view) -> animateButtonAndRevert(progressButton2,
                ContextCompat.getColor(MainActivity.this, R.color.transparent),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cloud_upload_white_24dp)));

        CircularProgressButton progressButtonNoPadding2 =
                (CircularProgressButton) findViewById(R.id.progress_btn_no_padding2);

        progressButtonNoPadding2.setOnClickListener((view) -> animateButtonAndRevert(progressButtonNoPadding2,
                ContextCompat.getColor(MainActivity.this, R.color.colorAccent),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_pregnant_woman_white_48dp)));
    }

    private void animateButtonAndRevert(final CircularProgressButton circularProgressButton, int fillColor, Bitmap bitmap){
        Handler handler = new Handler();

        Runnable runnable = () -> {
            circularProgressButton.doneLoagingAnimation(
                    fillColor,
                    bitmap);

            new Handler().postDelayed(circularProgressButton::revertAnimation, 2000);
        };

        circularProgressButton.startAnimation();
        handler.postDelayed(runnable, 3000);
    }

    private void animateTwice(final CircularProgressButton circularProgressButton){
        Handler handler = new Handler();

        Runnable runnable = () -> {
            circularProgressButton.revertAnimation();
            circularProgressButton.startAnimation();

            new Handler().postDelayed(circularProgressButton::revertAnimation, 2000);
        };

        circularProgressButton.startAnimation();
        handler.postDelayed(runnable, 3000);
    }

    /*private void animateButton(final CircularProgressButton circularProgressButton){
        Handler handler = new Handler();

        Runnable runnable = () -> circularProgressButton.doneLoagingAnimation(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

        circularProgressButton.startAnimation();
        handler.postDelayed(runnable, 3000);
    }*/
}
