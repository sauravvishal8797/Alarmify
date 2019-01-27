package com.example.sauravvishal8797.alarmify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;
import com.jaeger.library.StatusBarUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // ButterKnife.bind(this);
        setUpUI();
        statusBarTransparent();
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
                        Log.i("stephanie", "poi");
                        if(deleteAllAlarms()[0]){
                            disableAllActiveAlarms();
                            setAdapter();
                            Log.i("chuitya", "semantic");
                        }else {
                            Log.i("joshmourinho", "issue");
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
        /*realmController = RealmController.with(this);
        realm = realmController.getRealm();
        realmController.clearAll();*/
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> realmResults = realm.where(Alarm.class).findAll();
                deleted[0] = realmResults.deleteAllFromRealm();
            }
        });
        return deleted;
        //setAdapter();
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
            AlarmAdapter alarmAdapter = new AlarmAdapter(getData(), this, this);
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
                    Intent intent = new Intent(MainActivity.this, AlarmDetail.class);
                    startActivity(intent);
                    return true;

                case R.id.all_alarms:
                    return true;

                case R.id.settings:
                    return true;
            }
            return false;
        }
    };

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
        realmController = RealmController.with(this);
        realm = realmController.getRealm();
        RealmResults<Alarm> realmResults = realmController.getAlarms();
        if (realmResults.size()==0) return allAlarms;
        for(int i = 0; i < realmResults.size(); i++){
            allAlarms.add(realmResults.get(i));
        }
       return allAlarms;
    }
}
