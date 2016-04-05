package netp.tj.whiteboard;

import android.app.Activity;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tj on 4/5/16.
 */
public class ReceivingThread extends Thread {
    private static final String TAG = "ReceivingThread";
    Socket socket = null;
    DataInputStream dataInputStream = null;
    OnActivityInteractionListerner mlisterner;
    Boolean receiving;
    Activity mActivity;
    String response = "";

    ReceivingThread(Socket s, OnActivityInteractionListerner l, Boolean r, Activity activity){
        socket=s;
        mlisterner=l;
        receiving=r;
        mActivity=activity;
    }
    @Override
    public void run() {
        try {
            //socket = new Socket(dstAddress, dstPort);
            dataInputStream = new DataInputStream(socket.getInputStream());



        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            while (receiving) {
                response = dataInputStream.readUTF();
                //receiving=false;//remove
                Log.d(TAG,response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mlisterner.onReceived(response);
                    }
                });

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            //response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            //response = "IOException: " + e.toString();
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    }

}
