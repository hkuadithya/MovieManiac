package com.adithyaupadhya.uimodule.triangleshape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by adithya.upadhya on 19-06-2016.
 */
public class TriangleShape extends View {
    private Paint paint;
    private Path path;

    public TriangleShape(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
    }

    public TriangleShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
    }

    public TriangleShape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();

        path.moveTo(w, 0);
        path.lineTo(w, w);
        path.lineTo(0, w);
        path.lineTo(w, 0);
        path.close();

        paint.setColor(Color.BLACK);

        canvas.drawPath(path, paint);
    }
}
