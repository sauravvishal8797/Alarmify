package com.example.sauravvishal8797.alarmify.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sauravvishal8797.alarmify.adapters.repeatAlarmAdapter;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;
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

import io.realm.Realm;

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

    private boolean previewScreen = false;

    /**
     * UI elements for the default dismiss view
     */
    private TextView currentTimeView;
    private Button dismissButton;
    private Button snoozeButton;
    private TextView alarmLabelMessage;
    private TextView alarmPeriod;

    private int hour=0;
    private int minutes=0;
    private String period;
    private String alamtime;
    private int id;
    private String alarmLabel;
    private int snoozeTime=0;
    private boolean deleteAfterGpingoff;

    private PreferenceUtil SP;
    private AudioManager audioManager;
    private MediaPlayer previewMediaPlayer;
    private RelativeLayout previewModeLayout;
    private ImageView previewAbortButton;

    private int auto_dismiss=0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dismiss_alarm_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        audioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        SP = PreferenceUtil.getInstance(this);
        Intent intent = getIntent();
        if(intent.hasExtra("preview")){
           previewScreen = intent.getBooleanExtra("preview", false);
           Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
           if (alarmUri == null) {
               alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           }
           previewMediaPlayer = MediaPlayer.create(this, alarmUri);
           previewMediaPlayer.setLooping(true);
           if(SP.getBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false)){
               audioManager.setStreamVolume(
                       AudioManager.STREAM_MUSIC,
                       audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                       0);
           }
           previewMediaPlayer.start();
        }
        hour = intent.getIntExtra("hour", 0);
        minutes = intent.getIntExtra("minutes", 0);
        period = intent.getStringExtra("period");
        alamtime = intent.getStringExtra("time");
        typeDismiss = intent.getStringExtra("stop");
        id = intent.getIntExtra("id", 0);
        deleteAfterGpingoff = intent.getBooleanExtra("deleteAfterGoingOff", false);
        snoozeTime = intent.getIntExtra("snooze", 0);
        Log.i("angmassssssssss", String.valueOf(snoozeTime));
        alarmLabel = intent.getStringExtra("label");
        statusBarTransparent();
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if(SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0) > 0){
            auto_dismiss = SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0);
           /** Handler handler = new Handler();
            Runnable runnable = new Runnable() {
               @Override
               public void run() {
                   if (count < auto_dismiss){
                       count++;
                       Log.i("cunttttt", String.valueOf(count));
                   } else {
                       Log.i("cuntt", "kaka");
                       if(AlarmReceiver.mediaPlayer!=null && AlarmReceiver.mediaPlayer.isPlaying()){
                           AlarmReceiver.mediaPlayer.stop();
                           AlarmReceiver.mediaPlayer.release();
                           SharedPreferences.Editor editor = SP.getEditor();
                           editor.putString("ringing", "not");
                           editor.commit();
                           finish();
                       }
                   }
               }
           };
           handler.postDelayed(runnable, 60000);*/
            ScheduledExecutorService scheduler =
                    Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            if (count < auto_dismiss) {
                                count++;
                                Log.i("cunttttt", String.valueOf(count));
                            } else {
                                Log.i("cuntt", "kaka");
                                if (AlarmReceiver.mediaPlayer != null && AlarmReceiver.mediaPlayer.isPlaying()) {
                                    AlarmReceiver.mediaPlayer.stop();
                                    AlarmReceiver.mediaPlayer.release();
                                    SharedPreferences.Editor editor = SP.getEditor();
                                    editor.putString("ringing", "not");
                                    editor.commit();
                                    finish();
                                }
                                // call service
                            }
                        }
                    }, 0, 60, TimeUnit.SECONDS);
        }
       // setUpUi();
        setUpUiDefaultDismissView();
        isPaused=false;
        Toast.makeText(getApplicationContext(), "Hey there buddy", Toast.LENGTH_SHORT).show();
        Log.i("papapa", "lalalopappaa");
    }

    private void setUpUiDefaultDismissView(){
        previewModeLayout = findViewById(R.id.preview_mode_textView);
        previewAbortButton = findViewById(R.id.preview_abort_button);
        if(previewScreen){
            previewModeLayout.setVisibility(View.VISIBLE);
            previewAbortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(previewMediaPlayer.isPlaying()){
                        previewMediaPlayer.stop();
                        previewMediaPlayer.release();
                        finish();
                    }
                }
            });
        }
        currentTimeView = findViewById(R.id.time_current);
        currentTimeView.setText("It's " +Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes());
        currentTimeView.setTextSize(40);
        dismissButton = findViewById(R.id.dismiss_button);
        snoozeButton = findViewById(R.id.snooze_button);
        if(snoozeTime>0){
            snoozeButton.setVisibility(View.VISIBLE);
        } else {
            snoozeButton.setVisibility(View.GONE);
        }
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AlarmReceiver.mediaPlayer!=null && AlarmReceiver.mediaPlayer.isPlaying()){
                    AlarmReceiver.mediaPlayer.stop();
                    AlarmReceiver.mediaPlayer.release();
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putString("ringing", "not");
                    editor.commit();
                    setAlarmAfterSnooze(snoozeTime);
                }
                finish();
            }
        });
        alarmLabelMessage = findViewById(R.id.alarm_label_message);
        alarmLabelMessage.setText(alarmLabel);
        alarmPeriod = findViewById(R.id.period);
        alarmPeriod.setText(period);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(previewScreen){
                    if(previewMediaPlayer.isPlaying()){
                        previewMediaPlayer.stop();
                        previewMediaPlayer.release();
                        finish();
                    }
                } else {
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
            }
        });
    }

    private void setAlarmAfterSnooze(int snoozeTime){
        String alarmTime = " ";
        final String[] hours = new String[1];
        final String[] minutesf = new String[1];
       AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Log.i("latinnnnn", String.valueOf(snoozeTime));
        Calendar calendar = Calendar.getInstance();
        Log.i("minutesssssssssss", String.valueOf(minutes));
        minutes = minutes+snoozeTime;
        Log.i("minutesssss",String.valueOf(minutes));
        if(minutes>59){
            minutes = minutes - 60;
        }
        if(hour>=12){
            if(hour-12>0)
                hours[0] = "0"+String.valueOf(hour-12);
            else
                hours[0] = String.valueOf(hour);
            if(hour>=0 && minutes<=9)
                minutesf[0] = "0"+minutes;
            else
                minutesf[0] = String.valueOf(minutes);

            alarmTime = hours[0]+":"+minutesf[0];
                   /* alarmTime = (time_picker.getCurrentHour()-12>0)?(String.valueOf(time_picker.getCurrentHour()-12)):
                            time_picker.getCurrentHour()+":" +
                            ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                                    time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());*/
            period = "PM";
        } else {
            alarmTime = String.valueOf(hour) +":" +
                    ((minutes>=0 && minutes<=9)?String.valueOf(0) +
                            String.valueOf(minutes):String.valueOf(minutes));
            period = "AM";
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        if(calendar.before(now)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(DismissAlarmActivity.this, AlarmReceiver.class);
        intent.putExtra("alarmtime", alarmTime);
        intent.putExtra("hour", hour);
        Log.i("lalammmmmmmm", String.valueOf(hour));
        intent.putExtra("minutes", minutes);
        intent.putExtra("deleteAfterGoingOff", true);
        intent.putExtra("period", period);
        intent.putExtra("snooze", snoozeTime);
        intent.putExtra("label", alarmLabel);
        intent.putExtra("repeat", 0);
        final int _id = (int) System.currentTimeMillis();
        intent.putExtra("id", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DismissAlarmActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Log.i("fafafafafa", String.valueOf(time_picker.getCurrentHour())+String.valueOf(time_picker.getCurrentMinute()));
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.i("ldldldld", String.valueOf(alarmTime));
        creatingNewAlarmObject(_id, alarmTime, 0);
        finish();
    }

    private void creatingNewAlarmObject(int pendingIntentId, String alamtime, int rep){
        StringBuilder builder = new StringBuilder();
        RealmController realmController = RealmController.with(this);
        Realm realm = realmController.getRealm();
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(alamtime);
        newAlarm.setHour(hour);
        //Log.i("mmmmmmm", String.valueOf(time_picker.getCurrentHour()));
        newAlarm.setPendingIntentId(pendingIntentId);
        newAlarm.setMinute(minutes);
        if(rep == 0)
            newAlarm.setDays("No Repeat");
        newAlarm.setActivated(true);
        newAlarm.setSnoozeTime(snoozeTime);
        newAlarm.setDeleteAfterGoesOff(deleteAfterGpingoff);
        newAlarm.setLabel(alarmLabel);
        newAlarm.setPeriod(period);
        realmController.addAlarm(newAlarm);
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
    protected void onUserLeaveHint() {
        //super.onUserLeaveHint();
        isShutting=true;
        //onWindowFocusChanged(false);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ringerMax = false;
        if(SP.getBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false)){
            ringerMax = true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //  new ResumeActivity().execute();
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && ringerMax){
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && ringerMax){
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE && ringerMax){
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_POWER) {
            return false;
        } else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isFocus=hasFocus;
        if(!hasFocus && SP.getBoolean(getResources().getString(R.string.prevent_phone_power_off), false)){
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
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
