package com.example.demoservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    Button startRedeliver, startSticky, startNonSticky, bindService, messageService;
    ServiceDemo mService;
    boolean mBound = false;
    EditText limitEt;
    ServiceConnection connection;
    int current = 0, inputLimit;
    TextView printNumber;

    Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSticky = findViewById(R.id.start_sticky);
        startNonSticky = findViewById(R.id.start_not_sticky);
        startRedeliver = findViewById(R.id.start_redeliver);
        bindService = findViewById(R.id.bind_service);
        limitEt = findViewById(R.id.limit);
        printNumber = findViewById(R.id.counter);
        messageService = findViewById(R.id.message_service);


        //bindService(new Intent(MainActivity.this, ServiceDemo.class), MainActivity.this, Context.BIND_AUTO_CREATE);


        startSticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(1);

//                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//                    Log.e("srvc name", service.service.getClassName() );
////                    if ("ServiceDemo".equals(service.service.getClassName())) {
////                        Log.e("test", "onStop: service is running" );
////                    }
//                }

            }
        });


        messageService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(-1);
            }
        });

        startNonSticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(2);
            }
        });


        startRedeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(3);
            }
        });

        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "service binding", Toast.LENGTH_SHORT).show();

                if (mBound) {
                    try {
                        mService.printNumbers(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    void startService(int type) {
        if (limitEt.getText().toString().length() > 0) {
            inputLimit = Integer.parseInt(limitEt.getText().toString());
            if (current == 0) {
                current = inputLimit;
                if (type == -1)
                    startService(new Intent(MainActivity.this, ServiceDemo.class).putExtra("type", 1).putExtra("limit", current).putExtra("messenger", messenger));
                else
                    startService(new Intent(MainActivity.this, ServiceDemo.class).putExtra("type", type).putExtra("limit", current));
            } else {
                if (current != inputLimit) {
                    current = inputLimit;
                    stopService(new Intent(MainActivity.this, ServiceDemo.class));
                    if (type == -1)
                        startService(new Intent(MainActivity.this, ServiceDemo.class).putExtra("type", 1).putExtra("limit", current).putExtra("messenger", messenger));
                    else
                        startService(new Intent(MainActivity.this, ServiceDemo.class).putExtra("type", type).putExtra("limit", current));


                } else {

                }
            }
        }
    }

    @Override
    protected void onStop() {
        Log.e("tst", "onStop: ");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.e("srvc name", service.getClass().getName());
            if ("ServiceDemo".equals(service.service.getClassName())) {
                Log.e("test", "onStop: service is running");
            }
        }
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServiceDemo.LocalBinder binder = (ServiceDemo.LocalBinder) service;
        mService = binder.getService();
        mBound = true;
        Log.e("Test", "connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
        mService = null;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            printNumber.setText(bundle.getString("number"));

        }
    }

}