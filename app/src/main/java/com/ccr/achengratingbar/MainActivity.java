package com.ccr.achengratingbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ccr.ratingbarlibrary.AchengRatingBar;

public class MainActivity extends AppCompatActivity {

    AchengRatingBar sample0;
    AchengRatingBar sample1;
    AchengRatingBar sample2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sample0 = (AchengRatingBar) findViewById(R.id.sample0);
        sample1 = (AchengRatingBar) findViewById(R.id.sample1);
        sample2 = (AchengRatingBar) findViewById(R.id.sample2);
        sample0.setOnRatingChangedListener(new AchengRatingBar.OnRatingChangedListener() {
            @Override
            public void ratingChanged(float rating) {
                Toast.makeText(MainActivity.this,rating+"",Toast.LENGTH_LONG).show();
            }
        });
        sample1.setOnRatingChangedListener(new AchengRatingBar.OnRatingChangedListener() {
            @Override
            public void ratingChanged(float rating) {
                Toast.makeText(MainActivity.this,rating+"",Toast.LENGTH_LONG).show();
            }
        });

        sample2.setOnRatingChangedListener(new AchengRatingBar.OnRatingChangedListener() {
            @Override
            public void ratingChanged(float rating) {
                Toast.makeText(MainActivity.this,rating+"",Toast.LENGTH_LONG).show();
            }
        });
    }
}
