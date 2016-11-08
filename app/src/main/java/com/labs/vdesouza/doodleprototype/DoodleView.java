package com.labs.vdesouza.doodleprototype;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoodleView extends View {


    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    // Object that holds current paint and path set to the last color picked
    class PaintPath {
        //defines how to draw
        private Paint mPaint;
        //drawing path
        private Path mPath;

        public  PaintPath() {
            mPath = new Path();
            mPaint = new Paint();
            mPaint.setColor(currentPaintColor );
            mPaint.setAlpha(currentPaintAlpha);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(currentBrushSize);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        public PaintPath(Paint paint, Path path, int color, float brushSize, int alpha) {
            mPaint = paint;
            mPath = path;
            mPaint.setColor(color);
            mPaint.setAntiAlias(true);
            mPaint.setAlpha(alpha);
            mPaint.setStrokeWidth(brushSize);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        public Paint getPaint() {
            return mPaint;
        }

        public Path getPath() {
            return mPath;
        }
    }

    //current color
    private int currentPaintColor = Color.parseColor("#212121");

    //current alpha
    private int currentPaintAlpha = 255;

    //canvas - holding pen, holds your drawings
    //and transfers them to the view
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;

    //brush size
    private float currentBrushSize, lastBrushSize;

    private List<PaintPath> paths = new ArrayList<PaintPath>();
    private List<PaintPath> undoPaths = new ArrayList<PaintPath>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public PaintPath currentPaintPath;

    private void init(AttributeSet attrs, int defStyle) {
        currentBrushSize = 30;
        lastBrushSize = currentBrushSize;
        currentPaintPath = new PaintPath();
        paths.add(currentPaintPath);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (PaintPath p : paths) {
            canvas.drawPath(p.getPath(), p.getPaint());
        }
        canvas.drawPath(currentPaintPath.getPath(), currentPaintPath.getPaint());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_down(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }


    private void touch_down(float x, float y) {
        undoPaths.clear();
        currentPaintPath.getPath().reset();
        currentPaintPath.getPath().moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_up() {
        currentPaintPath.getPath().lineTo(mX, mY);
        drawCanvas.drawPath(currentPaintPath.getPath(), currentPaintPath.getPaint());
        paths.add(currentPaintPath);
        currentPaintPath = new PaintPath();

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentPaintPath.getPath().quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }


    public void onClickUndo() {
        if (paths.size() > 0) {
            undoPaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    public void onClickRedo() {
        if (undoPaths.size() > 0) {
            paths.add(undoPaths.remove(undoPaths.size() - 1));
            invalidate();
        }
    }

    public void onClickClearAll() {
        if (paths.size() > 0) {
            undoPaths.addAll(paths);
            invalidate();
        }
        paths.removeAll(paths);
        invalidate();
    }

    public int getCurrentBrushSize() {
        return (int) currentPaintPath.getPaint().getStrokeWidth();
    }
    public int getCurrentColor() {
        return currentPaintPath.getPaint().getColor();
    }
    public int getCurrentAlpha() {
        return currentPaintPath.getPaint().getAlpha();
    }

    public void setPaintColor(int color) {
        invalidate();
        currentPaintColor = color;
        currentPaintPath = new PaintPath(currentPaintPath.getPaint(), currentPaintPath.getPath(),
                currentPaintColor, currentBrushSize, currentPaintAlpha );
    }

    public void setBrushSize(float newSize) {
        invalidate();
        if (newSize == 0) {
            newSize = (float) 1.0;
        }
        currentBrushSize = newSize;
        currentPaintPath = new PaintPath(currentPaintPath.getPaint(), currentPaintPath.getPath(),
                currentPaintColor, currentBrushSize, currentPaintAlpha);
    }

    public void setBrushOpacity(int alpha) {
        invalidate();
        currentPaintAlpha = alpha;
        currentPaintPath = new PaintPath(currentPaintPath.getPaint(), currentPaintPath.getPath(),
                currentPaintColor, currentBrushSize, currentPaintAlpha );
    }
}