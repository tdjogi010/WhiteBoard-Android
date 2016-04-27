package netp.tj.whiteboard.event;

/**
 * Created by ayush on 19/04/16.
 */
public class SimulateDrawingEvent {

    public static final int SIMULATE_START = 0;
    public static final int SIMULATE_MOVE = 1;
    public static final int SIMULATE_END = 2;

    private int mode;
    private float coord1;
    private float coord2;
    private float coord3;
    private float coord4;
    private float width;
    private int color;

    public SimulateDrawingEvent(int mode, float coord1, float coord2, float coord3, float coord4, float width, int color) {
        this.mode = mode;
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.coord3 = coord3;
        this.coord4 = coord4;
        this.width = width;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getCoord1() {
        return coord1;
    }

    public void setCoord1(float coord1) {
        this.coord1 = coord1;
    }

    public float getCoord2() {
        return coord2;
    }

    public void setCoord2(float coord2) {
        this.coord2 = coord2;
    }

    public float getCoord3() {
        return coord3;
    }

    public void setCoord3(float coord3) {
        this.coord3 = coord3;
    }

    public float getCoord4() {
        return coord4;
    }

    public void setCoord4(float coord4) {
        this.coord4 = coord4;
    }
}
