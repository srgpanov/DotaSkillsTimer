package com.example.dotaskillstimer.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.DecelerateInterpolator;

import com.example.dotaskillstimer.utils.LayoutConfig;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class CustomItemAnimator extends SimpleItemAnimator {
    private int leftMargin;
    private int topMargin;
    private int width;
    private int height;

    public CustomItemAnimator(LayoutConfig config) {
        this.leftMargin = config.leftMargin;
        this.topMargin = config.topMargin;
        this.width = config.width;
        this.height = config.height;
    }

    public CustomItemAnimator() {
    }
    public void setConfig(LayoutConfig config){
        this.leftMargin = config.leftMargin;
        this.topMargin = config.topMargin;
        this.width = config.width;
        this.height = config.height;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        if (holder instanceof HeroListAdapter.HeroItem) {
            HeroListAdapter.HeroItem commonHolder = (HeroListAdapter.HeroItem) holder;
            int left = holder.itemView.getLeft();
            int top = holder.itemView.getTop();
            float holderWidth = holder.itemView.getWidth();
            float holderHeight = holder.itemView.getHeight();
            commonHolder.itemView.setTranslationY(topMargin - top);
            commonHolder.itemView.setTranslationX(leftMargin - left);
            commonHolder.itemView.setPivotX(0);
            commonHolder.itemView.setPivotY(0);
            commonHolder.itemView.setScaleY(height / holderHeight);
            commonHolder.itemView.setScaleX(width / holderWidth);
            commonHolder.itemView.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(new DecelerateInterpolator(3.0f))
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            dispatchAddFinished(commonHolder);
                        }
                    })
                    .start();
        }
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {

    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}