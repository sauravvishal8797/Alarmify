package com.example.sauravvishal8797.alarmify;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private RelativeLayout parentlayout;
    private LinearLayout emptyView;
    private BottomNavigationView bottomNavigationView;
    private RealmController realmController;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentlayout = (RelativeLayout) findViewById(R.id.layout_id);
        emptyView = (LinearLayout) findViewById(R.id.no_alarm_view);
        alarmRecyclerView = (RecyclerView) findViewById(R.id.viewalarm);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        if (getData().size()==0)
            emptyView.setVisibility(View.VISIBLE);
        else {
           setAdapter();
        }
        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.all_alarms);
        statusBarTransparent();
    }

    private void setAdapter(){
        AlarmAdapter alarmAdapter = new AlarmAdapter(getData(), this);
        alarmRecyclerView.setAdapter(alarmAdapter);
    }

    private int navigationMenu(){
        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        bottomNavigationView.setSelectedItemId(R.id.all_alarms);
    }

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

    //method to set the status bar transparent
    private void statusBarTransparent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private ArrayList<Alarm> getData(){
        ArrayList<Alarm> allAlarms = new ArrayList<>();
        realmController = RealmController.with(this);
        realm = realmController.getRealm();
        RealmResults<Alarm> realmResults = realmController.getAlarms();
        if (realmResults.size()==0)
            return allAlarms;
        for(int i = 0; i < realmResults.size(); i++){
            allAlarms.add(realmResults.get(i));
        }
       return allAlarms;
    }
}
