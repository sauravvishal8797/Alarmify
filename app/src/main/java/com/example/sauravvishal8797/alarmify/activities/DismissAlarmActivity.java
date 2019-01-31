package com.example.sauravvishal8797.alarmify.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.example.sauravvishal8797.alarmify.services.Alarmservice;
import com.example.sauravvishal8797.alarmify.R;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DismissAlarmActivity extends AppCompatActivity {

    private boolean isFocus;
    private static boolean isPaused=false;
   // private boolean
    private Handler collapseNotificationHandler;
    private boolean isShutting=false;
    private ActivityManager mActivityManager;
    private Handler mHandler;
    //private interface lifecycledelegate;

    private TextView expText;
    private Button submitButton;
    public static boolean repeat=false;
    private EditText ansEdttxt;
    private String typeDismiss;

    private LinearLayout linearLayout;
    private RelativeLayout dismiss_layout;
    private Button dismiss_button;
    private TextView dismiss_time;
    private TextView dismiss_message;

    /**
     * UI elements for the default dismiss view
     */
    private TextView currentTimeView;
    private Button dismissButton;
    private TextView alarmLabelMessage;
    private TextView alarmPeriod;

    private int hour;
    private int minutes;
    private String period;
    private String alamtime;
    private int id;
    private String alarmLabel;

    private PreferenceUtil SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dismiss_alarm_view);
        Intent intent = getIntent();
        SP = PreferenceUtil.getInstance(this);
        hour = intent.getIntExtra("hour", 0);
        minutes = intent.getIntExtra("minutes", 0);
        period = intent.getStringExtra("period");
        alamtime = intent.getStringExtra("alarmtime");
        typeDismiss = intent.getStringExtra("stop");
        id = intent.getIntExtra("id", 0);
        alarmLabel = intent.getStringExtra("label");
        statusBarTransparent();
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
       // setUpUi();
        setUpUiDefaultDismissView();
        isPaused=false;
        Toast.makeText(getApplicationContext(), "Hey there buddy", Toast.LENGTH_SHORT).show();
        Log.i("papapa", "lalalopappaa");
    }

    private void setUpUiDefaultDismissView(){
        currentTimeView = findViewById(R.id.time_current);
        currentTimeView.setText(alamtime);
        dismissButton = findViewById(R.id.dismiss_button);
        alarmLabelMessage = findViewById(R.id.alarm_label_message);
        alarmLabelMessage.setText(alarmLabel);
        alarmPeriod = findViewById(R.id.period);
        alarmPeriod.setText(period);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AlarmReceiver.mediaPlayer!=null && AlarmReceiver.mediaPlayer.isPlaying()){
                    AlarmReceiver.mediaPlayer.stop();
                    AlarmReceiver.mediaPlayer.release();
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putString("ringing", "not");
                    editor.commit();
                }
                finish();
                Toast.makeText(view.getContext(), getResources().getString(R.string.dismiss_alarm_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.hideFakeStatusBarView(this);
    }

    private void setUpUi(){
        linearLayout = findViewById(R.id.calculator_layout);
        if(typeDismiss.equals("normal")){
            //dismiss_layout = findViewById(R.id.layout_dismiss);
            linearLayout.setVisibility(View.GONE);
            dismiss_layout.setVisibility(View.VISIBLE);
            dismiss_button = findViewById(R.id.dismiss_button);
            dismiss_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            dismiss_time = findViewById(R.id.time_current);
            dismiss_message = findViewById(R.id.alarm_label_message);
        } else {
            expText = (TextView) findViewById(R.id.math_exp);
            final String[] result = generateExpression();
            expText.setText(result[0]);
            ansEdttxt = (EditText) findViewById(R.id.ans_edittext);
        }
    }

    public int checkAnswer(int a){

        return a;
    }

    public String[] generateExpression(){
        String[] exp = new String[2];
        Random random = new Random();
        int x = random.nextInt(9) + 1;
        int y = random.nextInt(9)+ 1;
        int z = random.nextInt(9) + 1;
        int sum = x+y+z;
        exp[0]=String.valueOf(x) + "+" + String.valueOf(y) + "+" + String.valueOf(z) + "=" + "?";
        exp[1]=String.valueOf(sum);
        return exp;
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
        repeat=true;

        //Intent intent = new Intent(DismissAlarmActivity.this, Restart.class);
        //startActivity(intent);

       // new ResumeActivity().execute();

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
       // new ResumeActivity().execute();
       // repeat=true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("stopping", "onDestroy");
       // new ResumeActivity().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused=false;
        repeat=false;
        Log.i("lalalalal", "lolo");
        Log.i("papapapap", "lalalalallll");
    }

    class ResumeActivity extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            ScheduledExecutorService scheduler =
                    Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            if(isPaused&&repeat){
                                Log.i("papapapapapa", "lalalalala");
                                Intent intent = new Intent(getApplicationContext(), Alarmservice.class);
                                intent.putExtra("Data", "kio");
                                startService(intent);
                            }
                            // call service
                        }
                    }, 0, 2, TimeUnit.SECONDS);
            return null;
        }
    }
}
