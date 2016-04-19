package netp.tj.whiteboard;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import netp.tj.whiteboard.event.SimulateDrawingEvent;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MainActivity";
    Paint mPaint;
    Socket socket;
    int whichsocket;
    ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<>();
    DrawViewListener drawViewListener;
    DrawView drawView;

    /*Spinner spinner_color;
    Spinner spinner_text_size;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            public void OnDrawn(boolean startOrEnd, float x, float y) {

                String msg=x+" "+y;
                if(startOrEnd){
                    msg += " e";
                }else{
                    msg += " s";
                }
                queue.add(msg);
            }
        };
        drawView= new DrawView(this,mPaint,drawViewListener);
        ((FrameLayout) findViewById(R.id.main_ll)).addView(drawView, 0);

        /*spinner_text_size = (Spinner)findViewById(R.id.spinner_text_size);
        spinner_color = (Spinner)findViewById(R.id.spinner_color);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> color_adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_color_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_color.setAdapter(color_adapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> text_size_adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_text_size_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        text_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_text_size.setAdapter(text_size_adapter);

        spinner_color.setOnItemSelectedListener(this);
        spinner_text_size.setOnItemSelectedListener(this);*/

        startReceiving();
        startSending();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       /* Log.d(TAG, "Selected at id: "+ id + " and position : " + position);

        if(parent != null){
            Log.d(TAG, "Spinner not null");
            switch (parent.getId()){
                case R.id.spinner_color:
                    String color = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, "Selected " + color, Toast.LENGTH_SHORT).show();
                    break;

                case R.id.spinner_text_size:
                    String size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, "Selected " + size, Toast.LENGTH_SHORT).show();
                    break;
            }
        }*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void startReceiving(){
        ReceivingThread rthread= new ReceivingThread(socket, MainActivity.this);
        rthread.start();
    }

    void startSending(){
        SendingThread sthread= new SendingThread(socket, queue);
        sthread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void simulateDrawing(SimulateDrawingEvent event){
        if (event.getMode() == SimulateDrawingEvent.SIMULATE_MOVE){
            drawView.simulateDraw(event.getCoord1(), event.getCoord2(), event.getCoord3(), event.getCoord4());
        } else if (event.getMode() == SimulateDrawingEvent.SIMULATE_START){
            drawView.simulateStart(event.getCoord1(), event.getCoord2());
        } else if (event.getMode() == SimulateDrawingEvent.SIMULATE_END){
            drawView.simulateEnd(event.getCoord1(), event.getCoord2());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
