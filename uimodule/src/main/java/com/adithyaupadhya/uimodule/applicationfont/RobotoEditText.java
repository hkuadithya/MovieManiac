package com.adithyaupadhya.uimodule.applicationfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.adithyaupadhya.uimodule.R;

/**
 * Created by adithya.upadhya on 19-01-2016.
 */
public class RobotoEditText extends EditText {
    public RobotoEditText(Context context) {
        super(context);
        initializeRobotoEditText(context, null, 0);

    }

    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRobotoEditText(context, attrs, 0);

    }

    public RobotoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRobotoEditText(context, attrs, defStyleAttr);

    }

    private void initializeRobotoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        if(isInEditMode())
            return;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RobotoEditText, defStyleAttr, 0);
            if (typedArray != null){
                int fontType = typedArray.getInteger(R.styleable.RobotoTextView_fontType, 1);
                Typeface typeface = FontTypeface.returnFontTypeface(fontType, context);
                super.setTypeface(typeface);
                typedArray.recycle();
            }
        }
    }

}
