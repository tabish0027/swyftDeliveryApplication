package io.swyft.swyft;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.devbeans.swyft.R;
import io.devbeans.swyft.activity_login;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thr= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Intent i = new Intent(Splash.this, activity_login.class);
                    Splash.this.startActivity(i);
                }catch (Exception i){

                }
            }
        });
        thr.start();

    }
}
