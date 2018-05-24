package com.haichenyi.aloe.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.haichenyi.aloe.interfaces.AnimationInterface;
import com.haichenyi.aloe.tools.ToolsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 海灬琼
 * @date 2017/11/29
 * @desc 圆圈加载进度
 */
public class CircleProgressView extends View {
    private int mWidth, mHeight;
    private Paint mPaintCircle, mPainCenterText, mPainBottomText, mPaintProgress, mPaintImgSuccessFailed,
            mPaintSmallCircle, mPaintShadow;
    private float radius = ToolsUtils.digitValue(50, TypedValue.COMPLEX_UNIT_DIP);
    private float mPaintImgSuccessFailedWidth = ToolsUtils.digitValue(3, TypedValue.COMPLEX_UNIT_DIP);//画勾和X的画笔宽度
    private float paintCircleWidth = ToolsUtils.digitValue(7, TypedValue.COMPLEX_UNIT_DIP), //圆画笔的宽度
            radiusPointShadow = paintCircleWidth, radiusPointCurrentShadow = radiusPointShadow;
    private float textSize = ToolsUtils.digitValue(20, TypedValue.COMPLEX_UNIT_DIP);//字的大小
    // 结果文字上方外边距.
    private int resultTextMarginTop = (int) ToolsUtils.digitValue(20, TypedValue.COMPLEX_UNIT_DIP);
    private float pathTextWidth = 3f;//字画笔的宽度
    private Path pathCircle, pathProgress, pathDstProgress, pathSuccess, pathDstSuccess, pathFailed,
            pathDstFailed;
    private float mCurrent = 0;//当前动画进度
    private String textContentCenter = (int) (mCurrent * 100) + "%";
    private String textContentBottom = "";
    private static final int startColor = 0xFF04EAF6; // 圆环渐变 开始颜色.
    private static final int endColor = 0xFF6A65FE; // 圆环渐变 结束颜色
    public static final int DRAWPROGRESS = 1;//进度
    public static final int SUCCESS = 2;//成功
    public static final int FAILED = 3;//失败
    private static int DRAW_TYPE = DRAWPROGRESS;
    private PathMeasure pathMeasureProgress, pathMeasureSuccess, pathMeasureFailed;
    private float currentDistance;
    private ValueAnimator successAnim, failedAnimLeft, failedAnimRight, shadowAnim;
    private float length;
    private long animatorTime = 3000;//动画时长
    private long shadowAnimatorTime = 500;//阴影动画时间
    private float currentProgress = 0;//当前进度
    private List<ValueAnimator> animatorList = new ArrayList<>();
    private AnimationInterface animationInterface;
    private float[] pointXY = new float[2];
    private float pointX, pointY; // 小白球点坐标
    private String mContent;//文本内容

