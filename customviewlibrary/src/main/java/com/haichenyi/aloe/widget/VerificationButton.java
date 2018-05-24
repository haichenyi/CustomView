package com.haichenyi.aloe.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by Aloe.Zheng on 2017/9/22.
 * 倒计时验证码
 */

public class VerificationButton extends AppCompatTextView {
    //倒计时定时器
    private CountDownTimer countDownTimer;
    //按钮文本
    private String txt;

    public VerificationButton(Context context) {
        super(context);
    }

    public VerificationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 开始倒计时，默认总时间60秒,间隔1秒.
     */
    public void onStart() {
        onStart(60000);
    }

    public void onStart(int time) {
        onStart(time, 1000);
    }

    /**
     * 开始倒计时.
     *
     * @param time     总倒计时间
     * @param interval 计时间隔
     */
    private void onStart(int time, final int interval) {
        time = time + 500;//防止显示误差
        if (isCountDown()) {
            return;
        }
        txt = getText().toString();
        if (0 == interval) {
            return;
        }
        countDownTimer = new CountDownTimer(time, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(String.valueOf(millisUntilFinished / interval));
                if (isEnabled()) {
                    setEnabled(false);
                }
            }

            @Override
            public void onFinish() {
                onFinishCountDown();
            }
        };
        countDownTimer.start();
    }

    /**
     * 停止倒计时.
     */
    public void onStop() {
        if (!isCountDown()) {
            return;
        }
        countDownTimer.cancel();
        onFinishCountDown();
    }

    /**
     * 倒计时完成.
     */
    private void onFinishCountDown() {
        if (TextUtils.isEmpty(txt)) {
            txt = "";
        }
        setText(txt);
        countDownTimer = null;
        setEnabled(true);
    }

    /**
     * 是否正在倒计时.
     *
     * @return true:是,false:不
     */
    private boolean isCountDown() {
        return null != countDownTimer;
    }
}
