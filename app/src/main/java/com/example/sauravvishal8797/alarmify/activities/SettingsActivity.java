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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView dismissMissionLevelText;
    private RelativeLayout dismissQuestionsLayout;
    private TextView noOfDismissQuestions;
    private LinearLayout dismissMissionLevelView;
    private LinearLayout autoDismissView;
    private TextView autoDismissTimeText;
    private CheckBox preventPowerOffCheckbox;
    private CheckBox setAlarmconfirmationCheckbox;
    private SwitchCompat editSavedAlarmsButton;
    private LinearLayout setMaxSnoozesLayout;
    private TextView setMaxSnoozeText;

    /** Obtaining an instance of PreferenceUtil for SharedPreferences cammunications */
    private PreferenceUtil SP;

    /** Keeping track of the auto-dismiss time if alloted */
    private int autoDismissTime = 0;
    private int autoDismissTimeArrayPos = 0;

    /**Keeping track of the dismiss mission level */
    private String dismissLevel = " ";
    private int levelArrayPos = 0;

    //For keeping track of the question count selected by users
    private int noOfQuestions = 0;
    private int questionsArrayPos=0;

    //for max no of snoozes
    private int maxSnoozeArrayPos = 0;
    private String maxSnoozeValue=" ";

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

        /** Set-Alarm confirmation */
        setAlarmconfirmationCheckbox = findViewById(R.id.set_alarm_confirmation_checkbox);
        setAlarmconfirmationCheckbox.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.set_alarm_confirmation), false)){
            setAlarmconfirmationCheckbox.setChecked(true);
        } else {
            setAlarmconfirmationCheckbox.setChecked(false);
        }
        setAlarmconfirmationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.set_alarm_confirmation), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.set_alarm_confirmation), false);
                    editor.commit();
                }
            }
        });

        /** Edit saved alarms */
        editSavedAlarmsButton = findViewById(R.id.enable_alarm_edit_switch);
        editSavedAlarmsButton.setOnCheckedChangeListener(null);
        if(SP.getBoolean(getResources().getString(R.string.edit_saved_alarm_action_mssg), true)){
            editSavedAlarmsButton.setChecked(true);
        } else {
            editSavedAlarmsButton.setChecked(false);
        }
        editSavedAlarmsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.edit_saved_alarm_action_mssg), true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putBoolean(getResources().getString(R.string.edit_saved_alarm_action_mssg), false);
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

        /**Dismiss mission level */
        dismissMissionLevelView = findViewById(R.id.dismiss_alarm_mission_level_view);
        dismissMissionLevelText = findViewById(R.id.dismiss_mission_level_text);
        dismissMissionLevelText.setText(SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None"));
        dismissMissionLevelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().
                        getString(R.string.default_dismiss_mission)).equals(getResources().getString(R.string.default_dismiss_mission))){
                    Toast.makeText(getApplicationContext(), "Dismiss mission level is only allowed to be set for Maths Puzzle mission",
                            Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
                    builder.setTitle("Choose Dismiss Mission Level");
                    final int checkedItem2 = SP.getInt(getResources().getString(R.string.dismiss_level_pos), 0);
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.dismiss_mission_level), checkedItem2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case 0:
                                    dismissLevel = getResources().getString(R.string.dismiss_mission_level_default);
                                    levelArrayPos = 0;
                                    break;

                                case 1:
                                    dismissLevel = getResources().getString(R.string.dismiss_mission_level_easy);
                                    levelArrayPos = 1;
                                    break;

                                case 2:
                                    dismissLevel = getResources().getString(R.string.dismiss_mission_level_medium);
                                    levelArrayPos = 2;
                                    break;

                                case 3:
                                    dismissLevel = getResources().getString(R.string.dismiss_mission_level_hard);
                                    levelArrayPos = 3;
                                    break;

                                default:
                                    dismissLevel = getResources().getString(R.string.dismiss_mission_level_default);
                                    levelArrayPos = 0;
                            }
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), dismissLevel);
                            editor.putInt(getResources().getString(R.string.dismiss_level_pos), levelArrayPos);
                            editor.commit();
                            if(levelArrayPos==0){
                                dismissMissionLevelText.setText("None");
                            } else {
                                dismissMissionLevelText.setText(dismissLevel);
                            }
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
            }
        });

        /** Dismiss Questions View */
        dismissQuestionsLayout = findViewById(R.id.dismiss_question_no_view);
        noOfDismissQuestions = findViewById(R.id.no_of_dismiss_questions);
        noOfDismissQuestions.setText(String.valueOf(SP.getInt(getResources().getString(R.string.dismiss_maths_mission_ques), 3)));
        dismissQuestionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().
                        getString(R.string.default_dismiss_mission)).equals(getResources().getString(R.string.default_dismiss_mission))){
                    Toast.makeText(getApplicationContext(), "Setting dismiss questions is only allowed for Maths Puzzle mission",
                            Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
                    builder.setTitle("Choose no of questions");
                    final int checkPos = SP.getInt(getResources().getString(R.string.dismiss_maths_mission_quest_array_pos), 0);
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.dismiss_mission_questions), checkPos, new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    switch (i){
                                        case 0:
                                            noOfQuestions = 3;
                                            questionsArrayPos = 0;
                                            break;

                                        case 1:
                                            noOfQuestions = 4;
                                            questionsArrayPos = 1;
                                            break;

                                        case 2:
                                            noOfQuestions = 5;
                                            questionsArrayPos = 2;
                                            break;

                                        case 3:
                                            noOfQuestions = 6;
                                            questionsArrayPos = 3;
                                            break;

                                        case 4:
                                            noOfQuestions = 7;
                                            questionsArrayPos = 4;
                                            break;

                                        case 5:
                                            noOfQuestions = 8;
                                            questionsArrayPos = 5;
                                            break;

                                        default:
                                            noOfQuestions = 3;
                                            questionsArrayPos = 0;
                                    }

                                }
                            });

                    final AlertDialog dialog = builder.create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putInt(getResources().getString(R.string.dismiss_maths_mission_ques), noOfQuestions);
                            editor.putInt(getResources().getString(R.string.dismiss_maths_mission_quest_array_pos), questionsArrayPos);
                            editor.commit();
                            noOfDismissQuestions.setText(String.valueOf(noOfQuestions));
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
            }
        });

        /** Dismiss Mission */
        dismissAlarmMissionView = findViewById(R.id.dismiss_alarm_mission_view);
        dismissMissionText = findViewById(R.id.dismiss_mission_text);
        dismissMissionText.setText(SP.getString(getResources().getString(R.string.dismiss_default_text),
                getResources().getString(R.string.default_dismiss_mission)));
        if(SP.getString(getResources().getString(R.string.dismiss_default_text), getResources().
                getString(R.string.default_dismiss_mission)).equals(getResources().getString(R.string.default_dismiss_mission))){
            if(!SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("None")){
                SharedPreferences.Editor editor = SP.getEditor();
                editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "None");
                editor.putInt(getResources().getString(R.string.dismiss_level_pos), 0);
                editor.commit();
                dismissMissionLevelText.setText("None");
                noOfDismissQuestions.setText("None");
            }
        } else {
            if(SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("None")) {
                SharedPreferences.Editor editor = SP.getEditor();
                editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "Easy");
                editor.putInt(getResources().getString(R.string.dismiss_level_pos), 1);
                editor.commit();
                dismissMissionLevelText.setText("Easy");
                noOfDismissQuestions.setText(SP.getString(getResources().getString(R.string.dismiss_maths_mission_ques), String.valueOf(3)));
            }
        }
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
                    dismissMissionText.setText(getResources().getString(R.string.default_dismiss_mission));
                    if(!SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("None")){
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "None");
                        editor.putInt(getResources().getString(R.string.dismiss_level_pos), 0);
                        editor.commit();
                        dismissMissionLevelText.setText("None");
                        noOfDismissQuestions.setText("None");
                    }
                } else {
                    setchecked = 1;
                    dismissMissionText.setText(getResources().getString(R.string.maths_mission_dismiss));
                    if(SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("None")) {
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "Easy");
                        editor.putInt(getResources().getString(R.string.dismiss_level_pos), 1);
                        editor.commit();
                        dismissMissionLevelText.setText("Easy");
                        noOfDismissQuestions.setText(SP.getString(getResources().getString(R.string.dismiss_maths_mission_ques), String.valueOf(3)));
                    }
                }
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.dismiss_mission_options), setchecked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            SharedPreferences.Editor editor = SP.getEditor();
                            editor.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                                    getString(R.string.default_dismiss_mission));
                            editor.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "None");
                            editor.putInt(getResources().getString(R.string.dismiss_level_pos), 0);
                            editor.commit();
                            dismissMissionLevelText.setText("None");
                        }else {
                            SharedPreferences.Editor editor2 = SP.getEditor();
                            editor2.putString(getResources().getString(R.string.dismiss_default_text), getResources().
                                    getString(R.string.maths_mission_dismiss));
                            if(SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level), "None").equals("None")) {
                                editor2.putString(getResources().getString(R.string.dismiss_alarm_mission_level), "Easy");
                                editor2.putInt(getResources().getString(R.string.dismiss_level_pos), 1);
                                dismissMissionLevelText.setText("Easy");
                            }
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
                       /** if (dismiss_miss.equals(getResources().getString(R.string.default_dismiss_mission))){
                            //disabaling the questions preference
                           // dismissQuestionsLayout.setVisibility(View.GONE);
                            //dismissMissionLevelText.setText("None");
                           // dismissMissionLevelView.setOnClickListener(null);
                        } else {
                            //dismissQuestionsLayout.setVisibility(View.VISIBLE);
                            //dismissMissionLevelText.setText(SP.getString(getResources().getString(R.string.dismiss_alarm_mission_level),
                              //      getResources().getString(R.string.dismiss_mission_level_default)));
                        }*/
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
        autoDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int checkedItem = SP.getInt(getResources().getString(R.string.auto_dismiss_timr_pos), 0);
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

        /** Set max no of snoozes */
        setMaxSnoozesLayout = findViewById(R.id.set_max_snoozes_layout);
        setMaxSnoozeText = findViewById(R.id.max_nof_snoozes_text);
        setMaxSnoozeText.setText(SP.getString(getResources().getString(R.string.set_max_snoozes), "Unlimited"));
        setMaxSnoozesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkeditempos = SP.getInt(getResources().getString(R.string.max_snoozes_array_pos), 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
                builder.setTitle("Choose Max no of snoozes");
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.max_snooze_options), checkeditempos,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0:
                                        maxSnoozeValue="Unlimited";
                                        maxSnoozeArrayPos=0;
                                        break;

                                    case 1:
                                        maxSnoozeValue="1";
                                        maxSnoozeArrayPos=1;
                                        break;

                                    case 2:
                                        maxSnoozeValue="2";
                                        maxSnoozeArrayPos=2;
                                        break;

                                    case 3:
                                        maxSnoozeValue="3";
                                        maxSnoozeArrayPos=3;
                                        break;

                                    case 4:
                                        maxSnoozeValue="4";
                                        maxSnoozeArrayPos=4;
                                        break;

                                    case 5:
                                        maxSnoozeValue="5";
                                        maxSnoozeArrayPos=5;
                                        break;

                                    case 6:
                                        maxSnoozeValue="6";
                                        maxSnoozeArrayPos=6;
                                        break;

                                        default:
                                            maxSnoozeValue="Unlimited";
                                            maxSnoozeArrayPos=0;
                                            break;

                                }
                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK".toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        SharedPreferences.Editor editor = SP.getEditor();
                        editor.putString(getResources().getString(R.string.set_max_snoozes), maxSnoozeValue);
                        editor.putInt(getResources().getString(R.string.max_snoozes_array_pos), maxSnoozeArrayPos);
                        editor.commit();
                        setMaxSnoozeText.setText(maxSnoozeValue);
                        Log.i("cunttttttttttttt", maxSnoozeValue);
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
