package haichenyi.aloe.customview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.haichenyi.aloe.widget.WaveView;

import haichenyi.aloe.customview.R;


/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: 2018/5/22
 * @Version: V1.0
 */
public class WaveViewActivity extends AppCompatActivity {
    ValueAnimator animator1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_view);
        final WaveView waveView = findViewById(R.id.wave_view);
        waveView.setWaveColor(ContextCompat.getColor(this,R.color.colorPrimaryDark1));
        waveView.setWaveDuration(2000);
        waveView.setWaveWidth(300);
        waveView.setWaveMaxHeight(30);
        waveView.setShowProgress(false);
        waveView.setProgressColor(Color.RED);
        waveView.setProgressSize(18);
        waveView.setSrcBitmapId(R.mipmap.psb17);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveView.startAnimation();
                if (animator1 != null && animator1.isRunning()) {
                    animator1.cancel();
                    animator1 = null;
                }
                animator1 = ValueAnimator.ofFloat(0, 1);
                animator1.setDuration(5000);
                animator1.setInterpolator(new LinearInterpolator());
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float animatedValue = (Float) animation.getAnimatedValue();
                        waveView.setCurrentStartY(animatedValue);
                    }
                });
                animator1.start();
                animator1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animator1.cancel();
                    }
                });
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                stopAnimation();
                waveView.stopAnimation();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void stopAnimation() {
        if (animator1 != null && animator1.isRunning()) {
            if (animator1.isPaused()) {
                animator1.resume();
            } else {
                animator1.pause();
            }
        }
    }
}
