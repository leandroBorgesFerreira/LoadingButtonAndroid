package br.com.simplepass.loadingbutton;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.simplepass.loading_button_lib.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircularProgressButton progressButton = (CircularProgressButton) findViewById(R.id.progress_btn);

        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(progressButton);
            }
        });

        final CircularProgressButton progressButtonNoPadding =
                (CircularProgressButton) findViewById(R.id.progress_btn_no_padding);

        progressButtonNoPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(progressButtonNoPadding);
            }
        });
    }


    private void animateButton(final CircularProgressButton circularProgressButton){
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                circularProgressButton.stopAnimation();
                circularProgressButton.revertAnimation();
            }
        };

        circularProgressButton.startAnimation();
        handler.postDelayed(runnable, 3000);
    }
}
