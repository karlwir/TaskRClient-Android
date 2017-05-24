package taskr.se.taskr.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by jacoblodenius on 22/05/17.
 */

public class CircleSegment extends View {
    private Bitmap back;
    private Paint paint;
    private RectF oval;
    private Paint textPaint;
    private Paint titlePaint;
    private String text;
    private String title;
    private float angle;
    private boolean active;

    public CircleSegment(Context context, String text, String title, float angle, int resIdBack, int resIdFill) {
        super(context);
        this.text = text;
        this.title = title;
        this.angle = angle;
        Resources res = getResources();
        back = BitmapFactory.decodeResource(res, resIdBack);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ring = BitmapFactory.decodeResource(res, resIdFill);
        paint.setShader(new BitmapShader(ring, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        oval = new RectF(0, 0, back.getWidth(), back.getHeight());
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70);
        textPaint.setTextAlign(Paint.Align.CENTER);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(40);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);
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
        canvas.translate((getWidth() - back.getWidth()) / 2, (getHeight() - back.getHeight() + textPaint.ascent()) / 2);
        canvas.drawBitmap(back, 0, 0, null);
        canvas.drawArc(oval, -90, angle, true, paint);
        canvas.drawText(text,
                back.getWidth() / 2,
                (back.getHeight() - textPaint.ascent()) / 2,
                textPaint);

        if(active) titlePaint.setColor(Color.parseColor("#F4A536"));
        else titlePaint.setColor(Color.parseColor("#000000"));
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

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}
