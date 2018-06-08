package com.haichenyi.aloe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.haichenyi.aloe.interfaces.ViewPagerClickListener;


/**
 * @Title: AutoViewPager
 * @Description: 自动轮播ViewPager
 * 1、支持摸出停止轮播，手抬起之后继续轮播
 * 2、自动开始循环，Activity销毁后，自动停止
 * @Author: wz
 * @Date: 2018/6/8
 * @Version: V1.0
 */
public class AutoViewPager extends ViewPager {

    /**
     * 轮播图每张图片的时间
     */
    private static long AUTO_TIME = 3000;
    /**
     * 是否需要自动轮播
     */
    private boolean isAuto = true;

    private static final int START_AUTO = 0x01;
    private static final int STOP_AUTO = 0x02;
    private static final int SEND_MSG = 0x03;
    private ViewPagerClickListener lisnter;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_AUTO:
                    isAuto = true;
                    sendMsgDelayed();
                    break;
                case STOP_AUTO:
                    removeMsg();
                    isAuto = false;
                    break;
                case SEND_MSG:
                    int c = getCurrentItem();
                    PagerAdapter adapter = getAdapter();
                    if (adapter != null) {
                        if (c == adapter.getCount() - 1) {
                            c = 0;
                        } else {
                            c++;
                        }
                    }
                    setCurrentItem(c, true);
                    if (isAuto) {
                        sendMsgDelayed();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public AutoViewPager(@NonNull Context context) {
        super(context);
    }

    public AutoViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void removeMsg() {
        handler.removeMessages(SEND_MSG);
    }

    private void sendMsgDelayed() {
        Message scroll = handler.obtainMessage(SEND_MSG);
        handler.sendMessageDelayed(scroll, AUTO_TIME);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //开启轮播
        if (isAuto) {
            handler.obtainMessage(START_AUTO).sendToTarget();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //关闭轮播
        handler.obtainMessage(STOP_AUTO).sendToTarget();
    }

    public AutoViewPager setAutoTime(long autoTime) {
        AUTO_TIME = autoTime;
        return this;
    }

    public AutoViewPager setAuto(boolean auto) {
        isAuto = auto;
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (lisnter == null) {
                    removeMsg();
                } else {
                    lisnter.onClick(getCurrentItem());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (lisnter == null) {
                    sendMsgDelayed();
                }
                break;
            default:
        }
        return super.onTouchEvent(ev);
    }

    public void addOnClickListener(ViewPagerClickListener lisnter) {
        this.lisnter = lisnter;
    }
}
