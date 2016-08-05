package com.kaushal.level;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DecimalFormat;

public class LevelActivity extends AppCompatActivity implements LevelListener {

    private static Context context;
    public DecimalFormat df = new DecimalFormat("#.#");
    TextView orientation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_main);
        context = this;
        orientation = (TextView) findViewById(R.id.orientation);
        String CurrentOrientation = getRotation(context);
        orientation.setText(CurrentOrientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String CurrentOrientation = getRotation(context);
        orientation.setText(CurrentOrientation);
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
        TextView value = (TextView) findViewById(R.id.value);
        TextView zValue = (TextView) findViewById(R.id.z);
        assert value != null;
        levelValue(Math.round(x), Math.round(y), value);
        assert zValue != null;
        zValue.setText(String.valueOf(Math.round(z)));
    }

    private void levelValue(int x, int y, TextView value) {
        if (Math.abs(x) >= 9){
            value.setText(String.valueOf(y));
        }
        else if (Math.abs(y) >= 9){
            value.setText(String.valueOf(x));
        }
        else{
            value.setText(String.valueOf(x) + ", " + String.valueOf(y));
        }
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