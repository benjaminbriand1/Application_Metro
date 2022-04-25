package com.example.android.bluetoothchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.android.common.activities.SampleActivityBase;

public class SlapshActivity extends SampleActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slapsh);

        // rediriger vers la page principale apres 3 secondes
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // demarrer une page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        // handler post delayed
        new Handler().postDelayed(runnable,3000);
    }
}