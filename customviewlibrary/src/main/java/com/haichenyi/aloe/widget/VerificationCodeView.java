package com.haichenyi.aloe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.haichenyi.aloe.impl.MyTextWatcher;


/**
 * Created by Aloe.Zheng on 2017/9/21.
 * 验证码输入框
 */

public class VerificationCodeView extends ViewGroup {
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_PASSWORD = "password";
    private static final String TYPE_PHONE = "phone";
    private String inputType = TYPE_PASSWORD;
    private int box = 6;
    private int boxWidth = 120;
    private int boxHeight = 120;
    private int boxColor;
    private int childHPadding = 14;
    private int childVPadding = 14;
    private Drawable boxBgFocus = null;
    private Drawable boxBgNormal = null;
    private OnCompleteListener onCompleteListener;

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public interface OnCompleteListener {
        void onComplete(String content);
    }

    public VerificationCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerificationCodeView);
        box = array.getInt(R.styleable.VerificationCodeView_box, 4);
        childHPadding = (int) array.getDimension(R.styleable.VerificationCodeView_child_h_padding, 0);
        childVPadding = (int) array.getDimension(R.styleable.VerificationCodeView_child_v_padding, 0);
        boxBgFocus = array.getDrawable(R.styleable.VerificationCodeView_box_bg_focus);
        boxBgNormal = array.getDrawable(R.styleable.VerificationCodeView_box_bg_normal);
        inputType = array.getString(R.styleable.VerificationCodeView_inputType);
        boxColor = array.getColor(R.styleable.VerificationCodeView_box_color, Color.WHITE);
        boxWidth = (int) array.getDimension(R.styleable.VerificationCodeView_child_width, boxWidth);
        boxHeight = (int) array.getDimension(R.styleable.VerificationCodeView_child_height, boxHeight);
        array.recycle();
        initView();
    }

    private void initView() {
        MyTextWatcher textWatcher = new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    focus();
                    checkAndCommit();
                }
            }
        };
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    backFocus();
                }
                return false;
            }
        };
        int paddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        for (int i = 0; i < box; i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(boxWidth, boxHeight);
            layoutParams.bottomMargin = childVPadding;
            layoutParams.topMargin = childVPadding;
            layoutParams.leftMargin = childHPadding;
            layoutParams.rightMargin = childHPadding;
            layoutParams.gravity = Gravity.CENTER;
            EditText editText = new EditText(getContext());
            editText.setOnKeyListener(onKeyListener);
            setBg(editText, false);
            editText.setTextColor(boxColor);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.CENTER);
            editText.setPadding(0, 0, 0, paddingBottom);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            if (TYPE_NUMBER.equals(inputType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (TYPE_PASSWORD.equals(inputType)) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (TYPE_TEXT.equals(inputType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (TYPE_PHONE.equals(inputType)) {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            editText.setId(i);
            editText.setEms(1);
            editText.addTextChangedListener(textWatcher);
            addView(editText, i);
        }
    }

    private void backFocus() {
        int count = getChildCount();
        EditText editText;
        for (int i = count - 1; i >= 0; i--) {
            editText = (EditText) getChildAt(i);
            if (editText.getText().length() == 1) {
                editText.requestFocus();
                editText.setSelection(1);
                return;
            }
        }
    }

    private void focus() {
        int count = getChildCount();
        EditText editText;
        for (int i = 0; i < count; i++) {
            editText = (EditText) getChildAt(i);
            if (editText.getText().length() < 1) {
                editText.requestFocus();
                return;
            }
        }
    }

    private void setBg(EditText editText, boolean focus) {
        if (boxBgNormal != null && !focus) {
            editText.setBackground(boxBgNormal);
        } else if (boxBgFocus != null && focus) {
            editText.setBackground(boxBgFocus);
        }
    }

    private void checkAndCommit() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean full = true;
        for (int i = 0; i < box; i++) {
            EditText editText = (EditText) getChildAt(i);
            String content = editText.getText().toString();
            if (content.length() == 0) {
                full = false;
                break;
            } else {
                stringBuilder.append(content);
            }
        }
        if (full) {
            if (onCompleteListener != null) {
                onCompleteListener.onComplete(stringBuilder.toString());
                setEnabled(false);
            }
        }
    }

    /**
     * 清除内容.
     */
    public void cleanTxt() {
        setEnabled(true);
        int count = getChildCount();
        EditText editText = null;
        for (int i = count - 1; i >= 0; i--) {
            editText = (EditText) getChildAt(i);
            editText.setText("");
        }
        if (null != editText) {
            editText.requestFocus();
            editText.setSelection(0);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
    /*int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);
      child.setEnabled(enabled);
    }*/
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        if (count > 0) {
            View child = getChildAt(0);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            int maxH = height + 2 * childVPadding;
            int maxW = (width + childHPadding) * box + childHPadding;
            setMeasuredDimension(resolveSize(maxW, widthMeasureSpec),
                    resolveSize(maxH, heightMeasureSpec));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setVisibility(View.VISIBLE);
            int w = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int cl = (i) * (w + childHPadding);
            int cr = cl + w;
            int ct = childVPadding;
            int cb = ct + height;
            child.layout(cl, ct, cr, cb);
        }
    }
}
