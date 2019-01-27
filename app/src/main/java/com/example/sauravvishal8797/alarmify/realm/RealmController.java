package com.example.sauravvishal8797.alarmify.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.sauravvishal8797.alarmify.models.Alarm;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application){
        realm = Realm.getDefaultInstance();
    }

    public RealmController(Context context){
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Activity activity){

        if(instance == null){
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Context context){
        if (instance == null) {
            instance = new RealmController(context);
        }
        return instance;
    }

    public static RealmController getInstance(){
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the Realm instance

    //Clear all the alarms from the database
    public void clearAll(){
        realm.beginTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> realmResults = realm.where(Alarm.class).findAll();
                realmResults.deleteAllFromRealm();
            }
        });
        realm.commitTransaction();
    }

    public void deactivateAlarm(String time){
        Alarm alarm = realm.where(Alarm.class).equalTo("time", time).findFirst();
        realm.beginTransaction();
        alarm.setActivated(false);
        realm.commitTransaction();
    }

    public void reActivateAlarm(String time){
        Alarm alarm = realm.where(Alarm.class).equalTo("time", time).findFirst();
        realm.beginTransaction();
        alarm.setActivated(true);
        realm.commitTransaction();
    }

    //Retrieve all the active alarm details
    public RealmResults<Alarm> getAlarms(){

        return realm.where(Alarm.class).findAll();
    }

    //Add new alarm details to the database
    public void addAlarm(Alarm alarm){
        realm.beginTransaction();
        Alarm alarm1 = realm.createObject(Alarm.class, alarm.getTime());
       // alarm1.setTime(alarm.getTime());
        alarm1.setDays(alarm.getDays());
        alarm1.setPeriod(alarm.getPeriod());
        alarm1.setActivated(alarm.isActivated());
        alarm1.setLabel(alarm.getLabel());
        alarm1.setSnoozeTime(alarm.getSnoozeTime());
        alarm1.setDeleteAfterGoesOff(alarm.isDeleteAfterGoesOff());
        realm.commitTransaction();
    }

    //Delete an alarm from the database
    public void deleteAlarm(final String time){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> realmResults = realm.where(Alarm.class).equalTo("time", time).findAll();
                realmResults.deleteAllFromRealm();
            }
        });
    }


    /** Checks if an alarm is already activated for a particular time */
    public boolean[] checkIfAlarmExists(final String time, final String period){
        final boolean[] exists = {false, false};
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("time", time).findAll();
                if(results.size() > 0){
                    if(results.get(0).getPeriod().equals(period)&&results.get(0).isActivated()){
                        exists[0] =true;
                        exists[1] = true;
                    } else if(results.get(0).getPeriod().equals(period)&&!results.get(0).isActivated()) {
                        exists[0] = true;
                        exists[0] = false;
                    }
                }
            }
        });
        return exists;
    }
}
