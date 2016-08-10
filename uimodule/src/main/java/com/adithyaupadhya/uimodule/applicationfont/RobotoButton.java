package com.adithyaupadhya.uimodule.applicationfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.adithyaupadhya.uimodule.R;

/**
 * Created by adithya.upadhya on 05-02-2016.
 */
public class RobotoButton extends Button {
    public RobotoButton(Context context) {
        super(context);
        initializeRobotoButton(context, null, 0);
    }

    public RobotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRobotoButton(context, attrs, 0);
    }

    public RobotoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRobotoButton(context, attrs, 0);
    }

    private void initializeRobotoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        if(isInEditMode())
            return;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RobotoButton, defStyleAttr, 0);
            if (typedArray != null) {
                int fontType = typedArray.getInteger(R.styleable.RobotoButton_fontType, 1);
                Typeface typeface = FontTypeface.returnFontTypeface(fontType, context);
                super.setTypeface(typeface);
                typedArray.recycle();
            }
        }
    }
}
