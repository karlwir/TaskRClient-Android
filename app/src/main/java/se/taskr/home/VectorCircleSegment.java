package se.taskr.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kawi01 on 2017-06-07.
 */

public class VectorCircleSegment extends View {

    private float radius;

    Paint paint;
    Paint backPaint;
    Paint numberPaint;
    Paint titlePaint;

    Path myPath;

    RectF outterCircle;
    RectF innerCircle;
    RectF outterCircleActive;
    RectF innerCircleActive;

    String number;
    String title;
    float angle;
    boolean active;

//    public CircleSegment(Context context, String text, String title, float angle, int resIdBack, int resIdFill) {

    public VectorCircleSegment(Context context, AttributeSet attrs, String number, String title, float radius, float angle, int backColor, int fillColor) {
        super(context, attrs);
        this.number = number;
        this.title = title;
        this.angle = angle;
        if (radius > 150) {
            radius = 150;
        }
        this.radius = radius;

        paint = new Paint();
        paint.setDither(true);
        paint.setColor(getResources().getColor(fillColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(radius / 14.0f);

        backPaint = new Paint();
        backPaint.setColor(getResources().getColor(backColor));
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setStrokeJoin(Paint.Join.ROUND);
        backPaint.setStrokeCap(Paint.Cap.ROUND);
        backPaint.setAntiAlias(true);
        backPaint.setStrokeWidth(radius / 14.0f);

        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setTextSize(radius/2);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setColor(Color.parseColor("#979797"));

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(radius/3.5f);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        myPath = new Path();

        outterCircle = new RectF();
        innerCircle = new RectF();

        float adjust = adjust = radius/4f;
        outterCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = radius/2f;
        innerCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);


        outterCircleActive = new RectF();
        innerCircleActive = new RectF();

        adjust = radius/7;
        outterCircleActive.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = radius/2.5f;
        innerCircleActive.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setText(String number) {
        this.number = number;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(active) {
            titlePaint.setColor(Color.parseColor("#F4A536"));
            drawActiveDonut(canvas, backPaint, angle,-359.99f);
            drawActiveDonut(canvas,paint, -90, angle);
            canvas.drawText(title, radius, radius*2 + radius/3 , titlePaint);
        }
        else {
            titlePaint.setColor(Color.parseColor("#979797"));
            drawDonut(canvas, backPaint, angle,-359.99f);
            drawDonut(canvas,paint, -90, angle);
            canvas.drawText(title, radius, radius*2 + radius/4 , titlePaint);
        }

        canvas.drawText(number, radius, radius + (numberPaint.getTextSize() / 3), numberPaint);
    }

    public void drawDonut(Canvas canvas, Paint paint, float start, float sweep){
        myPath.reset();
        myPath.arcTo(outterCircle, start, sweep, false);
        myPath.arcTo(innerCircle, start+sweep, -sweep, false);
        myPath.close();
        canvas.drawPath(myPath, paint);
    }

    public void drawActiveDonut(Canvas canvas, Paint paint, float start, float sweep){
        myPath.reset();
        myPath.arcTo(outterCircleActive, start, sweep, false);
        myPath.arcTo(innerCircleActive, start+sweep, -sweep, false);
        myPath.close();
        canvas.drawPath(myPath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) radius * 2;
        int desiredHeight = (int) (radius * 2 + radius/1.5);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //70dp exact
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}
