package taskr.se.taskr.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.SparseIntArray;
import android.view.View;
import taskr.se.taskr.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacoblodenius on 22/05/17.
 */

public class CircleSegment extends View {
    private Bitmap back;
    private Bitmap fill;
    private Bitmap backTwo;
    private Bitmap fillTwo;
    private Bitmap backOne;
    private Bitmap fillOne;
    private BitmapShader shaderOne;
    private BitmapShader shaderTwo;
    private Paint paint;
    private RectF oval;
    private Paint textPaint;
    private Paint titlePaint;
    private String text;
    private String title;
    private float angle;
    private boolean active;
    private SparseIntArray imageMap;

    public CircleSegment(Context context, String text, String title, float angle, int resIdBack, int resIdFill) {
        super(context);
        this.text = text;
        this.title = title;
        this.angle = angle;

        Resources res = getResources();
        back = BitmapFactory.decodeResource(res, resIdBack);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill = BitmapFactory.decodeResource(res, resIdFill);
        paint.setShader(new BitmapShader(fill, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        oval = new RectF(0, 0, back.getWidth(), back.getHeight());
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#979797"));
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(40);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        imageMap = new SparseIntArray();
        imageMap.put(R.drawable.grey_small1, R.drawable.grey1);
        imageMap.put(R.drawable.grey_small2, R.drawable.grey2);
        imageMap.put(R.drawable.green_small1, R.drawable.green1);
        imageMap.put(R.drawable.green_small2, R.drawable.green2);
        imageMap.put(R.drawable.orange_small1, R.drawable.orange1);
        imageMap.put(R.drawable.orange_small2, R.drawable.orange2);
        imageMap.put(R.drawable.blue_small1, R.drawable.blue1);
        imageMap.put(R.drawable.blue_small2, R.drawable.blue2);

        backOne = back;
        fillOne = fill;
        backTwo = BitmapFactory.decodeResource(res, imageMap.get(resIdBack));
        fillTwo = BitmapFactory.decodeResource(res, imageMap.get(resIdFill));
        shaderOne = new BitmapShader(fillOne, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shaderTwo = new BitmapShader(fillTwo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(active) {
            titlePaint.setColor(Color.parseColor("#F4A536"));
            back = backTwo;
            fill = fillTwo;
            paint.setShader(shaderTwo);
        }
        else {
            titlePaint.setColor(Color.parseColor("#979797"));
            back = backOne;
            fill = fillOne;
            paint.setShader(shaderOne);
        }

        canvas.translate((getWidth() - back.getWidth()) / 2, (getHeight() - back.getHeight() + textPaint.ascent()) / 2);
        canvas.drawBitmap(back, 0, 0, null);
        canvas.drawArc(oval, -90, angle, true, paint);
        canvas.drawText(text,
                back.getWidth() / 2,
                (back.getHeight() - textPaint.ascent()) / 2,
                textPaint);

        canvas.drawText(title, back.getWidth() / 2, back.getHeight() - (textPaint.ascent() * 2) / 2, titlePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = back.getHeight() - (int)textPaint.ascent() * 2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }
}
