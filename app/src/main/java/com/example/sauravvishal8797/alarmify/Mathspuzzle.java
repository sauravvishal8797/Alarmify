package com.example.sauravvishal8797.alarmify;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Mathspuzzle extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maths_exp_view);
        Intent intent = getIntent();
        statusBarTransparent();
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
       // setUpUi();
        isPaused=false;
        Toast.makeText(getApplicationContext(), "Hey there buddy", Toast.LENGTH_SHORT).show();
        Log.i("papapa", "lalalopappaa");
    }

    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.hideFakeStatusBarView(this);
    }

    private void setUpUi(){
        final int[] count = {0};
        final int[] ans = {0};
        expText = (TextView) findViewById(R.id.math_exp);
        final String[] result = generateExpression();
        expText.setText(result[0]);
        ans[0] =Integer.valueOf(result[1]);
        ansEdttxt = (EditText) findViewById(R.id.ans_edittext);
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = checkAnswer(ans[0]);
                if(!ansEdttxt.getText().toString().isEmpty() && count[0]<=3){
                    String[] res = generateExpression();
                    if(ansEdttxt.getText().toString().equals(ans[0])){
                        ansEdttxt.getText().clear();
                        //ansEdttxt.setText(res[0]);
                        expText.setText(res[0]);
                        ans[0] = ans[0] + (Integer.valueOf(res[1]) - ans[0]);
                        count[0]++;
                    }
                }else {
                    finish();
                }
            }
        });
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

        //Intent intent = new Intent(Mathspuzzle.this, Restart.class);
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

    String[] getActivePackagesCompat() {
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

    String[] getActivePackages() {
        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }
}
