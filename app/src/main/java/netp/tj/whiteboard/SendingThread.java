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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tj on 4/5/16.
 */
public class SendingThread extends Thread {

    private static final String TAG = "SendingThread";
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    ConcurrentLinkedQueue<String> msgs;

    SendingThread(Socket s, ConcurrentLinkedQueue<String> msgTo){
        socket=s;
        msgs = msgTo;
    }

    @Override
    public void run() {
        try {
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());

        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            return;
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            return;
        }
        try {
        while (true) {
                try {
                    if (!msgs.isEmpty()) {
                        String msg = msgs.remove();
                        if (msg != null) {
                            //dataOutputStream.writeUTF(msg);
                            msg=msg+" 1.0\n";
                            dataOutputStream.write(msg.getBytes("US-ASCII"));
                            //Log.d(TAG, "WriteUTF :" + msg);
                            Log.d(TAG, "Write :" + msg);
                        }
                    }
                }catch (NoSuchElementException e){
                    //e.printStackTrace();
                    //lite
                }catch (NullPointerException e) {
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



