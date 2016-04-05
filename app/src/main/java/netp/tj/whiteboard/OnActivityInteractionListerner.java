package netp.tj.whiteboard;

/**
 * Created by tj on 4/5/16.
 */
public interface OnActivityInteractionListerner {
    void onSent();
    void onReceived(String response);
}
