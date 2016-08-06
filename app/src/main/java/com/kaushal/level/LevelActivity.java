package com.kaushal.level;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class LevelActivity extends AppCompatActivity implements LevelListener {

    private static Context context;
    public DecimalFormat df = new DecimalFormat("#.#");
    private float lastXValue;
    private float lastYValue;
    ImageView imgAnimation;
    TranslateAnimation animation;
    String currentOrientation;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_main);
        context = this;
        currentOrientation = getRotation(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        lastXValue = 0f;
        lastYValue = 0f;
        super.onConfigurationChanged(newConfig);
        currentOrientation = getRotation(context);
    }

    protected void onResume() {
        super.onResume();
        if (LevelManager.isSupported()) {
            LevelManager.startListening(this);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (LevelManager.isListening()) {
            LevelManager.stopListening();
        }
    }

    public static Context getContext() {
        return context;
    }

    public void onShake(float force) {

    }
    
    public void onAccelerationChanged(float x, float y, float z) {
        float animMultiplier = 80f;
        float animX = x * animMultiplier;
        float animY = y * animMultiplier;
        TextView value = (TextView) findViewById(R.id.value);
        imgAnimation = (ImageView) findViewById(R.id.imageView);
        assert value != null;
        if (currentOrientation == "portrait"){
            imgAnimation.startAnimation(bubbleAnimation(animX, 0f));
            value.setText(String.valueOf(df.format(x)));
            lastXValue = animX;
            lastYValue= 0f;
        }
        else if (currentOrientation == "landscape" || currentOrientation == "reverse landscape"){
            imgAnimation.startAnimation(bubbleAnimation(animY, 0f));
            value.setText(String.valueOf(df.format(y)));
            lastXValue = animY;
            lastYValue= 0f;
        }
        else{
            value.setText("Orientation Error!! Please Restart application.");
        }
    }

    private TranslateAnimation bubbleAnimation(float x, float y) {
        animation = new TranslateAnimation(lastXValue, x, lastYValue, y);
        animation.setDuration(5000);
        animation.setFillAfter(true);
        return animation;

    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse portrait";
            default:
                return "reverse landscape";
        }
    }
}