package netp.tj.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;
import android.view.View;



import android.view.MotionEvent;

import java.io.FileOutputStream;
import java.io.IOException;

public class DrawView extends View  {

    FileOutputStream fos = null;
    Bitmap bmpBase = null;

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
     * Path to draw for receiver
     */
    private Path mPath_receiver;

    /*
     * Paint (to describe the colors and styles for the drawing) for Bitmap
     */
    private Paint mBitmapPaint;

    /*
     * Paint (to describe the colors and styles for the drawing) for Path
     */
    private Paint mPaint;

    /*
     * Paint (to describe the colors and styles for the drawing) for Path for receiver
     */
    private Paint mPaint_receiver;

    /*
     * Hold coordinates of previous touch
     */

    private float mX, mY;
    /*
     * Tolerance value for touch events (in pixels)
     */

    private float mX_receiver, mY_receiver;
    /*
     * Tolerance value for touch events (in pixels)
     */
    private static final float TOUCH_TOLERANCE = 4;

    DrawViewListener drawViewListener;

    public DrawView(Context context,Paint mPaint, Paint mPaint_receiver, DrawViewListener dl) {
        super(context);
        mPath = new Path();
        mPath_receiver = new Path();
        this.mPaint = mPaint;
        this.mPaint_receiver = mPaint_receiver;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        drawViewListener=dl;
        setFocusable(true);
        setFocusableInTouchMode(true);

//        bmpBase = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
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
        canvas.drawPath(mPath_receiver, mPaint_receiver);
    }

    public void saveBitmap(){
        try
        {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + System.currentTimeMillis()+".png");
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            fos = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
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
                drawViewListener.OnDrawn(mX,mY,x,y, mPaint.getStrokeWidth(), mPaint.getColor());
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

    void simulateDraw(float oldx,float oldy,float newx,float newy, float width, int color){
        Log.d(TAG,"simulating"+oldx+" "+oldy+" "+newx+" "+newy + ",Width & Color:" + width + "," + color );
        mPaint_receiver.setStrokeWidth(width);
        mPaint_receiver.setColor(color);
        simulateTouchMove(newx,newy,oldx,oldy);
        invalidate();

    }

    void simulateStart(float x,float y){
        Log.d(TAG,"simulating start "+x+" "+y);
        simulateTouchStart(x,y);
        invalidate();

    }

    void simulateEnd(float x, float y){
        Log.d(TAG,"simulating end"+x+" "+y);
        simulateTouchUp(x,y);
        invalidate();
    }

    /**
     * Start of drawing upon touch. Function reused in both Craete & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void simulateTouchStart(float x, float y) {
        //Log.d(TAG,"Touch_start : " + x + ","+ y + "  " + mX + "," + mY);
        mPath_receiver.reset();
        mPath_receiver.moveTo(x, y);
        mX_receiver = x;
        mY_receiver = y;
    }

    /**
     * Continue drawing. Function reused for both Create & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void simulateTouchMove(float x, float y,float mmX,float mmY) {

        float dx = Math.abs(x - mmX);
        float dy = Math.abs(y - mmY);
        //if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {//undo it maybe
        //Make bezier curve through the points
        mPath_receiver.quadTo(mX_receiver, mY_receiver, (x + mX_receiver)/2, (y + mY_receiver)/2);
        mX_receiver = x;
        mY_receiver = y;
        //Log.d(TAG,"Touch_move : " + x + ","+ y + "  " + mX + "," + mY);


        //}
    }

    /**
     * Stop drawing once finger lifted
     */
    private void simulateTouchUp(float mX,float mY) {
        //mPath.lineTo(mX, mY);


        Log.d(TAG, "Touch_end : " + mX + "," + mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath_receiver, mPaint_receiver);
        // kill this so we don't double draw
        mPath_receiver.reset();
    }
}

