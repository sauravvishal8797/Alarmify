package com.example.sauravvishal8797.alarmify;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.sauravvishal8797.alarmify.adapters.AlarmAdapter;
import com.example.sauravvishal8797.alarmify.models.Alarm;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmRecyclerView = (RecyclerView) findViewById(R.id.viewalarm);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        AlarmAdapter alarmAdapter = new AlarmAdapter(getData());
        alarmRecyclerView.setAdapter(alarmAdapter);
        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        statusBarTransparent();
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

    //method to set the status bar transparent
    private void statusBarTransparent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private ArrayList<Alarm> getData(){
        ArrayList<Alarm> alarms = new ArrayList<>();
        for(int i=0; i<10; i++){
            ArrayList<String> days = new ArrayList<>();
            days.add("Tu ");
            days.add("Wed ");
            days.add("Thru ");
            Alarm a = new Alarm();
            a.setTime("7:30");
            a.setPeriod("AM");
            a.setDays(days);
            alarms.add(a);
        }
        return alarms;
    }
}
