package com.example.camera.utility;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Animation;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by Dell on 5/23/2017.
 */

public class AnimUtils {
    private static AnimUtils animUtils;
    public boolean animationRunning;

    private AnimUtils() {
    }

    public static AnimUtils getInstance() {
        if (animUtils == null)
            animUtils = new AnimUtils();
        return animUtils;
    }

    public void zoomInAnimation(View view) {
        YoYo.with(Techniques.ZoomIn)
                .duration(1000)
                .repeat(0).playOn(view);
    }
  public void woobleInAnimation(View view) {
        YoYo.with(Techniques.Swing)
                .duration(2000)
                .repeatMode(Animation.REVERSE)
                .repeat(Animation.INFINITE).playOn(view);
    }
 public void Pulse(View view) {
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .repeatMode(Animation.REVERSE)
                .repeat(Animation.INFINITE).playOn(view);
    }

    public void pulseAnimation(View view, final ICallBack iCallBack) {

        YoYo.with(Techniques.Pulse)
                .duration(200)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public void rotateOutUpLeftAnimation(View view, final ICallBack iCallBack) {
        YoYo.with(Techniques.SlideOutRight)
                .duration(300)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public void slideOutDownAnimation(View view, final ICallBack iCallBack) {
        YoYo.with(Techniques.SlideOutDown)
                .duration(300)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public void slideInUpAnimation(View view, final ICallBack iCallBack) {
        YoYo.with(Techniques.BounceInUp)
                .duration(300)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public void rotateSlideInRight(View view, final ICallBack iCallBack) {
        YoYo.with(Techniques.SlideInRight)
                .duration(300)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public void rotateSlideOutLeft(View view, final ICallBack iCallBack) {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(300)
                .repeat(0).withListener(new AnimatorListener(iCallBack)).playOn(view);
    }

    public YoYo.YoYoString pulseAnimationInfinite(View view, final ICallBack iCallBack) {
        YoYo.YoYoString yoYoString= YoYo.with(Techniques.Pulse)
                .duration(1000)
                .repeatMode(Animation.REVERSE)
                .repeat(YoYo.INFINITE).withListener(new AnimatorListener(iCallBack)).playOn(view);
        return yoYoString;
    }


    private class AnimatorListener implements Animator.AnimatorListener {
        private ICallBack iCallBack;

        public AnimatorListener(ICallBack iCallBack) {
            this.iCallBack = iCallBack;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            animationRunning = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationRunning = false;
            if (iCallBack != null) {
                iCallBack.onComplete(null);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animationRunning = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
