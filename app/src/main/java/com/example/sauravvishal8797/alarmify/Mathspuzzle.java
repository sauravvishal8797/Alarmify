package com.example.sauravvishal8797.alarmify;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Mathspuzzle extends AppCompatActivity {

    private boolean isFocus;
    private boolean isPaused=false;
    private Handler collapseNotificationHandler;
    private Button button;
    private boolean isShutting=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathspuzzle);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration configuration) {

            }

            @Override
            public void onLowMemory() {

            }
        });

            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        button = (Button) findViewById(R.id.burron);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        Toast.makeText(getApplicationContext(), "Hey there", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
          //  new ResumeActivity().execute();
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        //super.onUserLeaveHint();
        isShutting=true;
        //onWindowFocusChanged(false);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isFocus=hasFocus;
        if(!hasFocus && !isShutting){
            collapseNow();
        }else if(!hasFocus && isPaused){
            //onResume();
            Log.i("llaallaa", "focu");
        }
    }

    private void collapseNow(){
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!isFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;
                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`

                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!isFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }

                }
            }, 300L);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onResume();
        isPaused=true;
        isShutting=true;

        //Intent intent = new Intent(Mathspuzzle.this, Restart.class);
        //startActivity(intent);

        new ResumeActivity().execute();
        Log.i("stopper", "pause");
    }



    @Override
    protected void onStop() {
        //
        super.onStop();
       // new ResumeActivity().execute();
       // new ResumeActivity().execute();
       // onPostResume();
        //onRestart();
        //isPaused=false;
        Log.i("stop", "onStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("stopping", "onDestroy");
       // new ResumeActivity().execute();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isPaused=false;
    }

    class ResumeActivity extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(isPaused){
                Intent intent = new Intent(getApplicationContext(), Alarmservice.class);
                intent.putExtra("Data", "martg");
                startService(intent);
            }
            return null;
        }
    }
}
