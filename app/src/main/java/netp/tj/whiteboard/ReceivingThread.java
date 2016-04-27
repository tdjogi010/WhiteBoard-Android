package netp.tj.whiteboard;

import android.app.Activity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import netp.tj.whiteboard.event.SimulateDrawingEvent;

/**
 * Created by tj on 4/5/16.
 */
public class ReceivingThread extends Thread {

    private static final String TAG = "ReceivingThread";
    Socket socket = null;
    DataInputStream dataInputStream = null;
    Activity mActivity;
    String response = "";

    ReceivingThread(Socket s, Activity activity){
        socket=s;
        mActivity=activity;
    }
    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            while (true) {
                response = dataInputStream.readUTF();

                Log.d(TAG,"ReadUTF response :" + response);

                String[] arr= response.split(" ");
                if (arr.length == 6){

                    // Continue drawing event

                    EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_MOVE,
                            Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                            Float.parseFloat(arr[2]),Float.parseFloat(arr[3]), Float.parseFloat(arr[4]), Integer.parseInt(arr[5])));
                }else if (arr.length == 3){

                    //Start or end

                    if (arr[2].equals("s")){
                        //Start
                        EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_START,
                                Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                                0.0f, 0.0f, 0.0f, 0));
                    }else{
                        //End
                        EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_END,
                                Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                                0.0f, 0.0f, 0.0f, 0));
                    }
                } else if(arr.length == 2){
                    // Clear Canvas
                    EventBus.getDefault().post(new SimulateDrawingEvent(-1, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0));
                }

            }
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        } finally {
            Log.d(TAG,"Closing everthing");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
