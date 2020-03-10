package com.srgpanov.dotaskillstimer.utils;

import android.view.View;

public class LayoutConfig {
    public int leftMargin;
    public int topMargin;
    public int width;
    public int height;

    public LayoutConfig(int leftMargin, int topMargin, int width, int height) {
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.width = width;
        this.height = height;
    }
    public LayoutConfig(View view) {
        this.leftMargin = view.getLeft();
        this.topMargin = view.getTop();
        this.width = view.getWidth();
        this.height = view.getHeight();
    }

    @Override
    public String toString() {
        return "LayoutConfig{" +
                "leftMargin=" + leftMargin +
                ", topMargin=" + topMargin +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
