package com.monkey.systemloadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SystemLoadingView loadingView = (SystemLoadingView) findViewById(R.id.loading_view);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingView.isLoading()) {
                    loadingView.stop();
                } else {
                    loadingView.start();
                }
            }
        });
    }
}
