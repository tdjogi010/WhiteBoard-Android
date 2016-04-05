package netp.tj.whiteboard;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Created by tj on 4/5/16.
 */
public class SendingThread extends Thread {
    private static final String TAG = "SendingThread";
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    Queue<String> msgs;
    Boolean sending;

    SendingThread(Socket s, Queue<String> msgTo,Boolean send){
        socket=s;
        msgs = msgTo;
        sending=send;
    }

    @Override
    public void run() {
        try {
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            //response = "UnknownHostException: " + e.toString();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            //response = "IOException: " + e.toString();
            return;
        }
        try {
        while (sending) {
                try {
                    if (!msgs.isEmpty()) {
                        String msg = msgs.remove();
                        if (msg != null) {
                            dataOutputStream.writeUTF(msg);
                            Log.d(TAG, msg);
                        }
                        //sending=false;//remove
                    }
                }catch (NoSuchElementException e){
                    //e.printStackTrace();
                    //lite
                }catch (NullPointerException e){
                    //e.printStackTrace();
                    //lite
                }


            }


        } catch (IOException e) {
                e.printStackTrace();
        } finally {
            Log.d(TAG,"Closing everthing");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

     }
    }
}



