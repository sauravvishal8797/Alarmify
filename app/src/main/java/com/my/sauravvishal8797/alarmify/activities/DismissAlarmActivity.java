package com.my.sauravvishal8797.alarmify.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.my.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.models.MathsExpression;
import com.my.sauravvishal8797.alarmify.realm.RealmController;
import com.my.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.my.sauravvishal8797.alarmify.R;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class DismissAlarmActivity extends AppCompatActivity {

    private boolean isFocus;
    private static boolean isPaused = false;
    // private boolean
    private Handler collapseNotificationHandler;
    private boolean isShutting = false;
    private ActivityManager mActivityManager;
    private Handler mHandler;
    //private interface lifecycledelegate;

    private TextView expText;
    private Button submitButton;
    public static boolean repeat = false;
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
    private TextView dismissButton;
    private TextView snoozeButton;
    private TextView alarmLabelMessage;
    private TextView alarmPeriod;

    private int hour = 0;
    private int minutes = 0;
    private String period;
    private String alamtime;
    private int id;
    private String alarmLabel;
    private int snoozeTime = 0;
    private boolean deleteAfterGpingoff;
    private int noOfTimesSnoozed = 0;

    private PreferenceUtil SP;
    private AudioManager audioManager;
    private MediaPlayer previewMediaPlayer;
    private RelativeLayout previewModeLayout;
    private ImageView previewAbortButton;

    /**
     * Maths Puzzle dismiss view UI elements
     */
    private TextView mathsExpression;
    private TextView onebutton;
    private TextView twoButton;
    private TextView threeButton;
    private TextView fourButton;
    private TextView fiveButton;
    private TextView sixButton;
    private TextView sevenButton;
    private TextView eightButton;
    private TextView nineButton;
    private TextView zeroButton;
    private TextView deleteButton;
    private TextView okButton;
    private EditText answerEditText;
    private TextView questionText;
    private TextView snoozeButtonMathsPuzzle;
    private TextView currentTime;
    private TextView currentPeriod;
    private TextView alarmLabelTextMathsPuzz;

    private StringBuilder answerBuilder = new StringBuilder();
    private int mathsAnswer;
    private HashMap<String, Integer> mathsPuzzle = new HashMap<>();
    private ArrayList<MathsExpression> mathsPuzz;

    private Handler someHandler;
    private Runnable runnable;

    private boolean dismissButtonPress = false;

    private int auto_dismiss = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if ((AlarmReceiver.mediaPlayer == null) && !intent.getBooleanExtra("preview", false)) {
            finish();
        }
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.AppTask> tasks = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tasks = am.getAppTasks();
            }
            if (tasks != null && tasks.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tasks.get(0).setExcludeFromRecents(true);
                }
            }
        }
        SP = PreferenceUtil.getInstance(this);
        if (SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().getString(R.string.default_dismiss_mission))
                .equals(getResources().getString(R.string.maths_mission_dismiss))) {
            setContentView(R.layout.maths_exp_view);
        } else {
            setContentView(R.layout.dismiss_alarm_view);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_FULLSCREEN |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        audioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        if (intent.hasExtra("preview")) {
            previewScreen = intent.getBooleanExtra("preview", false);
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            previewMediaPlayer = MediaPlayer.create(this, alarmUri);
            previewMediaPlayer.setLooping(true);
            if (SP.getBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false)) {
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
        noOfTimesSnoozed = intent.getIntExtra("noftimesSnoozed", 0);
        alarmLabel = intent.getStringExtra("label");
        statusBarTransparent();
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0) > 0) {
            auto_dismiss = SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0);
            ScheduledExecutorService scheduler =
                    Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            if (count < auto_dismiss) {
                                count++;
                            } else {
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
        //setUpUiDefaultDismissView();
        if (SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().getString(R.string.default_dismiss_mission))
                .equals(getResources().getString(R.string.maths_mission_dismiss))) {
            setUpMathsPuzzleView();
            someHandler = new Handler(getMainLooper());
            runnable = new Runnable() {
                @Override
                public void run() {
                    String timenow = " ", periodnow = " ";
                    int hours = 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String datetime = simpleDateFormat.format(new Date());
                    if (Integer.valueOf(datetime.substring(0, 2)) > 12) {
                        hours = Integer.valueOf(datetime.substring(0, 2)) - 12;
                        timenow = (hours >= 10) ? String.valueOf(hours) + ":" + datetime.substring(3,
                                datetime.length()) : "0" + String.valueOf(hours) + ":" + datetime.substring(3, datetime.length());
                        periodnow = "PM";

                    } else {
                        timenow = datetime;
                        periodnow = "AM";
                    }
                    currentTime.setText(timenow);
                    currentPeriod.setText(periodnow);
                    someHandler.postDelayed(this, 1000);
                }
            };
            someHandler.postDelayed(runnable, 10);
        } else {
            setUpUiDefaultDismissView();
            someHandler = new Handler(getMainLooper());
            runnable = new Runnable() {
                @Override
                public void run() {
                    String timenow = " ";
                    int hours = 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String datetime = simpleDateFormat.format(new Date());
                    if (Integer.valueOf(datetime.substring(0, 2)) > 12) {
                        hours = Integer.valueOf(datetime.substring(0, 2)) - 12;
                        timenow = (hours >= 10) ? String.valueOf(hours) + ":" + datetime.substring(3,
                                datetime.length()) + " PM" : "0" + String.valueOf(hours) + ":" + datetime.substring(3, datetime.length()) + " PM";
                    } else {
                        timenow = datetime + " AM";
                    }
                    currentTimeView.setText(timenow);
                    someHandler.postDelayed(this, 1000);
                }
            };
            someHandler.postDelayed(runnable, 10);
        }
        isPaused = false;
    }

    private static void setUpMathsPuzzleView() {
        mathsPuzz = new ArrayList<>();
        final int[] count = {0};
        mathsExpression = findViewById(R.id.expression_view);
        final int nofOfQuestion = SP.getInt(getResources().getString(R.string.dismiss_maths_mission_ques), 3);
        for (int i = 0; i < nofOfQuestion; i++) {
            String[] exp = generateExpression();
            MathsExpression mathsExpression = new MathsExpression();
            mathsExpression.setExpression(exp[0]);
            mathsExpression.setExpAnswer(Integer.parseInt(exp[1]));
            mathsPuzz.add(mathsExpression);
        }
        mathsExpression.setText(mathsPuzz.get(count[0]).getExpression());
        previewModeLayout = findViewById(R.id.preview_mode_textView);
        previewAbortButton = findViewById(R.id.preview_abort_button);
        if (previewScreen) {
            previewModeLayout.setVisibility(View.VISIBLE);
            previewAbortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (previewMediaPlayer.isPlaying()) {
                        previewMediaPlayer.stop();
                        previewMediaPlayer.release();
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString("previewMode", "off");
                        editor.commit();
                        finish();
                    }
                }
            });
        } else {
            previewModeLayout.setVisibility(View.GONE);
        }
        ansEdttxt = findViewById(R.id.exp_edittext);
        onebutton = findViewById(R.id.one);
        onebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("1");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        currentTime = findViewById(R.id.dialog_title);
        currentPeriod = findViewById(R.id.am_pm);
        twoButton = findViewById(R.id.two);
        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("2");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        threeButton = findViewById(R.id.three);
        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("3");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        fourButton = findViewById(R.id.four);
        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("4");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        fiveButton = findViewById(R.id.five);
        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("5");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        sixButton = findViewById(R.id.six);
        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("6");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        sevenButton = findViewById(R.id.seven);
        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("7");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        eightButton = findViewById(R.id.eight);
        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("8");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        nineButton = findViewById(R.id.nine);
        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("9");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        zeroButton = findViewById(R.id.zero);
        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerBuilder.append("0");
                ansEdttxt.setText(answerBuilder.toString());
                ansEdttxt.setSelection(ansEdttxt.getText().length());
            }
        });
        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answerBuilder.toString().isEmpty()) {
                    answerBuilder.delete(0, answerBuilder.toString().length());
                    ansEdttxt.setText(answerBuilder.toString());
                    ansEdttxt.setSelection(ansEdttxt.getText().length());
                }
            }
        });
        okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerBuilder.toString().isEmpty()) {

                } else {
                    mathsAnswer = Integer.parseInt(answerBuilder.toString());
                    ansEdttxt.setText("");
                    answerBuilder.delete(0, answerBuilder.toString().length());
                    if (mathsAnswer == mathsPuzz.get(count[0]).getExpAnswer() && count[0] < nofOfQuestion - 1) {
                        count[0]++;
                        mathsExpression.setText(mathsPuzz.get(count[0]).getExpression());
                    } else if (mathsAnswer == mathsPuzz.get(count[0]).getExpAnswer() && count[0] == nofOfQuestion - 1) {
                        if (AlarmReceiver.mediaPlayer != null && AlarmReceiver.mediaPlayer.isPlaying()) {
                            AlarmReceiver.mediaPlayer.stop();
                            AlarmReceiver.mediaPlayer.release();
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putString("ringing", "not");
                            editor.commit();
                        }
                        finish();
                        if (!previewScreen) {
                            Toast.makeText(view.getContext(), getResources().getString(R.string.dismiss_alarm_message), Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putString("previewMode", "off");
                            editor.commit();
                        }
                    }
                }
            }
        });
        alarmLabelTextMathsPuzz = findViewById(R.id.alarmlabeltextMathsPuzzle);
        alarmLabelTextMathsPuzz.setText(alarmLabel);
        snoozeButtonMathsPuzzle = findViewById(R.id.snoozeButton);
        String snoozeValue = SP.getString(getResources().getString(R.string.set_max_snoozes),
                "Unlimited");
        if (snoozeTime > 0) {
            snoozeButtonMathsPuzzle.setVisibility(View.VISIBLE);
            if (!snoozeValue.equals("Unlimited")) {
                if (noOfTimesSnoozed > Integer.valueOf(snoozeValue)) {
                    snoozeButton.setVisibility(View.GONE);
                } else {
                    snoozeButton.setVisibility(View.VISIBLE);
                }
            }
            snoozeButtonMathsPuzzle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AlarmReceiver.mediaPlayer != null && AlarmReceiver.mediaPlayer.isPlaying()) {
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
        } else {
            snoozeButtonMathsPuzzle.setVisibility(View.GONE);
        }
    }

    public String[] generateExpression() {
        int x = 0, y = 0, z = 0;
        String[] exp = new String[2];
        Random random = new Random();
        if (SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("Easy")) {
            x = random.nextInt(9) + 17;
            y = random.nextInt(9) + 18;
            z = random.nextInt(9) + 8;
        } else if (SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("Medium")) {
            x = random.nextInt(99) + 117;
            y = random.nextInt(999) + 79;
            z = random.nextInt(99) + 139;
        } else if (SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("Hard")) {
            x = random.nextInt(999) + 198;
            y = random.nextInt(999) + 1027;
            z = random.nextInt(999) + 1671;
        }
        int sum = x + y + z;
        exp[0] = String.valueOf(x) + "+" + String.valueOf(y) + "+" + String.valueOf(z) + "=" + "?";
        exp[1] = String.valueOf(sum);
        return exp;
    }

    private void setUpUiDefaultDismissView() {
        previewModeLayout = findViewById(R.id.preview_mode_textView);
        previewAbortButton = findViewById(R.id.preview_abort_button);
        if (previewScreen) {
            previewModeLayout.setVisibility(View.VISIBLE);
            previewAbortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (previewMediaPlayer.isPlaying()) {
                        previewMediaPlayer.stop();
                        previewMediaPlayer.release();
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString("previewMode", "off");
                        editor.commit();
                        finish();
                    }
                }
            });
        }
        currentTimeView = findViewById(R.id.time_current);
        currentTimeView.setText("It's " + Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes());
        currentTimeView.setTextSize(40);
        dismissButton = findViewById(R.id.dismiss_button);
        snoozeButton = findViewById(R.id.snooze_button);
        String snoozeValue = SP.getString(getResources().getString(R.string.set_max_snoozes),
                "Unlimited");
        if (snoozeTime > 0) {
            snoozeButton.setVisibility(View.VISIBLE);
            if (!snoozeValue.equals("Unlimited")) {
                if (noOfTimesSnoozed > Integer.valueOf(snoozeValue)) {
                    snoozeButton.setVisibility(View.GONE);
                } else {
                    snoozeButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            snoozeButton.setVisibility(View.GONE);
        }
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AlarmReceiver.mediaPlayer != null && AlarmReceiver.mediaPlayer.isPlaying()) {
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
                if (previewScreen) {
                    if (previewMediaPlayer.isPlaying()) {
                        previewMediaPlayer.stop();
                        previewMediaPlayer.release();
                        dismissButtonPress = true;
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString("previewMode", "off");
                        editor.commit();
                        finish();
                    }
                } else {
                    if (AlarmReceiver.mediaPlayer != null && AlarmReceiver.mediaPlayer.isPlaying()) {
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

    private void setAlarmAfterSnooze(int snoozeTime) {
        String alarmTime = " ";
        final String[] hours = new String[1];
        final String[] minutesf = new String[1];
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        //minutes = minutes+snoozeTime;
        minutes = now.getTime().getMinutes() + snoozeTime;
        if (minutes > 59) {
            minutes = minutes - 60;
            hour = hour + 1;
        }
        if (hour >= 12) {
            if (hour - 12 > 0)
                hours[0] = "0" + String.valueOf(hour - 12);
            else
                hours[0] = String.valueOf(hour);
            if (hour >= 0 && minutes <= 9)
                minutesf[0] = "0" + minutes;
            else
                minutesf[0] = String.valueOf(minutes);

            alarmTime = hours[0] + ":" + minutesf[0];
                   /* alarmTime = (time_picker.getCurrentHour()-12>0)?(String.valueOf(time_picker.getCurrentHour()-12)):
                            time_picker.getCurrentHour()+":" +
                            ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                                    time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());*/
            period = "PM";
        } else {
            alarmTime = String.valueOf(hour) + ":" +
                    ((minutes >= 0 && minutes <= 9) ? String.valueOf(0) +
                            String.valueOf(minutes) : String.valueOf(minutes));
            period = "AM";
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(DismissAlarmActivity.this, AlarmReceiver.class);
        intent.putExtra("alarmtime", alarmTime);
        intent.putExtra("hour", hour);
        intent.putExtra("minutes", minutes);
        intent.putExtra("deleteAfterGoingOff", true);
        intent.putExtra("period", period);
        intent.putExtra("snooze", snoozeTime);
        intent.putExtra("nooftimesSnoozed", noOfTimesSnoozed + 1);
        intent.putExtra("label", alarmLabel);
        intent.putExtra("repeat", 0);
        final int _id = (int) System.currentTimeMillis();
        intent.putExtra("id", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DismissAlarmActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        creatingNewAlarmObject(_id, alarmTime, 0);
        finish();
    }

    private void creatingNewAlarmObject(int pendingIntentId, String alamtime, int rep) {
        StringBuilder builder = new StringBuilder();
        RealmController realmController = RealmController.with(this);
        Realm realm = realmController.getRealm();
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(alamtime);
        newAlarm.setHour(hour);
        newAlarm.setPendingIntentId(pendingIntentId);
        newAlarm.setMinute(minutes);
        if (rep == 0)
            newAlarm.setDays("No Repeat");
        newAlarm.setActivated(true);
        newAlarm.setSnoozeTime(snoozeTime);
        newAlarm.setNoOfTimesSnoozed(noOfTimesSnoozed + 1);
        newAlarm.setDeleteAfterGoesOff(deleteAfterGpingoff);
        newAlarm.setLabel(alarmLabel);
        newAlarm.setPeriod(period);
        realmController.addAlarm(newAlarm);
    }

    private void statusBarTransparent() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.hideFakeStatusBarView(this);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onUserLeaveHint() {
        //super.onUserLeaveHint();
        isShutting = true;
        //onWindowFocusChanged(false);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ringerMax = false;
        if (SP.getBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false)) {
            ringerMax = true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //  new ResumeActivity().execute();
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && ringerMax) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && ringerMax) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE && ringerMax) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_POWER) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isFocus = hasFocus;
        if (!hasFocus && SP.getBoolean(getResources().getString(R.string.prevent_phone_power_off), false)) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
        if (!hasFocus && !isShutting) {
            collapseNow();
        } else if (!hasFocus && isPaused) {
            //onResume();
        }
    }

    private void collapseNow() {
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
                            collapseStatusBar = statusBarManager.getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager.getMethod("collapse");
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
        isPaused = true;
        isShutting = true;
        repeat = true;
        //new ResumeActivity().execute();
        someHandler.removeCallbacks(runnable);
        //new ResumeActivity().execute();

        //Intent intent = new Intent(DismissAlarmActivity.this, Restart.class);
        //startActivity(intent);

        // Log.i("stopper", "pause");
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
        someHandler.removeCallbacks(runnable);
        //new ResumeActivity().execute();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        repeat = false;
        if (SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().getString(R.string.default_dismiss_mission))
                .equals(getResources().getString(R.string.maths_mission_dismiss))) {
            // setUpMathsPuzzleView();
            someHandler = new Handler(getMainLooper());
            runnable = new Runnable() {
                @Override
                public void run() {
                    String timenow = " ", periodnow = " ";
                    int hours = 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String datetime = simpleDateFormat.format(new Date());
                    if (Integer.valueOf(datetime.substring(0, 2)) > 12) {
                        hours = Integer.valueOf(datetime.substring(0, 2)) - 12;
                        timenow = (hours > 10) ? String.valueOf(hours) + ":" + datetime.substring(3,
                                datetime.length()) : "0" + String.valueOf(hours) + ":" + datetime.substring(3, datetime.length());
                        periodnow = "PM";

                    } else {
                        timenow = datetime;
                        periodnow = "AM";
                    }
                    currentTime.setText(timenow);
                    currentPeriod.setText(periodnow);
                    someHandler.postDelayed(this, 1000);
                }
            };
            someHandler.postDelayed(runnable, 10);
        } else {
            //setUpUiDefaultDismissView();
            someHandler = new Handler(getMainLooper());
            runnable = new Runnable() {
                @Override
                public void run() {
                    String timenow = " ";
                    int hours = 0;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String datetime = simpleDateFormat.format(new Date());
                    if (Integer.valueOf(datetime.substring(0, 2)) > 12) {
                        hours = Integer.valueOf(datetime.substring(0, 2)) - 12;
                        timenow = (hours > 10) ? String.valueOf(hours) + ":" + datetime.substring(3,
                                datetime.length()) + " PM" : "0" + String.valueOf(hours) + ":" + datetime.substring(3, datetime.length()) + " PM";
                    } else {
                        timenow = datetime + " AM";
                    }
                    currentTimeView.setText(timenow);
                    someHandler.postDelayed(this, 1000);
                }
            };
            someHandler.postDelayed(runnable, 10);
        }
    }
}
