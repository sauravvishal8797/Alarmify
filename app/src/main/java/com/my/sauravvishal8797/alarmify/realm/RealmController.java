package com.my.sauravvishal8797.alarmify.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.my.sauravvishal8797.alarmify.models.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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

    /**
     * Retrives realm controller instance for particular activity scope
     * @param activity activity requesting for the RealmController instance
     * @return RealmController instance to the calling activity
     */
    public static RealmController with(Activity activity){
        if(instance == null){
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    /**
     * Retrieves RealmController instance
     * @param context context of the calling method
     * @return RealmController instance to the calling functionality
     */
    public static RealmController with(Context context){
        if (instance == null) {
            instance = new RealmController(context);
        }
        return instance;
    }

    /**
     * Retrieves a realm-controller instance
     * @return RealmController instance to be used by the calling method for realm operations
     */
    public static RealmController getInstance(){
        return instance;
    }

    /**
     * Retrieves the realm database instance
     * @return Realm instance to be used for cammunicating with the realm database
     */
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

    /**
     * Deactivates an alarm by setting the setActivated boolean variable to true
     * @param time time of the alarm to deactivate
     */
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

    /**
     * Reactivates a deactivated alarm
     * @param time time of the alarm to reactivate
     */
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

    /**
     * Adds new alarm object to the database
     * @param alarm The alarm object containing all the info about the alarm
     */
    public void addAlarm(Alarm alarm){
        realm.beginTransaction();
        Alarm alarm1 = realm.createObject(Alarm.class, alarm.getTime());
        alarm1.setHour(alarm.getHour());
        alarm1.setMinute(alarm.getMinute());
        alarm1.setDays(alarm.getDays());
        alarm1.setTimeInMillis(alarm.getTimeInMillis());
        alarm1.setPeriod(alarm.getPeriod());
        alarm1.setActivated(alarm.isActivated());
        alarm1.setLabel(alarm.getLabel());
        alarm1.setSnoozeTime(alarm.getSnoozeTime());
        alarm1.setDeleteAfterGoesOff(alarm.isDeleteAfterGoesOff());
        realm.commitTransaction();
    }

    /**
     * Deletes specific alarm data from the database
     * @param time The time of the alarm
     * @param period The time-period(AM/PM)
     */
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

    /**
     * Checks the state of a particular alarm i.e whether active or not
     * @param time The time for which the state is to be checked
     * @return A boolean variable indicating active or inactive state of the alarm
     */
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
     *
     * @param time The time to check for
     * @param period The time-period (AM/PM)
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

    /**
     * Retrieves activated alarms in order
     * @return List of alarm objects with all the activated alarms first and then the deactivated alarms
     */
    public ArrayList<Alarm> getActivatedAlarmsInOrder(){
        ArrayList<Alarm> alarms = new ArrayList<>();
        ArrayList<Alarm> alarmsNotActivated = new ArrayList<>();
        RealmResults<Alarm> realmResults = realm.where(Alarm.class).findAll();
        for (Alarm s: realmResults){
            if (s.isActivated())
                alarms.add(s);
            else
                alarmsNotActivated.add(s);
        }
        Collections.sort(alarms);
        alarms.addAll(alarmsNotActivated);
        return alarms;
    }

    /**
     * Retrieves next active alarm in the list
     * @return Alarm object
     */
    public Alarm getNextAlarm(){
        ArrayList<Alarm> activatedAlarms = new ArrayList<>();
        RealmResults<Alarm> allAlarms = realm.where(Alarm.class).findAll();
        for(Alarm a: allAlarms){
            if(a.isActivated()){
                activatedAlarms.add(a);
            }
        }
        Collections.sort(activatedAlarms);
        Calendar calendar = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();
        int count = 0, i = 0;
        boolean breakout = false;
        while (i < activatedAlarms.size() && (activatedAlarms.get(i).getDays() != null ||
                !activatedAlarms.get(i).getDays().isEmpty())){
            alarmTime.setTimeInMillis(activatedAlarms.get(i).getTimeInMillis());
            if (!alarmTime.before(calendar)){
                count = i;
                breakout = true;
            }
            if (breakout)
                break;
            else
                i++;
        }
        return activatedAlarms.get(count);
    }
}

