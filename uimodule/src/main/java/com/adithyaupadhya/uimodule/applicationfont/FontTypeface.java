package com.adithyaupadhya.uimodule.applicationfont;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by adithya.upadhya on 19-01-2016.
 */
public class FontTypeface {
    public static Typeface returnFontTypeface(int fontType, Context context){
        switch (fontType){
            case 0:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
            case 1:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            case 2:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
            case 3:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            default:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        }
    }
}
