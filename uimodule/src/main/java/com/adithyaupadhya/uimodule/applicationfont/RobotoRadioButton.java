package com.adithyaupadhya.uimodule.applicationfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.adithyaupadhya.uimodule.R;

/**
 * Created by adithya.upadhya on 19-06-2016.
 */
public class RobotoRadioButton extends RadioButton {
    public RobotoRadioButton(Context context) {
        super(context);
        initializeRobotRadioButton(context, null, 0);
    }

    public RobotoRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRobotRadioButton(context, attrs, 0);
    }

    public RobotoRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRobotRadioButton(context, attrs, defStyleAttr);
    }

    private void initializeRobotRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        if (isInEditMode())
            return;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RobotoRadioButton, defStyleAttr, 0);
            if (typedArray != null) {
                int fontType = typedArray.getInteger(R.styleable.RobotoRadioButton_fontType, 1);
                Typeface typeface = FontTypeface.returnFontTypeface(fontType, context);
                super.setTypeface(typeface);
                typedArray.recycle();
            }
        }
    }

}
