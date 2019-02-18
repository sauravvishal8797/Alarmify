package com.my.sauravvishal8797.alarmify.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.my.sauravvishal8797.alarmify.models.Alarm;

import java.util.ArrayList;

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

    /** Returns realmController object */
    public static RealmController with(Context context){
        if (instance == null) {
            instance = new RealmController(context);
        }
        return instance;
    }

    /** Instantiates a realmController instance */
    public static RealmController getInstance(){
        return instance;
    }

    /** Retrieves the realm database instance */
    public Realm getRealm() {
        return realm;
    }

    /** Clears entire data at once from the realm database */
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

    /** Deactivates an alarm by setting the setActivated boolean variable to true */
    public void deactivateAlarm(String time){
        Alarm alarm = realm.where(Alarm.class).equalTo("time", time).findFirst();
        if(alarm!=null){
            realm.beginTransaction();
            alarm.setActivated(false);
            alarm.setNoOfTimesSnoozed(0);
            realm.commitTransaction();
        } else {
            Log.i("nullmessage", "alarm is null");
        }
    }

    /** Reactivates a deactivated alarm */
    public void reActivateAlarm(String time){
        Alarm alarm = realm.where(Alarm.class).equalTo("time", time).findFirst();
        int pendingIntentId = alarm.getPendingIntentId();
        realm.beginTransaction();
        alarm.setActivated(true);
        alarm.setNoOfTimesSnoozed(0);
        realm.commitTransaction();
    }

    /** Retrieves all the activated/deactivated alarm data from the database
     * @return list of all the alarm realm objects stored in the database
     * */
    public RealmResults<Alarm> getAlarms(){
        return realm.where(Alarm.class).findAll();
    }

    /** Retrieves all the activated alarms from the database
     * @return list of all the activated alarm objects
     */
    public ArrayList<Alarm> getActivatedAlarms(){
        ArrayList<Alarm> activatedAlarms = new ArrayList<>();
        RealmResults<Alarm> allAlarms = realm.where(Alarm.class).findAll();
        for(Alarm a: allAlarms){
            if(a.isActivated()){
                activatedAlarms.add(a);
            }
        }
        return activatedAlarms;
    }

    /** Adds new activated alarm details to the database */
    public void addAlarm(Alarm alarm){
        realm.beginTransaction();
        Alarm alarm1 = realm.createObject(Alarm.class, alarm.getTime());
       // alarm1.setTime(alarm.getTime());
        alarm1.setHour(alarm.getHour());
        alarm1.setMinute(alarm.getMinute());
        alarm1.setDays(alarm.getDays());
        alarm1.setPeriod(alarm.getPeriod());
        alarm1.setActivated(alarm.isActivated());
        alarm1.setLabel(alarm.getLabel());
        alarm1.setSnoozeTime(alarm.getSnoozeTime());
        alarm1.setDeleteAfterGoesOff(alarm.isDeleteAfterGoesOff());
        realm.commitTransaction();
    }

    /** Deletes particular alarm data from the database */
    public void deleteAlarm(final String time, final String period){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Alarm> realmResults = realm.where(Alarm.class).equalTo("time", time).findAll();
                if(realmResults.get(0).getPeriod().equals(period)||realmResults.get(1).getPeriod().equals(period))
                    realmResults.deleteAllFromRealm();
            }
        });
    }

    public boolean checkAlarmState(String time){
        boolean activeState = false;
        Alarm alarm = realm.where(Alarm.class).equalTo("time", time).findFirst();
        if(alarm!=null && alarm.isActivated()){
            activeState = true;
        }
        return activeState;
    }

    /** Checks if an alarm is already activated for a particular time
     * @return boolean array with first element determining whether the alarm already exists in the database
     * and second element determines if the alarm is in activated state
     * */
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
                        exists[1] = false;
                    }
                }
            }
        });
        return exists;
    }
}
