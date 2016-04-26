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
    String wholeresponse="";

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
                //response = dataInputStream.readUTF();
                //need to change
                byte[] buffer=new byte[1024];
                Log.d(TAG,"Trying to read");
                int size=dataInputStream.read(buffer);
                Log.d(TAG,"read something...converting");
                wholeresponse= new String(buffer,0,size,"UTF-8");
                Log.d(TAG,"Read response :" + wholeresponse);
                String temp[]=wholeresponse.split("\n");
                //Log.d(TAG,"ReadUTF response :" + response);

                for(String t:temp) {
                    response=t;
                    Log.d(TAG,"using response " + response);
                    String[] arr = response.split(" ");
                    if (arr.length >= 4) {
                        EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_MOVE,
                                Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                                Float.parseFloat(arr[2]), Float.parseFloat(arr[3])));
                    } else if (arr.length == 3) {
                        if (arr[2].equals("s")) {
                            EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_START,
                                    Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                                    0.0f, 0.0f));
                        } else {
                            EventBus.getDefault().post(new SimulateDrawingEvent(SimulateDrawingEvent.SIMULATE_END,
                                    Float.parseFloat(arr[0]), Float.parseFloat(arr[1]),
                                    0.0f, 0.0f));
                        }
                    }
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
