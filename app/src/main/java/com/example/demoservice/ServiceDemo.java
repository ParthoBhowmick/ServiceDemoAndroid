package com.example.demoservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ServiceDemo extends Service {

    private final IBinder binder = new LocalBinder();
    Thread thread;
    Handler handler = new Handler();
    Messenger data;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent!=null) {

            if(intent.getParcelableExtra("messenger")!=null)
                data = intent.getParcelableExtra("messenger");

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i < intent.getIntExtra("limit", 0); i++) {
                        Log.d("print numbers", i + "");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e("test", e.toString() );
                            break;
                        }



                        if(data!=null) {
                            Message msg = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString("number", i + "");
                            msg.setData(bundle);
                            try {
                                data.send(msg);
                            } catch (RemoteException e) {
                                Log.e("exception", e.toString());
                            }
                        }
                    }
                }
            });

            thread.start();
        }
            return intent.getIntExtra("type", 0);

    }

    public void printNumbers(int n) throws InterruptedException {
        for(int i=1;i<n;i++) {
            System.out.println(i);
            Thread.sleep(1000);
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!!!", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        ServiceDemo getService() {
            return ServiceDemo.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        Log.e("test", "service stopped");
    }
}