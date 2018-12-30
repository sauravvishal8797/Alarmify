package com.example.sauravvishal8797.alarmify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.Calendar;

public class AlarmDetail extends AppCompatActivity {

    private ImageView setButton;
    private ImageView abortButton;
    private TextView alarmMessage;
    private SwitchCompat snooze;
    private SwitchCompat deleteAfterButton;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private TimePicker time_picker;
    private Resources system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail);
        time_picker = (TimePicker) findViewById(R.id.timepicker22);
        //setButton = (SwitchCompat) findViewById(R.id.set)
        setUI();
        statusBarTransparent();
        setTimePickerTextColor();
    }

    private void setUI(){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setButton = (ImageView) findViewById(R.id.set_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
        abortButton = (ImageView) findViewById(R.id.abort_button);
        snooze = (SwitchCompat) findViewById(R.id.snooze_button);
        deleteAfterButton = (SwitchCompat) findViewById(R.id.delete_after_button);

    }

    private void setAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
        calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
        Intent intent = new Intent(AlarmDetail.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmDetail.this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        //Log.i("lalalala", time_picker.getC)
        finish();

    }

    //method to set the status bar transparent
    private void statusBarTransparent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void setTimePickerTextColor(){
        system = Resources.getSystem();
        int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
        int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
        int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

        NumberPicker hour_numberpicker = (NumberPicker) time_picker.findViewById(hour_numberpicker_id);
        NumberPicker minute_numberpicker = (NumberPicker) time_picker.findViewById(minute_numberpicker_id);
        NumberPicker ampm_numberpicker = (NumberPicker) time_picker.findViewById(ampm_numberpicker_id);

        set_numberpicker_text_colour(hour_numberpicker);
        set_numberpicker_text_colour(minute_numberpicker);
        set_numberpicker_text_colour(ampm_numberpicker);

    }

    private void set_numberpicker_text_colour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.grey_shade);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                number_picker.invalidate();
            }
            catch(NoSuchFieldException e){
                Log.w("setNumberPickerText", e);
            }
            catch(IllegalAccessException e){
                Log.w("setNumberPickerTex", e);
            }
            catch(IllegalArgumentException e){
                Log.w("setNumberPickerText", e);
            }
        }
    }
}
