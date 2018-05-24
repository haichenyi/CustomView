package com.haichenyi.aloe.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;


/**
 * Author: 海晨忆
 * Date: 2018/3/27
 * Desc: 当你的觉得你的水波纹会有停顿的时候，你可以试着把水波纹的宽度减小一点。
 */
public class WaveView extends View {
    /*水波纹路径*/
    private Path path;
    /*水波纹画笔*/
    private Paint paintWave;
    /*进度文字画笔*/
    private Paint paintText;
    /*最后设置模式的时候的画笔*/
    private Paint paintCircle;
    /*水波纹的颜色*/
    private int waveColor;
    /*水波纹的时间*/
    private int waveDuration;
    /*水波纹的宽度*/
    private int waveWidth;
    /*水波纹的高度*/
    private int waveMaxHeight;
    /*水波纹动画*/
    private ValueAnimator animator;
    /*当前进度*/
    private float currentPercent;
    /*水波纹横着移动给的当前的X轴坐标*/
    private int currentStartX;
    /*水波纹竖着移动的当前的Y轴坐标*/
    private int currentStartY;
    /*动画是否完层*/
    private boolean isFinish = false;
    /*进度文字的颜色*/
    private int progressColor;
    /*进度文字的大小*/
    private int progressSize;
    /*是否显示进度文字，默认不显示*/
    private boolean isShowProgress = false;
    /*背景的宽*/
    private int mWidth;
    /*背景的高*/
    private int mHeight;
    /*源图片，也就是我们的背景图片*/
    private Bitmap mBitmapSrc;
    /*目标图片，就是一个圆形bitmap*/
    private Bitmap mBitmapDst;
    private Canvas mCanvas;
    /*背景图片的id：默认-1表示没有传背景图片*/
    private int srcBitmapId;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView();
    }

    private void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initPath();
        initPaint();
    }

    private void initPath() {
        path = new Path();
    }

    private void initPaint() {
        paintWave = new Paint();
        paintWave.setColor(waveColor);
        paintWave.setStyle(Paint.Style.FILL);
        paintWave.setAntiAlias(true);
        paintWave.setDither(true);
        paintText = new Paint();
        paintText.set(paintWave);
        paintText.setColor(progressColor);
        paintText.setTextSize(progressSize);
        paintCircle = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int minWidth = 300;
        int minHeight = 300;
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT
                && params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minWidth, minHeight);
        } else if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minWidth, heightSize);
        } else if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, minHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置了背景图片
        if (srcBitmapId != -1) {
            mBitmapSrc = BitmapFactory.decodeResource(getResources(), srcBitmapId)
                    .copy(Bitmap.Config.ARGB_8888, true);
            mWidth = mBitmapSrc.getWidth();
            mHeight = mBitmapSrc.getHeight();
        } else {
            mWidth = w;
            mHeight = h;
            mBitmapSrc = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(mBitmapSrc);
        mBitmapDst = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapDst);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paintCircle);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        waveColor = typedArray.getColor(R.styleable.WaveView_waveColor, Color.GRAY);
        waveDuration = typedArray.getInt(R.styleable.WaveView_waveDuration, 2000);
        waveWidth = (int) digitValue(typedArray.getInt(R.styleable.WaveView_waveWidth, 400),
                TypedValue.COMPLEX_UNIT_DIP);
        waveMaxHeight = (int) digitValue(typedArray.getInt(R.styleable.WaveView_waveMaxHeight,
                30), TypedValue.COMPLEX_UNIT_DIP);
        progressColor = typedArray.getColor(R.styleable.WaveView_progressColor, Color.GREEN);
        progressSize = (int) digitValue(typedArray.getInt(R.styleable.WaveView_progressSize,
                16), TypedValue.COMPLEX_UNIT_SP);
        isShowProgress = typedArray.getBoolean(R.styleable.WaveView_isShowProgress, false);
        srcBitmapId = typedArray.getResourceId(R.styleable.WaveView_imageBitmap, -1);
        typedArray.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2 - mWidth / 2, getHeight() / 2 - mHeight / 2);
        drawPath(mCanvas);
        int layerID = canvas.saveLayer(0, 0, mWidth, mHeight, paintCircle, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mBitmapDst, 0, 0, paintCircle);
        paintCircle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmapSrc, 0, 0, paintCircle);
        paintCircle.setXfermode(null);
        canvas.restoreToCount(layerID);
        if (isShowProgress) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        String text;
        if (currentPercent != 0) {
            text = ((int) (getFloat(currentPercent) * 100)) + "%";
        } else {
            text = "";
        }
        float v = paintText.measureText(text);//文本的宽度
        Paint.FontMetricsInt fm = paintText.getFontMetricsInt();
        //getHeight()：控件的高度
        //getHeight()/2-fm.descent：意思是将整个文字区域抬高至控件的1/2
        //+ (fm.bottom - fm.top) / 2：(fm.bottom - fm.top)其实就是文本的高度，意思就是将文本下沉文本高度的一半
        int i = mHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(text, (mWidth - v) / 2, i, paintText);
        Log.v("wz", text);
        if (currentPercent == 1) {
            isFinish = true;
        }
    }

    /**
     * 画水波纹path
     *
     * @param canvas canvas
     */
    private void drawPath(Canvas canvas) {
        path.reset();
        initWavePath();
        canvas.drawPath(path, paintWave);
    }

    /**
     * 初始化水波纹path
     */
    private void initWavePath() {
        int currentControlY = 0;
        if (currentStartY < waveMaxHeight) {
            currentControlY = currentStartY;
        } else {
            currentControlY = waveMaxHeight;
        }
        if (mHeight - currentStartY < waveMaxHeight) {
            currentControlY = mHeight - currentStartY;
        }
        path.moveTo(-waveWidth + currentStartX, mHeight - currentStartY);
        for (int i = -waveWidth; i < mWidth + waveWidth; i += waveWidth) {
            path.rQuadTo(waveWidth / 4, -currentControlY, waveWidth / 2, 0);
            path.rQuadTo(waveWidth / 4, currentControlY, waveWidth / 2, 0);
        }
        path.lineTo(mWidth, mHeight);
        path.lineTo(0, mHeight);
        path.close();
    }

    public void startAnimation() {
        cancelAnimation();
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(waveDuration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isFinish) {
                    cancelAnimation();
                }
                Float animatedValue = (Float) animation.getAnimatedValue();
                currentStartX = (int) (waveWidth * animatedValue);
                postInvalidateDelayed(10);
            }
        });
        animator.start();
    }

    private void cancelAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        isFinish = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            if (animator.isPaused()) {
                animator.resume();
            } else {
                animator.pause();
            }
        }
    }

    public void setSrcBitmapId(int srcBitmapId) {
        this.srcBitmapId = srcBitmapId;
    }

    public void setShowProgress(boolean showProgress) {
        this.isShowProgress = showProgress;
    }

    public void setProgressSize(int progressSize) {
        this.progressSize = (int) digitValue(progressSize, TypedValue.COMPLEX_UNIT_SP);
    }

    public void setProgressColor(int progressColor) {
        paintText.setColor(progressColor);
    }

    public void setCurrentStartY(float percent) {
        this.currentPercent = getFloat(percent);
        this.currentStartY = (int) (mHeight * percent);
    }

    public void setWaveColor(int waveColor) {
        paintWave.setColor(waveColor);
    }

    public void setWaveDuration(int waveDuration) {
        this.waveDuration = waveDuration;
    }

    public void setWaveWidth(int waveWidth) {
        this.waveWidth = (int) digitValue(waveWidth, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setWaveMaxHeight(int waveMaxHeight) {
        this.waveMaxHeight = (int) digitValue(waveMaxHeight, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 返回dp, px, sp...
     *
     * @param value 数据
     * @param unit  单位：dp为{@link TypedValue#COMPLEX_UNIT_DIP},
     *              sp为{@link TypedValue#COMPLEX_UNIT_SP},
     *              px为{@link TypedValue#COMPLEX_UNIT_PX},
     * @return 具体长度数值
     */
    private float digitValue(float value, int unit) {
        return TypedValue.applyDimension(unit, value, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 截取浮点数，保留2位.
     *
     * @param f 需要截取的数
     * @return 截取后的数
     */
    public static float getFloat(float f) {
        return new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
