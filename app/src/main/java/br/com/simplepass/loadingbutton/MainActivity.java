package br.com.simplepass.loadingbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import br.com.simplepass.loading_button_lib.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    private Boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLoading = false;

        final CircularProgressButton progressEditText = (CircularProgressButton) findViewById(R.id.progress_edit_text);

        progressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLoading) {
                    progressEditText.startAnimation();
                    isLoading = true;
                    Log.d("ET", "isLoading: " + isLoading.toString());
                } else{
                    progressEditText.stopAnimation();
                    progressEditText.revertAnimation();
                    isLoading = false;

                    Log.d("ET", "isLoading: " + isLoading.toString());
                }
            }
        });
    }


}
