package netp.tj.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;



import android.view.MotionEvent;

public class DrawView extends View  {
    private static final String TAG = "DrawView";
    /*
         * Bitmap to hold the pixels
         */
    private Bitmap mBitmap;

    /*
     * Canvas class holds the "draw" calls.
     */
    private Canvas mCanvas;

    /*
     * Path to draw
     */
    private Path mPath;
    /*
     * Paint (to describe the colors and styles for the drawing) for Bitmap
     */
    private Paint mBitmapPaint;

    /*
     * Paint (to describe the colors and styles for the drawing) for Path
     */
    private Paint mPaint;
    /*
     * Hold coordinates of previous touch
     */
    private float mX, mY;
    /*
     * Tolerance value for touch events (in pixels)
     */
    private static final float TOUCH_TOLERANCE = 4;

    DrawViewListener drawViewListener;

    public DrawView(Context context,Paint mPaint,DrawViewListener dl) {
        super(context);
        mPath = new Path();
        this.mPaint = mPaint;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        drawViewListener=dl;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        /*
         * If you need to create a new Canvas,
         * then you must define the Bitmap upon which drawing will actually be performed.
         * The Bitmap is always required for a Canvas.
         */

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        /*
         * The initial background color of the canvas
         */
        canvas.drawColor(0xFFAAAAAA);

        /*
         * Carry bitmap to this canvas
         */
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        /*
         * Draw path on the canvas
         */
        canvas.drawPath(mPath, mPaint);
    }


    /**
     * Start of drawing upon touch. Function reused in both Craete & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void touch_start(float x, float y) {


        //Log.d(TAG,"Touch_start : " + x + ","+ y + "  " + mX + "," + mY);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * Continue drawing. Function reused for both Create & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void touch_move(float x, float y,float mmX,float mmY) {

        float dx = Math.abs(x - mmX);
        float dy = Math.abs(y - mmY);
        //if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {//undo it maybe
            //Make bezier curve through the points
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            //Log.d(TAG,"Touch_move : " + x + ","+ y + "  " + mX + "," + mY);


        //}
    }

    /**
     * Stop drawing once finger lifted
     */
    private void touch_up(float mX,float mY) {
        //mPath.lineTo(mX, mY);


        Log.d(TAG, "Touch_end : " + mX + "," + mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /*
         * Get coordinates of touch
         */
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * When finger is put down on the touch screen
                 * Start the recording of touches for this path
                 */
                touch_start(x, y);
                invalidate();

                //send it
                drawViewListener.OnDrawn(false, x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                 * When finger is moved on the touch screen
                 * Continue the recording of touches
                 */
                touch_move(x, y,mX,mY);
                invalidate();

                //send it
                drawViewListener.OnDrawn(mX,mY,x,y);
                break;
            case MotionEvent.ACTION_UP:
                /*
                 * When finger is lifted from the touch screen
                 * Stop the recording of touches for this path
                 */
                touch_up(mX,mY);
                invalidate();
                //send it
                drawViewListener.OnDrawn(true, x,y);
                break;
        }
        return true;
    }

    void simulateDraw(float oldx,float oldy,float newx,float newy){
        Log.d(TAG,"simulating"+oldx+" "+oldy+" "+newx+" "+newy);
        //touch_move(oldx,oldy,newx,newy);
        touch_move(newx,newy,oldx,oldy);
        invalidate();

    }

    void simulateStart(float x,float y){
        Log.d(TAG,"simulating start "+x+" "+y);
        touch_start(x,y);
        invalidate();

    }

    void simulateEnd(float x, float y){
        Log.d(TAG,"simulating end"+x+" "+y);
        touch_up(x,y);
        invalidate();
    }
}














/**
 * TODO: document your custom view class.
 *//*
public class DrawView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public DrawView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrawView, defStyle, 0);

        mExampleString = getContext().getString(R.string.exampleString);
        mExampleColor = a.getColor(
                R.styleable.DrawView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.DrawView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.DrawView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.DrawView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);

        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    *//**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     *//*
    public String getExampleString() {
        return mExampleString;
    }

    *//**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     *//*
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    *//**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     *//*
    public int getExampleColor() {
        return mExampleColor;
    }

    *//**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     *//*
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    *//**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     *//*
    public float getExampleDimension() {
        return mExampleDimension;
    }

    *//**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     *//*
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    *//**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     *//*
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    *//**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     *//*
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
*/