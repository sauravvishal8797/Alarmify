package com.my.sauravvishal8797.alarmify.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.my.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.my.sauravvishal8797.alarmify.helpers.AlertDialogHelper;
import com.my.sauravvishal8797.alarmify.helpers.NotificationHelper;
import com.my.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.my.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.my.sauravvishal8797.alarmify.R;
import com.my.sauravvishal8797.alarmify.adapters.repeatAlarmAdapter;
import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.realm.RealmController;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;

public class AlarmDetailActivity extends AppCompatActivity {

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

    private PreferenceUtil SP;

    private boolean edit_mode = false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail);
        SP = PreferenceUtil.getInstance(this);
        intent = getIntent();
        if(intent.hasExtra("alarm_edit")){
            edit_mode = intent.getBooleanExtra("alarm_edit", false);
        }
        realmController = RealmController.with(this);
        time_picker = (TimePicker) findViewById(R.id.timepicker22);
        time_picker.setIs24HourView(false);
        setUI();
        statusBarTransparent();
        setTimePickerTextColor();
    }

    private void timePickerChange(){
        final String am_pm;
        final String[] hours = new String[1];
        final String[] minutes = new String[1];
        final int min = time_picker.getCurrentMinute();
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
            period = "PM";
        } else {
            alarmTime = time_picker.getCurrentHour().toString() +":" +
                    ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                            time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());
            period = "AM";
        }
        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Calendar now = Calendar.getInstance();
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
                    period = "PM";
                } else {
                    alarmTime = time_picker.getCurrentHour().toString() +":" +
                            ((time_picker.getCurrentMinute()>=0 && time_picker.getCurrentMinute()<=9)?String.valueOf(0) +
                                    time_picker.getCurrentMinute().toString():time_picker.getCurrentMinute().toString());
                    period = "AM";
                }
                int setmin = time_picker.getCurrentMinute() - now.get(Calendar.MINUTE);
                int hourset = time_picker.getCurrentHour() - now.get(Calendar.HOUR_OF_DAY);
                if (hourset>0){
                    if(setmin>0&&hourset>0){
                        alarmMessage.setText("Set alarm for "+ hourset+" hrs "+setmin+" mins from now");
                    } else if (setmin<0&&hourset>0){
                        alarmMessage.setText("Set alarm for "+ (hourset-1)+" hrs "+(60-Math.abs(setmin))+" mins from now");
                    } else if (setmin==0&&hourset>0){
                        alarmMessage.setText("Set alarm for "+ hourset+" hrs from now");
                    }
                } else if (hourset<0){
                    if(setmin>0){
                        alarmMessage.setText("Set alarm for "+
                                ((24-now.get(Calendar.HOUR_OF_DAY))+time_picker.getCurrentHour())+" hrs "+setmin+" mins from now");
                    } else if (setmin<0){
                        alarmMessage.setText("Set alarm for "+ (((24-now.get(Calendar.HOUR_OF_DAY))+
                                time_picker.getCurrentHour()-1))+" hrs "+(60-Math.abs(setmin))+" mins from now");
                    } else if (setmin==0){
                        alarmMessage.setText("Set alarm for "+ ((24-now.get(Calendar.HOUR_OF_DAY))+
                                time_picker.getCurrentHour())+" hrs from now");
                    }

                } else if (hourset==0){
                    if(setmin>0){
                        alarmMessage.setText("Set alarm for "+setmin+" mins from now");
                    } else if (setmin<0){
                        alarmMessage.setText("Set alarm for "+ 23+" hrs "+(60-Math.abs(setmin))+" mins from now");
                    } else if (setmin == 0){
                        alarmMessage.setText("Set alarm for now");
                    }
                }
            }
        });

    }

    private void setUI(){
        alarmMessage = (TextView) findViewById(R.id.set_alarm_mssg);
        setButton = (ImageView) findViewById(R.id.set_button);
        daytext = (TextView) findViewById(R.id.daytext);
        if(edit_mode){
            daytext.setText(intent.getStringExtra("repeatDays"));
        }
        alarmLabel = (TextView) findViewById(R.id.labeltext);
        if(edit_mode){
            if(intent.getStringExtra("label") != null){
                alarmLabel.setText(intent.getStringExtra("label"));
            }
        }
        timePickerChange();
        if(edit_mode){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time_picker.setHour(intent.getIntExtra("hour", 0));
                time_picker.setMinute(intent.getIntExtra("minute", 0));
            }
        }
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SP.getBoolean(getResources().getString(R.string.set_alarm_confirmation), false)&&!edit_mode){
                    final AlertDialog dialog = AlertDialogHelper.getTextDialog(AlarmDetailActivity.this, getResources().getString
                            (R.string.confirm_alarm_dialog_title), getResources().getString(R.string.confirm_alarm_dislog_mssg));
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_postive_button).
                            toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            setAlarm();
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_negative_mssg).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    dialog.show();
                } else if ((SP.getBoolean(getResources().getString(R.string.set_alarm_confirmation), false)&&edit_mode) ||
                        (!SP.getBoolean(getResources().getString(R.string.set_alarm_confirmation), false)&&edit_mode) ){
                    final AlertDialog dialog = AlertDialogHelper.getTextDialog(AlarmDetailActivity.this, getResources().getString
                            (R.string.edit_alarm_dialog_title), getResources().getString(R.string.edit_alarm_dialog_mssg));
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_postive_button).
                            toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            setAlarm();
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_negative_mssg).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    dialog.show();

                } else if (!SP.getBoolean(getResources().getString(R.string.set_alarm_confirmation), false)&&!edit_mode){
                    setAlarm();
                }
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
        if(edit_mode){
            snooze.setChecked(intent.getBooleanExtra("snooze", false));
        }
        snooze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    showSnoozeDialog();
                } else {
                    if (snoozetime > 0){
                        snoozetime = 0;
                    }
                }
            }
        });
        deleteAfterButton = (SwitchCompat) findViewById(R.id.delete_after_button);
        if(edit_mode){
            deleteAfterButton.setChecked(intent.getBooleanExtra("delete_after_going_off", false));
        }
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
                if(edit_mode){
                    if(labelText==null){
                        if(intent.getStringExtra("label").equals("No Label")){

                        } else {
                            editText.setText(intent.getStringExtra("label"));
                            editText.setSelection(editText.getText().length());
                        }
                    } else {
                        editText.setText(labelText);
                        editText.setSelection(editText.getText().length());
                    }
                } else {
                    editText.setText(labelText);
                    editText.setSelection(editText.getText().length());
                }
            }
        });
    }

    private void showDialog(){
        final StringBuilder builder2 = new StringBuilder();
        View dialogview = this.getLayoutInflater().from(AlarmDetailActivity.this).inflate(R.layout.repeat_alarm_dialog, null);
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
                repeatAlarmDays = arrangeRepeatDays(repeatAlarmAdapter.repeatDays);
               // repeatAlarmAdapter.repeatDays.clear();
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

    private ArrayList<String> arrangeRepeatDays(ArrayList<String> list){
        ArrayList<String> list1 = new ArrayList<>();
        int daysNum[] = new int[list.size()];
        for (int i = 0; i < list.size(); i++){
            daysNum[i] = mapDaysToNumber(list.get(i));
        }
        Arrays.sort(daysNum);
        for (int i = 0; i < daysNum.length; i++)
            list1.add(mapDaysToNum(daysNum[i]));
        return list1;
    }

    private int mapDaysToNumber(String day){
        int value = 0;
        HashMap<String, Integer> days = new HashMap<>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thr", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        if(days.containsKey(day)){
            value = days.get(day);
        }
        return value;
    }

    private String mapDaysToNum(int day){
        String daysStr = "";
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "Sun");
        map.put(2, "Mon");
        map.put(3, "Tue");
        map.put(4, "Wed");
        map.put(5, "Thr");
        map.put(6, "Fri");
        map.put(7, "Sat");
        if (map.containsKey(day))
            daysStr = map.get(day);
        return daysStr;
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
                snooze.setChecked(true);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                snooze.setChecked(false);
                snoozetime=0;
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

    /** Sets an alarm using the AlarmManager class in Android */
    private void setAlarm(){
        String manufacturer = "xiaomi";
        if(!edit_mode){
            boolean[] exists = checkIfAlreadyExists();
            if (exists[0]&&exists[1]) {
                Toast.makeText(this, "This Alarm already exists", Toast.LENGTH_SHORT).show();
            } else if (exists[0]&&!exists[1]) {
                alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                if((now.get(Calendar.HOUR_OF_DAY) == time_picker.getCurrentHour())&&
                        (now.get(Calendar.MINUTE) == time_picker.getCurrentMinute())){
                    calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);
                }
                if(calendar.before(now)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
                intent.putExtra("alarmtime", alarmTime);
                intent.putExtra("hour", time_picker.getCurrentHour());
                intent.putExtra("minutes", time_picker.getCurrentMinute());
                intent.putExtra("deleteAfterGoingOff", deleteAfterGoesOff);
                intent.putExtra("period", period);
                intent.putExtra("snooze", snoozetime);
                intent.putExtra("nooftimesSnoozed", 0);
                intent.putExtra("label", labelText);
                intent.putStringArrayListExtra("repeatList", repeatAlarmDays);
                int size = (repeatAlarmDays!=null)?repeatAlarmDays.size():0;
                intent.putExtra("repeat", size);
                final int _id = (int) System.currentTimeMillis();
                intent.putExtra("id", _id);
                pendingIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(repeatAlarmAdapter.repeatDays.size()==7){
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
                realmController.reActivateAlarm(alarmTime);
                int setmin = time_picker.getCurrentMinute() - now.get(Calendar.MINUTE);
                int hourset = time_picker.getCurrentHour() - now.get(Calendar.HOUR_OF_DAY);
                if (hourset>0){
                    if(setmin>0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ (hourset-1)+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin==0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours from now",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (hourset<0){
                    if(setmin>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+
                                        ((24-now.get(Calendar.HOUR_OF_DAY))+time_picker.getCurrentHour())+" hours "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ (((24-now.get(Calendar.HOUR_OF_DAY))+
                                        time_picker.getCurrentHour()-1))+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin==0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ ((24-now.get(Calendar.HOUR_OF_DAY))+
                                        time_picker.getCurrentHour())+" hours from now",
                                Toast.LENGTH_SHORT).show();
                    }

                } else if (hourset==0){
                    if(setmin>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ 23+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin == 0){
                        Toast.makeText(getApplicationContext(), "New alarm set for now",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (!exists[0]) {
                alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                if((now.get(Calendar.HOUR_OF_DAY) == time_picker.getCurrentHour())&&
                        (now.get(Calendar.MINUTE) == time_picker.getCurrentMinute())){
                    calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);
                }
                if(calendar.before(now)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                long time = calendar.getTime().getTime();
                Log.i("lioo", String.valueOf(time)+"    "+String.valueOf(calendar.getTimeInMillis()));
                Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
                intent.putExtra("alarmtime", alarmTime);
                intent.putExtra("hour", time_picker.getCurrentHour());
                intent.putExtra("minutes", time_picker.getCurrentMinute());
                intent.putExtra("deleteAfterGoingOff", deleteAfterGoesOff);
                intent.putExtra("period", period);
                intent.putExtra("snooze", snoozetime);
                intent.putExtra("nooftimesSnoozed", 0);
                intent.putExtra("label", labelText);
                intent.putStringArrayListExtra("repeatList", repeatAlarmDays);
                int size = (repeatAlarmDays!=null)?repeatAlarmDays.size():0;
                intent.putExtra("repeat", size);
                final int _id = (int) System.currentTimeMillis();
                intent.putExtra("id", _id);
                Log.i("timetime", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+"   "+calendar.get(Calendar.MINUTE));
                Log.i("timetimetime", String.valueOf(_id) +"    "+String.valueOf(calendar.getTimeInMillis()));
                pendingIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(repeatAlarmAdapter.repeatDays.size()==7){
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
                creatingNewAlarmObject(_id, calendar.getTimeInMillis());
                int setmin = time_picker.getCurrentMinute() - now.get(Calendar.MINUTE);
                int hourset = time_picker.getCurrentHour() - now.get(Calendar.HOUR_OF_DAY);
                if (hourset>0){
                    if(setmin>0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ (hourset-1)+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin==0&&hourset>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours from now",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (hourset<0){
                    if(setmin>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+
                                        ((24-now.get(Calendar.HOUR_OF_DAY))+time_picker.getCurrentHour())+" hours "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ (((24-now.get(Calendar.HOUR_OF_DAY))+
                                        time_picker.getCurrentHour()-1))+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin==0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ ((24-now.get(Calendar.HOUR_OF_DAY))+
                                        time_picker.getCurrentHour())+" hours from now",
                                Toast.LENGTH_SHORT).show();
                    }

                } else if (hourset==0){
                    if(setmin>0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+setmin+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin<0){
                        Toast.makeText(getApplicationContext(), "New alarm set for "+ 23+" hours "+(60-Math.abs(setmin))+" minutes from now",
                                Toast.LENGTH_SHORT).show();
                    } else if (setmin == 0){
                        Toast.makeText(getApplicationContext(), "New alarm set for now",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            realmController.deleteAlarm(intent.getStringExtra("time"), intent.getStringExtra("period"));
            alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            //calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
            if((now.get(Calendar.HOUR_OF_DAY) == time_picker.getCurrentHour())&&
                    (now.get(Calendar.MINUTE) == time_picker.getCurrentMinute())){
                calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute()-1);
                calendar.set(Calendar.SECOND, 0);
            }
            if(calendar.before(now)){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            long time = calendar.getTime().getTime();
            Log.i("lioo", String.valueOf(time)+"    "+String.valueOf(calendar.getTimeInMillis()));
            Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
            intent.putExtra("alarmtime", alarmTime);
            intent.putExtra("hour", time_picker.getCurrentHour());
            intent.putExtra("minutes", time_picker.getCurrentMinute());
            intent.putExtra("deleteAfterGoingOff", deleteAfterGoesOff);
            intent.putExtra("period", period);
            intent.putExtra("snooze", snoozetime);
            intent.putExtra("nooftimesSnoozed", 0);
            intent.putExtra("label", labelText);
            intent.putStringArrayListExtra("repeatList", repeatAlarmDays);
            int size = (repeatAlarmDays!=null)?repeatAlarmDays.size():0;
            intent.putExtra("repeat", size);
            final int _id = (int) System.currentTimeMillis();
            intent.putExtra("id", _id);
            pendingIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if(repeatAlarmAdapter.repeatDays.size()==7){
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                    //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            creatingNewAlarmObject(_id, calendar.getTimeInMillis());
            int setmin = time_picker.getCurrentMinute() - now.get(Calendar.MINUTE);
            int hourset = time_picker.getCurrentHour() - now.get(Calendar.HOUR_OF_DAY);
            if (hourset>0){
                if(setmin>0&&hourset>0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours"+setmin+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin<0&&hourset>0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ (hourset-1)+" hours"+(60-Math.abs(setmin))+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin==0&&hourset>0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ hourset+" hours from now",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (hourset<0){
                if(setmin>0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+
                                    ((24-now.get(Calendar.HOUR_OF_DAY))+time_picker.getCurrentHour())+" hours"+setmin+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin<0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ (((24-now.get(Calendar.HOUR_OF_DAY))+
                                    time_picker.getCurrentHour()-1))+" hours"+(60-Math.abs(setmin))+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin==0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ ((24-now.get(Calendar.HOUR_OF_DAY))+
                                    time_picker.getCurrentHour())+" hours from now",
                            Toast.LENGTH_SHORT).show();
                }

            } else if (hourset==0){
                if(setmin>0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+setmin+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin<0){
                    Toast.makeText(getApplicationContext(), "New alarm set for "+ 23+" hours "+(60-Math.abs(setmin))+" minutes from now",
                            Toast.LENGTH_SHORT).show();
                } else if (setmin == 0){
                    Toast.makeText(getApplicationContext(), "New alarm set for now",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        createNotificationForNextAlarm();
        finish();
    }

    /**
     * Pushes a notification displaying info about the next alarm
     */
    private void createNotificationForNextAlarm(){
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.createNotificationChannel();
        Alarm nextAlarm = realmController.getNextAlarm();
        notificationHelper.sendNotification("[Next Alarm] " + "Tue " + nextAlarm.getTime() + nextAlarm.getPeriod(),
                "Tap to disable the alarm");
    }

    /** Checks if an alarm already exists, can be in either state active or inactive
     * @return returns a boolean array, first element determines whether or not the alarm object exists in the database
     * second boolean element gives the state of the alarm object if it exists i.e active or inactive */
    private boolean[] checkIfAlreadyExists(){
        return realmController.checkIfAlarmExists(alarmTime, period);
    }

    /** Creates a new alarm object for storing in the local database */
    private void creatingNewAlarmObject(int pendingIntentId, long timeInMillis){
        Log.i("llppppppp", String.valueOf(timeInMillis));
        StringBuilder builder = new StringBuilder();
        realm = realmController.getRealm();
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(alarmTime);
        newAlarm.setHour(time_picker.getCurrentHour());
        newAlarm.setPendingIntentId(pendingIntentId);
        newAlarm.setMinute(time_picker.getCurrentMinute());
        newAlarm.setTimeInMillis(timeInMillis);
        if(repeatAlarmDays==null)
            newAlarm.setDays("No Repeat");
        else {
            for(int i=0; i<repeatAlarmDays.size(); i++){
                builder.append(repeatAlarmDays.get(i)+" ");
            }
            newAlarm.setDays(builder.toString());
            String[] str = builder.toString().split(" ");
        }
        newAlarm.setActivated(true);
        newAlarm.setSnoozeTime(snoozetime);
        newAlarm.setNoOfTimesSnoozed(0);
        newAlarm.setDeleteAfterGoesOff(deleteAfterGoesOff);
        if(edit_mode && labelText==null){
            newAlarm.setLabel(intent.getStringExtra("label"));
        } else {
            if(labelText==null){
                newAlarm.setLabel("No Label");
            } else {
                newAlarm.setLabel(labelText);
            }
        }
        newAlarm.setPeriod(period);
        realmController.addAlarm(newAlarm);
    }

    /** Sets the status bar transparent */
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
