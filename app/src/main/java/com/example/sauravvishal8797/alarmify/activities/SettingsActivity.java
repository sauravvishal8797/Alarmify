package com.example.sauravvishal8797.alarmify.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.jaeger.library.StatusBarUtil;

public class SettingsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    /** Declaring UI elements */
    private SwitchCompat moveActiveAlarmsTopButton;
    private SwitchCompat ringerMaxButton;
    private CheckBox useBuiltInSpeakerCheckbox;
    private LinearLayout dismissAlarmMissionView;
    private LinearLayout autoDismissView;

    /** Obtaining an instance of PreferenceUtil for SharedPreferences cammunications */
    private PreferenceUtil SP;

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
        dismissAlarmMissionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu(1, view);
            }
        });


        /** Auto-Dismiss Alarm */
        autoDismissView = findViewById(R.id.auto_dismiss_layout);
        autoDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu(0, view);
            }
        });
    }

    /** Displays popup menu options for alarmDismiss mission and autodismiss */
    private void popupMenu(int id, View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        // id==1 displays alarmDismiss mission
        if (id == 1){
            menuInflater.inflate(R.menu.dismiss_mission, popupMenu.getMenu());
            popupMenu.show();
        } else if (id == 0){
        }
    }



    /** Sets the status bar to transparent */
    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }

    /** Popup menu option click */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.deafault:
                SharedPreferences.Editor editor = SP.getEditor();
                editor.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                        getString(R.string.default_dismiss_mission));
                editor.commit();
                return true;

            case R.id.maths_mission:
                SharedPreferences.Editor editor2 = SP.getEditor();
                editor2.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                        getString(R.string.maths_mission_dismiss));
                editor2.commit();
                return true;

        }
        return false;
    }
}
