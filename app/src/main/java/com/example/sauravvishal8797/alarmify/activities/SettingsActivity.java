package com.example.sauravvishal8797.alarmify.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.jaeger.library.StatusBarUtil;

public class SettingsActivity extends AppCompatActivity{

    /** Declaring UI elements */
    private SwitchCompat moveActiveAlarmsTopButton;
    private SwitchCompat ringerMaxButton;
    private CheckBox useBuiltInSpeakerCheckbox;
    private LinearLayout dismissAlarmMissionView;
    private TextView dismissMissionText;
    private LinearLayout autoDismissView;
    private TextView autoDismissTimeText;
    private CheckBox preventPowerOffCheckbox;

    /** Obtaining an instance of PreferenceUtil for SharedPreferences cammunications */
    private PreferenceUtil SP;

    /** Keeping track of the auto-dismiss time if alloted */
    private int autoDismissTime = 0;
    private int autoDismissTimeArrayPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        SP = PreferenceUtil.getInstance(this);
        statusBarTransparent();
        setUI();
    }

    /** patches up the UI elements */
    private void setUI(){

        /** Move Active Alarms to top */
        moveActiveAlarmsTopButton = findViewById(R.id.move_active_alarms_switch);
        moveActiveAlarmsTopButton.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.move_active_alarms_mssg), false)){
            moveActiveAlarmsTopButton.setChecked(true);
        } else {
            moveActiveAlarmsTopButton.setChecked(false);
        }
        moveActiveAlarmsTopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.move_active_alarms_mssg), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.move_active_alarms_mssg), false);
                    editor.commit();
                }
            }
        });

        /** Prevent phone power-off */
        preventPowerOffCheckbox = findViewById(R.id.prevent_power_off_checkbox);
        preventPowerOffCheckbox.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.prevent_phone_power_off), false)){
            preventPowerOffCheckbox.setChecked(true);
        } else {
            preventPowerOffCheckbox.setChecked(false);
        }
        preventPowerOffCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.prevent_phone_power_off), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.prevent_phone_power_off), false);
                    editor.commit();
                }
            }
        });


        /** Ringer Volume Max */
        ringerMaxButton = findViewById(R.id.ringer_max_button);
        ringerMaxButton.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false)){
            ringerMaxButton.setChecked(true);
        } else {
            ringerMaxButton.setChecked(false);
        }
        ringerMaxButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.set_ringer_value_max_mssg), false);
                    editor.commit();
                }
            }
        });

        /** Use Buit-in Speaker Checkbox */
        useBuiltInSpeakerCheckbox = findViewById(R.id.use_built_in_speakers_checkbox);
        useBuiltInSpeakerCheckbox.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.use_built_in_speaker_mssg), false)){
            useBuiltInSpeakerCheckbox.setChecked(true);
        } else {
            useBuiltInSpeakerCheckbox.setChecked(false);
        }
        useBuiltInSpeakerCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.use_built_in_speaker_mssg), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.use_built_in_speaker_mssg), false);
                    editor.commit();
                }
            }
        });

        /** Dismiss Mission */
        dismissAlarmMissionView = findViewById(R.id.dismiss_alarm_mission_view);
        dismissMissionText = findViewById(R.id.dismiss_mission_text);
        dismissAlarmMissionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int setchecked = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
                builder.setTitle("Choose Dismiss mission");
                // builder.s
                String dismiss_miss = SP.getString(getResources().getString(R.string.dismiss_default_text),
                        getResources().getString(R.string.default_dismiss_mission));
                if(dismiss_miss.equals(getResources().getString(R.string.default_dismiss_mission))){
                    setchecked = 0;
                } else {
                    setchecked = 1;
                }
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.dismiss_mission_options), setchecked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                                    getString(R.string.default_dismiss_mission));
                            editor.commit();
                        }else {
                            SharedPreferences.Editor editor2 = SP.getEditor();
                            editor2.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                                    getString(R.string.maths_mission_dismiss));
                            editor2.commit();
                            //dismissMissionText.setText(getResources().getString(R.string.maths_mission_dismiss));
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        String dismiss_miss = SP.getString(getResources().getString(R.string.dismiss_default_text),
                                getResources().getString(R.string.default_dismiss_mission));
                        dismissMissionText.setText(dismiss_miss);
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        String dismiss_miss = SP.getString(getResources().getString(R.string.dismiss_default_text),
                                getResources().getString(R.string.default_dismiss_mission));
                        dismissMissionText.setText(dismiss_miss);
                    }
                });
                dialog.show();
            }
        });

        /** Auto-Dismiss Alarm */
        autoDismissView = findViewById(R.id.auto_dismiss_layout);
        autoDismissTimeText = findViewById(R.id.auto_dismiss_time_text);
        if(SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0)>0){
            autoDismissTimeText.setText(String.valueOf(SP.getInt(getResources().getString(R.string.auto_dismiss_time), 0)) + " minutes");
        }
        final int checkedItem = SP.getInt(getResources().getString(R.string.auto_dismiss_timr_pos), 0);
        autoDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
                builder.setTitle("Choose Auto-dismiss period");
                // builder.s
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.auto_dismiss_options), checkedItem,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       switch (i){
                           case 0:
                               autoDismissTime = 0;
                               autoDismissTimeArrayPos=0;
                               break;

                           case 1:
                               autoDismissTime = 1;
                               autoDismissTimeArrayPos=1;
                               break;

                           case 2:
                               autoDismissTime = 5;
                               autoDismissTimeArrayPos=2;
                               break;

                           case 3:
                               autoDismissTime = 7;
                               autoDismissTimeArrayPos=3;
                               break;

                           case 4:
                               autoDismissTime = 10;
                               autoDismissTimeArrayPos=4;
                               break;

                           case 5:
                               autoDismissTime = 15;
                               autoDismissTimeArrayPos=5;
                               break;

                           case 6:
                               autoDismissTime = 20;
                               autoDismissTimeArrayPos=6;
                               break;

                           case 7:
                               autoDismissTime = 25;
                               autoDismissTimeArrayPos=7;
                               break;

                           case 8:
                               autoDismissTime = 30;
                               autoDismissTimeArrayPos=8;
                               break;

                               default:
                                   autoDismissTime=0;
                                   autoDismissTimeArrayPos=0;

                       }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putInt(getResources().getString(R.string.auto_dismiss_time), autoDismissTime);
                        editor.putInt(getResources().getString(R.string.auto_dismiss_timr_pos), autoDismissTimeArrayPos);
                        editor.commit();
                        if(autoDismissTime==0){
                            autoDismissTimeText.setText("Off");
                        } else {
                            autoDismissTimeText.setText(String.valueOf(autoDismissTime) + " minutes");
                        }
                        Log.i("cunttttttttttttt", String.valueOf(autoDismissTime));
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
        });
    }

    /** Sets the status bar to transparent */
    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }
}
