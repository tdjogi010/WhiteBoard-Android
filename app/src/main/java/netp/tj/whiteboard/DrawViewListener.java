package netp.tj.whiteboard;

/**
 * Created by tj on 4/5/16.
 */
interface DrawViewListener {
    void OnDrawn(float oldx,float oldy,float newx,float newy, float width, int color);
    void OnDrawn(boolean startOrEnd, float x,float y);
}
