package com.example.zangdonglai.scratchwheel;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class ScratchWheel extends ImageView {
    //Circle Center Point
    protected final PointF wheelCenter = new PointF();

    // record lastRotation
    private double lastRotation;

    //when scratch the wheel ,we calculate  the degree to  rotate Canvas
    protected float absoluteDiscRotation = 0;

    //control whether autoRotate
    private boolean MuteAutoRotate = false;

    //every 3ms , it will rotate the wheel
    private int ROTATE_DURATION = 3;

    //the degree every rotate
    private float rotateDegree=1.3f;

    //scratch point
    private PointF touchPosition = new PointF();

    //the wheel's radius ,default is 1.f
    protected float radiusCircle = 1.f;

    private Resources mResources;

    //paint for the wheel's outer border
    Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public ScratchWheel(Context context) {
        this(context, null);
    }

    public ScratchWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getResources();
        mBorderPaint.setStrokeWidth(4.f * mResources.getDisplayMetrics().density);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.YELLOW);
        setImageResource(R.drawable.default_jog);

        //we use touchListenner Monitor the touchEvent
        setOnTouchListener(touchListenner);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        int newRadius = w >> 1;
        if (radiusCircle == newRadius)
            return;
        radiusCircle = newRadius;

        wheelCenter.x = radiusCircle;
        wheelCenter.y = radiusCircle;

        //calculate the  circle path  for clip a Circle Image
        circlePath = new Path();
        circlePath.moveTo(radiusCircle, radiusCircle);
        circlePath.addCircle(radiusCircle, radiusCircle, radiusCircle, Path.Direction.CCW);
        circlePath.close();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int sizeMin = Math.min(measuredWidth, measuredHeight);

        int sizeMeasureSpec = MeasureSpec.makeMeasureSpec(sizeMin, MeasureSpec.EXACTLY);

        super.onMeasure(sizeMeasureSpec, sizeMeasureSpec);


    }

    Path circlePath;

    @Override
    protected void onDraw(Canvas canvas) {

		canvas.save();
        if (circlePath != null) {
            canvas.clipPath(circlePath);        //clip a circle Image
        }
        canvas.rotate(absoluteDiscRotation, radiusCircle, radiusCircle);   //Rotate the absolute Degree
        super.onDraw(canvas);
		canvas.restore();

        //Draw the outer white border
        canvas.drawCircle(radiusCircle, radiusCircle, radiusCircle - 2.5f * mResources.getDisplayMetrics().density, mBorderPaint);

    }

    private OnTouchListener touchListenner = new OnTouchListener() {

        private double getCurrentAngle(float lastX, float lastY) {
            double newAngle = 0;

            double a = wheelCenter.y  - lastY ;
            double b = lastX - (wheelCenter.x );

            if (b == 0.0) {
                if ( a < 0)
                    newAngle = 270.;
                else if (a > 0)
                    newAngle = 90.;
            } else
                newAngle = Math.toDegrees(Math.atan(a/b));

            if (b < 0 )
                newAngle += 180.;
            else if ( a < 0 && b > 0 )
                newAngle += 360.;
            return newAngle;
        }

        public boolean onTouch(View myView, MotionEvent event) {


            int action = event.getAction();
            touchPosition.x = event.getX();
            touchPosition.y = event.getY();

            if (action==MotionEvent.ACTION_MOVE) {
                        double newRotation = getCurrentAngle(touchPosition.x, touchPosition.y);

                        if (Math.abs(newRotation - lastRotation) > 200.) {
                            if (newRotation < lastRotation)
                                newRotation += 360;
                            else
                                lastRotation += 360;
                        }
                        double rotationDelta = (newRotation - lastRotation);

                        lastRotation = newRotation;
                        while (lastRotation > 360)
                            lastRotation -= 360;


                        if (MuteAutoRotate) {
                            absoluteDiscRotation -= rotationDelta;
                            invalidate();
                        }
            }
            if (action == MotionEvent.ACTION_UP) {
                MuteAutoRotate = false;
                invalidate();
            }
            if (action==MotionEvent.ACTION_DOWN) {
                MuteAutoRotate = true;
                lastRotation = getCurrentAngle(touchPosition.x, touchPosition.y);
            }
            return true;
        }
    };

    //auto rotate

    private Handler mRotateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Rotate(rotateDegree);
            sendEmptyMessageDelayed(0, ROTATE_DURATION);
        }
    };

    public void Rotate(float degree) {
        if (!MuteAutoRotate) {
            absoluteDiscRotation = (absoluteDiscRotation + degree) % 360;
            invalidate();
        }
    }

    public void setRotateState(boolean isPlaying) {
        mRotateHandler.removeCallbacksAndMessages(null);
        if (isPlaying) {
            mRotateHandler.sendEmptyMessage(0);
        }
    }
}
