package com.example.jerrychan.myanimatiodemo;

/**
 * Created by jerrychan on 16/7/11.
 */
public class RadiusBean {

    private float rightRadius;
    private float leftRadius;

    public RadiusBean(float rightRadius, float leftRadius) {
        this.rightRadius = rightRadius;
        this.leftRadius = leftRadius;
    }

    public float getRightRadius() {
        return rightRadius;
    }

    public void setRightRadius(float rightRadius) {
        this.rightRadius = rightRadius;
    }

    public float getLeftRadius() {
        return leftRadius;
    }

    public void setLeftRadius(float leftRadius) {
        this.leftRadius = leftRadius;
    }
}