    public CircleProgressView(Context context) {
        super(context);
        init(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initPaint(context);
        initPath();
        initMeasure();
        mContent = context.getString(R.string.msg_refactory_binding);
    }

    private void initMeasure() {
        pathMeasureSuccess = new PathMeasure();
        pathMeasureSuccess.setPath(pathSuccess, false);
        pathMeasureFailed = new PathMeasure();
        pathMeasureFailed.setPath(pathFailed, false);
        pathMeasureProgress = new PathMeasure();
        pathMeasureProgress.setPath(pathProgress, true);
    }

    private void initPath() {
        //背景圆
        pathCircle = new Path();
        pathCircle.addCircle(0, 0, radius, Path.Direction.CW);
        //当前进度圆
        pathDstProgress = new Path();
        pathProgress = new Path();
        pathProgress.addCircle(0, 0, radius, Path.Direction.CW);
        //成功勾
        pathDstSuccess = new Path();
        pathSuccess = new Path();
        pathSuccess.moveTo(-radius / 3, -radius / 7);
        pathSuccess.lineTo(0, radius / 5);
        pathSuccess.lineTo(radius / 2, -radius / 2);
        //失败X
        pathDstFailed = new Path();
        pathFailed = new Path();
        Path pathFailedLeft = new Path();
        pathFailedLeft.moveTo(-radius / 3, -radius / 3);
        pathFailedLeft.lineTo(radius / 3, radius / 3);
        Path pathFailedRight = new Path();
        pathFailedRight.moveTo(radius / 3, -radius / 3);
        pathFailedRight.lineTo(-radius / 3, radius / 3);
        pathFailed.addPath(pathFailedLeft);
        pathFailed.addPath(pathFailedRight);
    }

    private void initPaint(Context context) {
        //背景圆画笔
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setColor(ContextCompat.getColor(context, R.color.progress_circle));
        mPaintCircle.setStrokeWidth(paintCircleWidth);
        mPaintCircle.setStrokeCap(Paint.Cap.ROUND);
        //当前进度圆画笔
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        //设置渐变颜色
        LinearGradient shaderCurrent = new LinearGradient(0, -radius, 0, radius,
                new int[]{startColor, endColor},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP);
        mPaintProgress.setShader(shaderCurrent);
        mPaintProgress.setStrokeWidth(paintCircleWidth);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        //中间进度字画笔
        mPainCenterText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPainCenterText.setAntiAlias(true);
        mPainCenterText.setStyle(Paint.Style.STROKE);
        mPainCenterText.setTextAlign(Paint.Align.CENTER);
        mPainCenterText.setStrokeCap(Paint.Cap.ROUND);
        mPainCenterText.setTextSize(textSize);
        LinearGradient shaderText = new LinearGradient(0, -
                textSize / 2, 0, textSize / 2,
                new int[]{startColor, endColor},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP);
        mPainCenterText.setShader(shaderText);
        //下方文字画笔
        mPainBottomText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPainBottomText.setAntiAlias(true);
        mPainBottomText.setStyle(Paint.Style.STROKE);
        mPainBottomText.setTextAlign(Paint.Align.CENTER);
        mPainBottomText.setStrokeCap(Paint.Cap.ROUND);
        mPainBottomText.setTextSize(textSize);
        mPainBottomText.setColor(ContextCompat.getColor(context, R.color.white));
        //成功(勾)，失败(X)的画笔
        mPaintImgSuccessFailed = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintImgSuccessFailed.setAntiAlias(true);
        mPaintImgSuccessFailed.setStyle(Paint.Style.STROKE);
        mPaintImgSuccessFailed.setTextAlign(Paint.Align.CENTER);
        mPaintImgSuccessFailed.setStrokeCap(Paint.Cap.ROUND);
        mPaintImgSuccessFailed.setStrokeWidth(mPaintImgSuccessFailedWidth);
        mPaintImgSuccessFailed.setTextSize(textSize);
        mPaintImgSuccessFailed.setShader(shaderText);
        //小圆球的画笔
        mPaintSmallCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSmallCircle.setAntiAlias(true);
        mPaintSmallCircle.setStyle(Paint.Style.FILL);
        mPaintSmallCircle.setColor(0xB3FFFFFF);
        mPaintSmallCircle.setStrokeCap(Paint.Cap.ROUND);
        //阴影画笔
        mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShadow.setAntiAlias(true);
        mPaintShadow.setStyle(Paint.Style.FILL);
        RadialGradient shadow = new RadialGradient(0, 0, radiusPointShadow - 2,
                new int[]{0xB3FFFFFF, 0x00FFFFFF},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP);
        mPaintShadow.setShader(shadow);
        mPaintShadow.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int minWidth = 400;
        int minHeight = 200;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
                && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minWidth, minHeight);
        } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minWidth, heightSize);
        } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, minHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        radius = Math.min(w, h) / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //画布设置抗锯齿
        canvas.translate(mWidth / 2, mHeight / 2);
        switch (DRAW_TYPE) {
            case DRAWPROGRESS://进度
                canvas.drawPath(pathCircle, mPaintCircle);//画背景圆
                canvas.drawText(textContentCenter, 0, textSize / 2, mPainCenterText);//画进度文字
                currentDistance = length * mCurrent;
                pathMeasureProgress.getSegment(0, currentDistance, pathDstProgress, true);
                pathMeasureProgress.getPosTan(currentDistance, pointXY, null);
                pointX = pointXY[0];
                pointY = pointXY[1];
                canvas.drawText(mContent, 0, radius + paintCircleWidth + textSize / 2 + resultTextMarginTop,
                        mPainBottomText);//画bottom文字
                canvas.rotate(-90);//画布旋转90度，从顶部开始画外圈的圆
                canvas.drawPath(pathDstProgress, mPaintProgress);//画当前进度圆
                canvas.save();
                canvas.translate(pointX, pointY);
                canvas.drawCircle(0, 0, paintCircleWidth / 2, mPaintSmallCircle);
                canvas.drawCircle(0, 0, radiusPointCurrentShadow, mPaintShadow);
                canvas.restore();
                break;
            case SUCCESS://成功
                canvas.drawPath(pathCircle, mPaintProgress);//画背景圆
                currentDistance = length * mCurrent;
                pathMeasureSuccess.getSegment(0, currentDistance, pathDstSuccess, true);
                canvas.drawPath(pathDstSuccess, mPaintImgSuccessFailed);//画成功勾path
                canvas.drawText(textContentBottom, 0, radius + paintCircleWidth + textSize / 2 +
                        resultTextMarginTop, mPainBottomText);//画bottom文字
                break;
            case FAILED://失败
                canvas.drawPath(pathCircle, mPaintProgress);//画背景圆
                currentDistance = length * mCurrent;
                pathMeasureFailed.getSegment(0, currentDistance, pathDstFailed, true);
                canvas.drawPath(pathDstFailed, mPaintImgSuccessFailed);//画失败Xpath
                canvas.drawText(textContentBottom, 0, radius + paintCircleWidth + textSize / 2 +
                        resultTextMarginTop, mPainBottomText);//画bottom文字
                break;
        }
    }

    /**
     * 设置当前进度
     *
     * @param progress 0-1f之间的数字
     */
    public void setProgress(float progress) {
        DRAW_TYPE = DRAWPROGRESS;
        initProgress(currentProgress, progress);
        currentProgress = progress;
    }

    /**
     * 初始化进度动画
     *
     * @param current  当前进度
     * @param progress 需要设置的进度
     */
    private void initProgress(float current, float progress) {
        ValueAnimator progressAnim = ValueAnimator.ofFloat(current, progress);
        animatorList.add(progressAnim);
        progressAnim.setDuration(animatorTime);
//    progressAnim.setRepeatCount(ValueAnimator.INFINITE);
        length = pathMeasureProgress.getLength();
        progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrent = (float) animation.getAnimatedValue();
                textContentCenter = (int) (mCurrent * 100) + "%";
                invalidate();
            }
        });
        progressAnim.start();
        if (null == shadowAnim) {
            shadowAnim = ValueAnimator.ofFloat(0.25f, 1f);
            animatorList.add(shadowAnim);
            shadowAnim.setDuration(shadowAnimatorTime);
            shadowAnim.setRepeatCount(ValueAnimator.INFINITE);
            shadowAnim.setRepeatMode(ValueAnimator.RESTART);
            shadowAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    radiusPointCurrentShadow = radiusPointShadow * animatedValue;
                    invalidate((int) -radius, (int) -radius, (int) radius, (int) radius);
                }
            });
            shadowAnim.start();
        }
    }

    /**
     * 设置动画时间
     *
     * @param animatorTime 动画时间，单位：毫秒
     */
    @SuppressWarnings("unused")
    public void setAnimatorDuration(long animatorTime) {
        this.animatorTime = animatorTime;
    }

    /**
     * 绘制成功
     *
     * @param success 需要绘制的文字
     */
    public void startSuccess(CharSequence success) {
        DRAW_TYPE = SUCCESS;
        textContentBottom = (String) success;
        mPaintProgress.setStrokeWidth(mPaintImgSuccessFailedWidth);
        if (null == successAnim) {
            initSuccessAnim();
        }
    }

    /**
     * 初始化成功动画
     */
    private void initSuccessAnim() {
        successAnim = ValueAnimator.ofFloat(0f, 1);
        animatorList.add(successAnim);
        successAnim.setDuration(animatorTime / 3);
//    successAnim.setRepeatCount(2);
        successAnim.setInterpolator(new LinearInterpolator());
        length = pathMeasureSuccess.getLength();
        successAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        successAnim.start();
        //success动画结束监听
        successAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (null != animationInterface) {
                    animationInterface.AnimationEnd();
                }
            }
        });
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    /**
     * 绘制失败
     *
     * @param failed 需要绘制的文字
     */
    public void startFailed(CharSequence failed) {
        DRAW_TYPE = FAILED;
        textContentBottom = (String) failed;
        mPaintProgress.setStrokeWidth(mPaintImgSuccessFailedWidth);
        if (null == failedAnimLeft) {
            initFailedAnim();
        }
    }

    public float getmCurrent() {
        return mCurrent;
    }

    /**
     * 初始化失败动画，分成两种左边和右边
     */
    private void initFailedAnim() {
        failedAnimLeft = ValueAnimator.ofFloat(0f, 1);
        animatorList.add(failedAnimLeft);
        failedAnimLeft.setDuration(animatorTime / 4);
        failedAnimLeft.setInterpolator(new LinearInterpolator());
        length = pathMeasureFailed.getLength();
        failedAnimLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrent = (float) animation.getAnimatedValue();
                if (1 == mCurrent) {  //跳转到下一个path，根据path的添加顺序
                    currentDistance = 0;
                    pathMeasureFailed.nextContour();
                }
                invalidate((int) -radius / 3, (int) -radius / 3, (int) radius / 3, (int) radius / 3);
            }
        });
        failedAnimLeft.start();
        failedAnimRight = ValueAnimator.ofFloat(0f, 1);
        animatorList.add(failedAnimRight);
        failedAnimRight.setDuration(animatorTime / 4);
        length = pathMeasureFailed.getLength();
        failedAnimRight.setInterpolator(new LinearInterpolator());
        failedAnimRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrent = (float) animation.getAnimatedValue();
                invalidate((int) -radius / 3, (int) -radius / 3, (int) radius / 3, (int) radius / 3);
            }
        });
        //left动画结束开始right动画
        failedAnimLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                failedAnimRight.start();
            }
        });
        //right动画结束监听
        failedAnimRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (null != animationInterface) {
                    animationInterface.AnimationEnd();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void onDestroy() {
        for (ValueAnimator animator : animatorList) {
            if (null != animator) {
                animator.cancel();
                animator = null;
            }
        }
    }

    public void setAnimationListener(AnimationInterface animationListener) {
        this.animationInterface = animationListener;
    }
}
