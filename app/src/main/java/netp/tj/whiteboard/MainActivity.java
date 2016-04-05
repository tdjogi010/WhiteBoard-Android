package netp.tj.whiteboard;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    Paint mPaint;
    OnActivityInteractionListerner rlistener;
    Boolean receiving,sending;
    Socket socket;
    int whichsocket;
    Queue<String> queue=new LinkedList<String>();
    DrawViewListener drawViewListener;
    DrawView drawView;
    int startorrend=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whichsocket=getIntent().getIntExtra("socket",-1);

        //need a better way
        if (whichsocket==0){
            socket=ConnectActivity.socket;
        }else{
            socket=ClientActivity.socket;
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        drawViewListener= new DrawViewListener() {
            @Override
            public void OnDrawn(float oldx, float oldy, float newx, float newy) {
                String msg=oldx+" "+oldy+" "+newx+" "+newy;
                //Log.d(TAG,msg);
                //give it to sending
                queue.add(msg);
            }

            @Override
            public void OnDrawn(float x, float y) {
                String msg=x+" "+y;
                queue.add(msg);
            }
        };
        drawView= new DrawView(this,mPaint,drawViewListener);

        setContentView(drawView);

        rlistener = new OnActivityInteractionListerner() {
            @Override
            public void onSent() {

            }

            @Override
            public void onReceived(String response) {
                //textResponse.setText(response);
                //parse it and give to drawing
                String[] arr= response.split(" ");
                if(arr.length==4){

                    drawView.simulateDraw(Float.parseFloat(arr[0]),Float.parseFloat(arr[1]),Float.parseFloat(arr[2]),Float.parseFloat(arr[3]));
                }else if (arr.length==2){
                    if (startorrend==0){
                        drawView.simulateStart(Float.parseFloat(arr[0]),Float.parseFloat(arr[1]));
                        startorrend=1;
                    }else{

                        drawView.simulateEnd(Float.parseFloat(arr[0]),Float.parseFloat(arr[1]));
                        startorrend=0;
                    }

                }


            }

        };

        startReceiving();
        startSending();

    }

    void startReceiving(){
        receiving=true;
        ReceivingThread rthread= new ReceivingThread(socket,rlistener,receiving,MainActivity.this);
        rthread.start();
    }

    void startSending(){
        sending=true;
        SendingThread sthread= new SendingThread(socket,queue,sending);
        sthread.start();
    }
}
