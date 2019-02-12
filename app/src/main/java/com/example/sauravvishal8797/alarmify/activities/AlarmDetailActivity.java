package com.example.sauravvishal8797.alarmify.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.example.sauravvishal8797.alarmify.helpers.AlertDialogHelper;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.adapters.repeatAlarmAdapter;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

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
                Log.i("oaoaoaoaoa", String.valueOf(intent.getIntExtra("hour", 0)));
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
                repeatAlarmDays = repeatAlarmAdapter.repeatDays;
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
        if(!edit_mode){
            boolean[] exists = checkIfAlreadyExists();
            if (exists[0]&&exists[1]) {
                Toast.makeText(this, "This Alarm already exists", Toast.LENGTH_SHORT).show();
            } else if (exists[0]&&!exists[1]) {
                realmController.reActivateAlarm(alarmTime);
            } else if (!exists[0]) {
                alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Calendar now = Calendar.getInstance();
                Log.i("lalalala", String.valueOf(now.get(Calendar.HOUR_OF_DAY)) +" "+String.valueOf(now.get(Calendar.MINUTE)));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                if(calendar.before(now)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
                intent.putExtra("alarmtime", alarmTime);
                intent.putExtra("hour", time_picker.getCurrentHour());
                intent.putExtra("minutes", time_picker.getCurrentMinute());
                intent.putExtra("deleteAfterGoingOff", deleteAfterGoesOff);
                intent.putExtra("period", period);
                Log.i("monutery", String.valueOf(snoozetime));
                intent.putExtra("snooze", snoozetime);
                Log.i("angmas", String.valueOf(snoozetime));
                intent.putExtra("label", labelText);
                intent.putStringArrayListExtra("repeatList", repeatAlarmDays);
                int size = (repeatAlarmDays!=null)?repeatAlarmDays.size():0;
                intent.putExtra("repeat", size);
                final int _id = (int) System.currentTimeMillis();
                intent.putExtra("id", _id);
                pendingIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.i("fafafafafa", String.valueOf(time_picker.getCurrentHour())+String.valueOf(time_picker.getCurrentMinute()));
                if(repeatAlarmAdapter.repeatDays.size()==7){
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
                creatingNewAlarmObject(_id);
            }
        } else {
            realmController.deleteAlarm(intent.getStringExtra("time"), intent.getStringExtra("period"));
            alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Calendar now = Calendar.getInstance();
            Log.i("lalalala", String.valueOf(now.get(Calendar.HOUR_OF_DAY)) +" "+String.valueOf(now.get(Calendar.MINUTE)));
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
            calendar.set(Calendar.MINUTE, time_picker.getCurrentMinute());
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
            intent.putExtra("label", labelText);
            intent.putStringArrayListExtra("repeatList", repeatAlarmDays);
            int size = (repeatAlarmDays!=null)?repeatAlarmDays.size():0;
            intent.putExtra("repeat", size);
            final int _id = (int) System.currentTimeMillis();
            intent.putExtra("id", _id);
            pendingIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("fafafafafa", String.valueOf(time_picker.getCurrentHour())+String.valueOf(time_picker.getCurrentMinute()));
            if(repeatAlarmAdapter.repeatDays.size()==7){
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            creatingNewAlarmObject(_id);
        }
        finish();
    }

    /** Checks if an alarm already exists, can be in either state active or inactive
     * @return returns a boolean array, first element determines whether or not the alarm object exists in the database
     * second boolean element gives the state of the alarm object if it exists i.e active or inactive */
    private boolean[] checkIfAlreadyExists(){
        return realmController.checkIfAlarmExists(alarmTime, period);
    }

    /** Creates a new alarm object for storing in the local database */
    private void creatingNewAlarmObject(int pendingIntentId){
        StringBuilder builder = new StringBuilder();
        realm = realmController.getRealm();
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(alarmTime);
        newAlarm.setHour(time_picker.getCurrentHour());
        Log.i("mmmmmmm", String.valueOf(time_picker.getCurrentHour()));
        newAlarm.setPendingIntentId(pendingIntentId);
        newAlarm.setMinute(time_picker.getCurrentMinute());
        if(repeatAlarmDays==null)
            newAlarm.setDays("No Repeat");
        else {
            for(int i=0; i<repeatAlarmDays.size(); i++){
                builder.append(repeatAlarmDays.get(i)+" ");
            }
            newAlarm.setDays(builder.toString());
            String[] str = builder.toString().split(" ");
            for(int i=0; i<str.length; i++){
                Log.i("daysR", str[i]);
            }
        }
        newAlarm.setActivated(true);
        newAlarm.setSnoozeTime(snoozetime);
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
