package com.kaushal.level;

/**
 * Created by xkxd061 on 8/5/16.
 */
public interface LevelListener {

    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);

}