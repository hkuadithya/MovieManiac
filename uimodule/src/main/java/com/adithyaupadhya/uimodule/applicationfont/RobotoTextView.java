package com.adithyaupadhya.uimodule.applicationfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.adithyaupadhya.uimodule.R;


/**
 * Created by adithya.upadhya on 19-01-2016.
 */
public class RobotoTextView extends TextView {
    public RobotoTextView(Context context) {
        super(context);
        initializeRobotoTextView(context, null, 0);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRobotoTextView(context, attrs, 0);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRobotoTextView(context, attrs, defStyleAttr);
    }

    private void initializeRobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        if(isInEditMode())
            return;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView, defStyleAttr, 0);
            if (typedArray != null){
                int fontType = typedArray.getInteger(R.styleable.RobotoTextView_fontType, 1);
                Typeface typeface = FontTypeface.returnFontTypeface(fontType, context);
                super.setTypeface(typeface);
                typedArray.recycle();
            }
        }
    }

}
