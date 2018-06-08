package com.haichenyi.aloe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @Title: 银行卡号输入框(4位一空)
 * @Description:
 * @Author: wz
 * @Date: 2018/6/8
 * @Version: V1.0
 */
public class BankEditText extends AppCompatEditText {
    private final String TAG = "MyEditText";
    private Drawable dRight;
    private Rect rBounds;
    private String strEditText = "null";

    public BankEditText(Context paramContext) {
        super(paramContext);
        initEditText();
    }

    public BankEditText(Context paramContext,
                        AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initEditText();
    }

    public BankEditText(Context paramContext,
                        AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initEditText();
    }

    private void initEditText() {
        setEditTextDrawable();
        addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;
            // 记录光标的位置
            int location = 0;
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int kongNumberB = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                BankEditText.this.setEditTextDrawable();
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;

                strEditText = s.toString();
                // 13位条形码加2空格
                if (s.toString().length() == 15) {

                } else {
                    strEditText = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                kongNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        kongNumberB++;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                if (isChanged) {

                    location = getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }
                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > kongNumberB) {
                        location += (konggeNumberC - kongNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    setText(str);
                    Editable etAble = getText();
                    Selection.setSelection(etAble, location);
                    isChanged = false;

                }
            }
        });

    }

    public void setEditTextDrawable() {
        if (getText().toString().length() == 0) {
            setCompoundDrawables(null, null, null, null);
        } else {
            setCompoundDrawables(null, null, this.dRight, null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.dRight = null;
        this.rBounds = null;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if ((this.dRight != null) && (paramMotionEvent.getAction() == 1)) {
            this.rBounds = this.dRight.getBounds();
            int i = (int) paramMotionEvent.getRawX();

            if (i > getRight() - 3 * this.rBounds.width()) {
                setText("");
                paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(paramMotionEvent);
    }

    @Override
    public void setCompoundDrawables(Drawable paramDrawable1,
                                     Drawable paramDrawable2, Drawable paramDrawable3,
                                     Drawable paramDrawable4) {
        if (paramDrawable3 != null) {
            this.dRight = paramDrawable3;
        }
        super.setCompoundDrawables(paramDrawable1, paramDrawable2,
                paramDrawable3, paramDrawable4);
    }
}