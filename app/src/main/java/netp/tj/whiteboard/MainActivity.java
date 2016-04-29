package netp.tj.whiteboard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import netp.tj.whiteboard.event.SimulateDrawingEvent;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MainActivity";
    Paint mPaint;
    Paint mPaint_receiver;
    Socket socket;
    int whichsocket;
    ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<>();
    DrawViewListener drawViewListener;
    DrawView drawView;

    Spinner spinner_color;
    Spinner spinner_text_size;

    Button bt_next;

    int width,height;

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
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mPaint_receiver = new Paint();
        mPaint_receiver.setAntiAlias(true);
        mPaint_receiver.setDither(true);
        mPaint_receiver.setColor(Color.RED);
        mPaint_receiver.setStyle(Paint.Style.STROKE);
        mPaint_receiver.setStrokeJoin(Paint.Join.ROUND);
        mPaint_receiver.setStrokeCap(Paint.Cap.ROUND);
        mPaint_receiver.setStrokeWidth(12);


        drawViewListener= new DrawViewListener() {
            @Override
            public void OnDrawn(float oldx, float oldy, float newx, float newy, float stroke_width, int color) {
                //scale down
                oldx=oldx/width;
                oldy=oldy/height;
                newx=newx/width;
                newy=newy/height;

                String msg=oldx+" "+oldy+" "+newx+" "+newy + " " + stroke_width + " " + color;
                //Log.d(TAG,msg);
                //give it to sending
                queue.add(msg);
            }

            @Override
            public void OnDrawn(boolean startOrEnd, float x, float y) {
                //scale down
                x=x/width;
                y=y/height;
                String msg=x+" "+y;
                if(startOrEnd){
                    msg += " e";
                }else{
                    msg += " s";
                }
                queue.add(msg);
            }
        };
        drawView= new DrawView(this, mPaint, mPaint_receiver, drawViewListener);
        FrameLayout fl= ((FrameLayout) findViewById(R.id.main_ll));
        fl.addView(drawView, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        Log.d(TAG,height+" "+width );


        spinner_text_size = (Spinner)findViewById(R.id.spinner_text_size);
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
        spinner_text_size.setOnItemSelectedListener(this);

        spinner_color.setSelection(0);
        spinner_text_size.setSelection(8);

//        HashMap<String, Integer> colors_map = new HashMap<>();
//        colors_map.put("Red", Color.RED);
//        colors_map.put("Green", Color.GREEN);
//        colors_map.put("Blue" , Color.BLUE);
//        colors_map.put("Yellow", Color.YELLOW);
//        colors_map.put("Magenta", Color.MAGENTA);
//        colors_map.put("Cyan", Color.CYAN);
//        colors_map.put("Gray", Color.GRAY);
//        colors_map.put("Black", Color.BLACK);


        bt_next = (Button) findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

        startReceiving();
        startSending();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Selected at id: "+ id + " and position : " + position);

        if(parent != null){
            Log.d(TAG, "Spinner not null");
            switch (parent.getId()){
                case R.id.spinner_color:
                    String color = (String) parent.getItemAtPosition(position);
                    String colors[] = getResources().getStringArray(R.array.spinner_color_array);

                    for (String c: colors){
                        if(c.toLowerCase().equals(color.toLowerCase())){
                            mPaint.setColor(Color.parseColor(c.toLowerCase()));
                        }
                    }
                    break;

                case R.id.spinner_text_size:
                    String size = (String) parent.getItemAtPosition(position);
                    Toast.makeText(this, "Selected " + size, Toast.LENGTH_SHORT).show();

                    mPaint.setStrokeWidth(Float.parseFloat(size));
                    break;
            }
        }

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
            //scale up
            drawView.simulateDraw(event.getCoord1()*width, event.getCoord2()*height, event.getCoord3()*width, event.getCoord4()*height,event.getWidth(),event.getColor());
            //drawView.simulateDraw(event.getCoord1(), event.getCoord2(), event.getCoord3(), event.getCoord4(), event.getWidth(), event.getColor());
        } else if (event.getMode() == SimulateDrawingEvent.SIMULATE_START){
            drawView.simulateStart(event.getCoord1()*width, event.getCoord2()*height);
            //drawView.simulateStart(event.getCoord1(), event.getCoord2());
        } else if (event.getMode() == SimulateDrawingEvent.SIMULATE_END){
            drawView.simulateEnd(event.getCoord1()*width, event.getCoord2()*height);
            drawView.simulateEnd(event.getCoord1(), event.getCoord2());
        } else if (event.getMode() == -1){
            drawView.nextPage();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                drawView.saveBitmap();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nextPage(){
        drawView.nextPage();

        String msg = "Clear canvas";
        queue.add(msg);
    }


}
