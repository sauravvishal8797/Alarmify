package com.my.sauravvishal8797.alarmify.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my.sauravvishal8797.alarmify.R;
import com.my.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.my.sauravvishal8797.alarmify.helpers.AlertDialogHelper;
import com.my.sauravvishal8797.alarmify.helpers.BasicCallback;
import com.my.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.my.sauravvishal8797.alarmify.models.Alarm;
import com.my.sauravvishal8797.alarmify.realm.RealmController;
import com.my.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.jaeger.library.StatusBarUtil;
import com.my.sauravvishal8797.alarmify.receivers.RepeatAlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.support.v7.widget.RecyclerView.*;

public class MainActivity extends AppCompatActivity{

    /**
     * Declaring UI widgets
     */

    private RecyclerView alarmRecyclerView;
    private RelativeLayout parentlayout;
    private LinearLayout emptyView;
    private BottomNavigationView bottomNavigationView;
    private TextView optionsMenuTextView;

    private RealmController realmController;
    private Realm realm;
    private PreferenceUtil SP;
    private SharedPreferences.Editor editor;

    private BasicCallback basicCallback = new BasicCallback() {
        @Override
        public void callBack(int statusCode) {
            if(statusCode == 2){
                emptyView.setVisibility(VISIBLE);
                optionsMenuTextView.setVisibility(GONE);
            }
        }
    };

    private View.OnClickListener alarmItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Alarm alarm = (Alarm) view.findViewById(R.id.timetextalarm).getTag();
            Intent intent1 = new Intent(view.getContext(), AlarmDetailActivity.class);
            intent1.putExtra("alarm_edit", true);
            intent1.putExtra("hour", alarm.getHour());
            intent1.putExtra("time", alarm.getTime());
            intent1.putExtra("minute", alarm.getMinute());
            intent1.putExtra("period", alarm.getPeriod());
            intent1.putExtra("delete_after_going_off", alarm.isDeleteAfterGoesOff());
            intent1.putExtra("label", alarm.getLabel());
            intent1.putExtra("snooze", alarm.getSnoozeTime());
            intent1.putExtra("repeatDays", alarm.getDays());
            startActivity(intent1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SP = PreferenceUtil.getInstance(this);
        editor = SP.getEditor();
        editor.putString("ringing", "not");
        editor.commit();
        if (SP.getString("setRepeatAlarmcheck", "no").equals("no"))
            setForRepeatingAlarms();
        setUpUI();
        statusBarTransparent();
        askAutoStartPermission();
    }

    /** Sets up a 1 am check for repeat alarms for the particular day
     * and activates if any
     */
    private void setForRepeatingAlarms(){
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 7);
        calendar.set(Calendar.SECOND, 39);
        Intent repeatIntent = new Intent(MainActivity.this, RepeatAlarmReceiver.class);
        repeatIntent.putExtra("repeatdaysAlarm", "yes");
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, _id, repeatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        editor.putString("setRepeatAlarmcheck", "yes");
        editor.commit();
    }


    /** Manages UI elements */
    private void setUpUI(){
        alarmRecyclerView = findViewById(R.id.viewalarm);
        alarmRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        parentlayout = findViewById(R.id.layout_id);
        emptyView = findViewById(R.id.no_alarm_view);
        bottomNavigationView = findViewById(R.id.bottombar);
        optionsMenuTextView = findViewById(R.id.Options_menu);
        optionsMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        if (getData().size()==0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            setAdapter();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.all_alarms);
    }

    /** Displays the popup menu, present at the top right corner of the Activity */
    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.delete_all_alarms:
                        if(deleteAllAlarms()[0]){
                            disableAllActiveAlarms();
                            setAdapter();
                        }else {
                        }
                        return true;
                }
                return false;
            }
        });
        inflater.inflate(R.menu.mainactivity_options_menu, popup.getMenu());
        popup.show();
    }

    /**Disables all the active alarms */
    private void disableAllActiveAlarms(){
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * To delete all the active/inactive alarm data from the database at once
     * @return boolean variable to indicate the success or failure of the operation
     */
    private boolean[] deleteAllAlarms(){
        final boolean[] deleted = {false};
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> realmResults = realm.where(Alarm.class).findAll();
                deleted[0] = realmResults.deleteAllFromRealm();
            }
        });
        return deleted;
    }

    /**
     * Populates the adapter to display all the alarm data from the realm database
     * Displays an emptyView with a message if no data is stored in the database
     */
    private void setAdapter(){
        if(getData().size()==0){
            alarmRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            optionsMenuTextView.setVisibility(View.GONE);
        }else {
            emptyView.setVisibility(View.GONE);
            alarmRecyclerView.setVisibility(View.VISIBLE);
            if(optionsMenuTextView.getVisibility() == View.GONE){
                optionsMenuTextView.setVisibility(View.VISIBLE);
            }
            AlarmAdapter alarmAdapter = new AlarmAdapter(getData(), this, this, basicCallback);
            alarmAdapter.setOnClickListener(alarmItemClickListener);
            alarmRecyclerView.setAdapter(alarmAdapter);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        bottomNavigationView.setSelectedItemId(R.id.all_alarms);
        setAdapter();
    }

    /**
     * BottomNavigation view itemSelectListener
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.create_alarm:
                    Intent intent = new Intent(MainActivity.this, AlarmDetailActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.all_alarms:
                    return true;

                case R.id.settings:
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    return true;
            }
            return false;
        }
    };

    /**Managing Auto-Start permission for Xioami MI phone users*/
    private void  askAutoStartPermission(){
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER) && SP.
                getString("firstLaunch", "yes").equals("yes")) {
            //this will open auto start screen where user can enable permission for your app
            android.app.AlertDialog alertDialog = AlertDialogHelper.getTextDialog(MainActivity.this, "Auto-Start permission",
                    "You need to give the app auto-start permisison to make sure that the alarm rings at the right time.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = SP.getEditor();
                    editor.putString("firstLaunch", "no");
                    editor.commit();
                    Intent intent1 = new Intent();
                    intent1.setComponent(new ComponentName("com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    startActivity(intent1);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    /** Sets the theme of the status bar to transparent */
    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }

    /**
     *Retrieves all the alarm data from the database making use of
     * getAlarms() from the RealmController class
     * @return ArrayList of Alarm objects
     */
    private ArrayList<Alarm> getData(){
        ArrayList<Alarm> allAlarms = new ArrayList<>();
        ArrayList<Alarm> activeAlarms = new ArrayList<>();
        ArrayList<Alarm> inActiveAlarms = new ArrayList<>();
        realmController = RealmController.with(this);
        realm = realmController.getRealm();
        RealmResults<Alarm> realmResults = realmController.getAlarms();
        if (realmResults.size()==0) return allAlarms;
        if(SP.getBoolean(getResources().getString(R.string.move_active_alarms_mssg), false)){
            for(int i = 0; i < realmResults.size(); i++){
                if(realmResults.get(i).isActivated()){
                    activeAlarms.add(realmResults.get(i));
                } else {
                    inActiveAlarms.add(realmResults.get(i));
                }
            }
            allAlarms.addAll(activeAlarms);
            allAlarms.addAll(inActiveAlarms);
        } else {
            for(int i = 0; i < realmResults.size(); i++){
                allAlarms.add(realmResults.get(i));
            }
        }
       return allAlarms;
    }
}
