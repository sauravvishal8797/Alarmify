package com.example.sauravvishal8797.alarmify;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.print.PrinterId;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.example.sauravvishal8797.alarmify.adapters.repeatAlarmAdapter;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class AlarmDetail extends AppCompatActivity {

    private ImageView setButton;
    private ImageView abortButton;
    private TextView alarmMessage;
    private RelativeLayout repeatView;
    private RelativeLayout labelView;
    private TextView daytext;
    private TextView alarmLabel;
    private SwitchCompat snooze;
    private SwitchCompat deleteAfterButton;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int snoozetime=0;
    private boolean deleteAfterGoesOff = false;
    private ArrayList<String> repeatAlarmDays;
    private String labelText;
    private String alarmTime;
    private Realm realm;
    private String period;
    private RealmController realmController;

    private TimePicker time_picker;
    private Resources system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail);
        time_picker = (TimePicker) findViewById(R.id.timepicker22);
        time_picker.setIs24HourView(false);
        //setButton = (SwitchCompat) findViewById(R.id.set)
        setUI();
        statusBarTransparent();
        setTimePickerTextColor();
    }

    private void timePickerChange(){
        final String am_pm;
        final String[] hours = new String[1];
        final String[] minutes = new String[1];
        //final int time_picker.getCurrentHour() = time_picker.getCurrentHour();
        final int min = time_picker.getCurrentMinute();
        Log.i("hours", String.valueOf(time_picker.getCurrentHour()) + "   " + String.valueOf(min));


        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if(time_picker.getCurrentHour()>=12){
                    if(time_picker.getCurrentHour()-12>0)
                        hours[0] = "0"+String.valueOf(time_picker.getCurrentHour()-12);
                    else
                        hours[0] = time_picker.getCurrentHour().toString();
                    if(time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)
                        minutes[0] = "0"+time_picker.getCurrentMinute();
                    else
                        minutes[0] = time_picker.getCurrentMinute().toString();

                    alarmTime = hours[0]+":"+minutes[0];
                   /* alarmTime = (time_picker.getCurrentHour()-12>0)?(String.valueOf(time_picker.getCurrentHour()-12)):
                            time_picker.getCurrentHour()+":" +
                            ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                                    time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());*/
                    period = "PM";
                } else {
                    alarmTime = time_picker.getCurrentHour().toString() +":" +
                            ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                                    time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());
                    period = "AM";
                }
                //Calendar.getInstance().getTime().getHours();
                int newhour = timePicker.getCurrentHour()-time_picker.getCurrentHour();
                int newmin = timePicker.getCurrentMinute()-time_picker.getCurrentMinute();
                if(newhour>0&&newmin<0){
                    alarmMessage.setText("Alarm set for "+ (24-Math.abs(newhour)) +"hours "+Math.abs(newmin)+"minutes from now");
                }else if(newhour>0&&newmin>0){
                    alarmMessage.setText("Alarm set for "+ (24-Math.abs(newhour)-1) +"hours "+time_picker.getCurrentMinute()+
                            (60-timePicker.getCurrentMinute())+"minutes from now");

                }else if(newhour>0&&newmin==0){
                    alarmMessage.setText("Alarm set for "+ (24-Math.abs(newhour)) +"hours from now");

                }else if(newhour<0&&newmin<0){
                    alarmMessage.setText("Alarm set for "+ Math.abs(newhour) +"hours "+Math.abs(newmin)+"minutes from now");

                }else if(newhour<0&&newmin==0){
                    alarmMessage.setText("Alarm set for "+ Math.abs(newhour) +"hours from now");
                }else if(newhour<0 && newmin>0){
                    alarmMessage.setText("Alarm set for "+ (Math.abs(newhour)-1) +"hours "+time_picker.getCurrentMinute()+
                            (60-timePicker.getCurrentMinute())+"minutes from now");

                }
                Log.i("hours", String.valueOf(timePicker.getCurrentHour()) + "   " + String.valueOf(timePicker.getCurrentMinute()));
            }
        });

    }

    private void setUI(){
        alarmMessage = (TextView) findViewById(R.id.set_alarm_mssg);
        setButton = (ImageView) findViewById(R.id.set_button);
        daytext = (TextView) findViewById(R.id.daytext);
        alarmLabel = (TextView) findViewById(R.id.labeltext);
        timePickerChange();
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
        abortButton = (ImageView) findViewById(R.id.abort_button);
        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        repeatView = (RelativeLayout) findViewById(R.id.repeat_alarm_option);
        repeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        snooze = (SwitchCompat) findViewById(R.id.snooze_button);
        snooze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    showSnoozeDialog();
                }
            }
        });
        deleteAfterButton = (SwitchCompat) findViewById(R.id.delete_after_button);
        deleteAfterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    deleteAfterGoesOff=true;
                }else {
                    deleteAfterGoesOff=false;
                }
            }
        });
        daytext = (TextView) findViewById(R.id.daytext);
        labelView = (RelativeLayout) findViewById(R.id.labelview);
        labelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = getLayoutInflater().inflate(R.layout.edittext, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog_Dark);
                builder.setView(view1);
                final EditText editText = (EditText) view1.findViewById(R.id.labeledit_tex);
                //editText.setHintTextColor();
                final AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        labelText = editText.getText().toString().trim();
                        if(labelText.isEmpty())
                            alarmLabel.setText("No Label");
                        else
                            alarmLabel.setText(labelText);
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
            }
        });
    }

    private void showDialog(){
        final StringBuilder builder2 = new StringBuilder();
        View dialogview = this.getLayoutInflater().from(AlarmDetail.this).inflate(R.layout.repeat_alarm_dialog, null);
        dialogview.setBackgroundColor(getResources().getColor(R.color.customPrimary));
        RecyclerView dayList = (RecyclerView) dialogview.findViewById(R.id.repeat_option_dialog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        dayList.setLayoutManager(linearLayoutManager);
        repeatAlarmAdapter alarmAdapter = new repeatAlarmAdapter(addDays(), getApplicationContext());
        dayList.setAdapter(alarmAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Dark);
        builder.setView(dialogview);
        final AlertDialog dialog = builder.create();
        //dialog.show();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                repeatAlarmDays = repeatAlarmAdapter.repeatDays;
                for(int k=0; k<repeatAlarmDays.size(); k++){
                    builder2.append(repeatAlarmDays.get(k)+",");
                }
                daytext.setText(builder2.toString().substring(0, builder2.toString().lastIndexOf(',')));
            }
        });
        //dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(R.color.customPrimary);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
       // dialog.getWindow().
    }

    private void showSnoozeDialog(){
        final String[] colors = {"Off", "5 minutes", "10 minutes", "15 minutes", "20 minutes", "25 minutes", "30 minutes"};
        final int[] values = {0, 5, 10, 15, 20, 25, 30};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Dark);
        builder.setTitle("Choose Snooze duration");
       // builder.s
        builder.setSingleChoiceItems(colors, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    snoozetime=0;
                }else {
                   // String time = colors[i];
                    //time
                    snoozetime = values[i];
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private ArrayList<String> addDays(){
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thrusday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");
        return days;
    }

    private void setAlarm(){
        creatingNewAlarmObject();
        alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Log.i("lalalala", String.valueOf(now.get(Calendar.HOUR_OF_DAY)) +" "+String.valueOf(now.get(Calendar.MINUTE)));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
        calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
        if(calendar.before(now)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(AlarmDetail.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmDetail.this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //Log.i("lalalala", time_picker.getC)
        finish();
    }

    private void creatingNewAlarmObject(){
        StringBuilder builder = new StringBuilder();
        realmController = RealmController.with(this);
        realm = realmController.getRealm();
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(alarmTime);
        newAlarm.setHour(time_picker.getCurrentHour());
        newAlarm.setMinute(time_picker.getCurrentMinute());
        if(repeatAlarmDays==null)
            newAlarm.setDays("No Repeat");
        else {
            for(int i=0; i<repeatAlarmDays.size(); i++){
                builder.append(repeatAlarmDays.get(i)+" ");
            }
            newAlarm.setDays(builder.toString());
        }
        newAlarm.setActivated(true);
        newAlarm.setSnoozeTime(snoozetime);
        newAlarm.setDeleteAfterGoesOff(deleteAfterGoesOff);
        newAlarm.setLabel(labelText);
        newAlarm.setPeriod(period);
        realmController.addAlarm(newAlarm);
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
        final int color = getResources().getColor(R.color.white);

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
