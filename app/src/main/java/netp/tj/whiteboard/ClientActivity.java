package netp.tj.whiteboard;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    EditText welcomeMsg;
    static Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextAddress.setText("192.168.43.");
        editTextPort = (EditText) findViewById(R.id.port);
        editTextPort.setText("8080");
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);


        welcomeMsg = (EditText)findViewById(R.id.welcomemsg);
        welcomeMsg.setText("1");


        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });


    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            String tMsg = welcomeMsg.getText().toString();
            if(tMsg.equals("")){
                tMsg = null;
                Toast.makeText(ClientActivity.this, "No Welcome Msg sent", Toast.LENGTH_SHORT).show();
                return;
            }
            //queue.add(tMsg);

            /*MyClientTask myClientTask = new MyClientTask(editTextAddress
                    .getText().toString(), Integer.parseInt(editTextPort
                    .getText().toString()),
                    tMsg);
            myClientTask.execute();*/



            Thread thread = new Thread(){
                @Override
                public void run() {
                    final String error;
                    Log.d(TAG,"herer");
                    try {
                        socket = new Socket(editTextAddress
                                .getText().toString(), Integer.parseInt(editTextPort.getText().toString()));
                        //send welcome msg
                        DataOutputStream  dataOutputStream = new DataOutputStream(
                                socket.getOutputStream());
                        dataOutputStream.write("1\n".getBytes("US-ASCII"));//hardcoded 1...remove this...take from welcomeMsg
                        if (dataOutputStream != null) {
                            //dataOutputStream.close();
                        }
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        byte[] buffer=new byte[1024];
                        Log.d(TAG,"Trying to read");
                        int size=dataInputStream.read(buffer);
                        final String response=new String(buffer,0,size,"UTF-8");
                        Log.d(TAG,response);
                        if (dataInputStream != null) {
                            //dataInputStream.close();
                        }

                        ClientActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ClientActivity.this,response,Toast.LENGTH_SHORT).show();
                                startMainActvity();

                                /*startReceiving();
                                startSending();*/
                            }
                        });
                        return;
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        error="UnknownHostException: " + e.toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                        error="IOException: " + e.toString();

                    }
                    ClientActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textResponse.setText(error);
                        }
                    });
                }
            };
            thread.start();

        }
    };

    void startMainActvity(){
        Intent i = new Intent(ClientActivity.this,MainActivity.class);
        i.putExtra("socket",1);//1-server
        startActivity(i);
    }

/*


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
*/



}
