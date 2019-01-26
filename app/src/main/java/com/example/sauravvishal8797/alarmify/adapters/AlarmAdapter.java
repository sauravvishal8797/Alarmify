package com.example.sauravvishal8797.alarmify.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Layout;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.AlarmDetail;
import com.example.sauravvishal8797.alarmify.AlarmReceiver;
import com.example.sauravvishal8797.alarmify.AlertDialogHelper;
import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private ArrayList<Alarm> list;
    StringBuilder builder = new StringBuilder();
    private Context context;
    private RealmController realmController;
    private Activity activity;
    private Realm realm;

    public AlarmAdapter(ArrayList<Alarm> list, Context context, Activity activity) {
        this.list=list;
        this.context=context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        StringBuilder builder = new StringBuilder();
        final Alarm alarm = list.get(i);
        viewHolder.timeText.setText(alarm.getTime());
        viewHolder.periodText.setText(alarm.getPeriod());
        viewHolder.daysText.setText(alarm.getDays());
        if(alarm.isActivated()){
            Log.i("lalalalala", "lop");
            viewHolder.button.setChecked(true);
        } else {
            Log.i("lllll", "loo");
            viewHolder.button.setChecked(false);
        }
        viewHolder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    final AlertDialog dialog = AlertDialogHelper.getTextDialog(context, context.getResources().getString
                            (R.string.deactivate_alarm_dialog_title), context.getResources().getString(R.string.deactivate_alarm_dialog_mssg));
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.dialog_postive_button).
                            toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            deactivateAlarm(alarm.getTime());
                            viewHolder.button.setChecked(false);
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_negative_mssg).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    viewHolder.button.setChecked(true);

                                }
                            });
                    dialog.show();
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Calendar now = Calendar.getInstance();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                    calendar.set(Calendar.MINUTE, alarm.getMinute());
                    if(calendar.before(now)){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmManager.cancel(pendingIntent);
                } else {
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Calendar now = Calendar.getInstance();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                    calendar.set(Calendar.MINUTE, alarm.getMinute());
                    if(calendar.before(now)){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        });
        viewHolder.periodText.setText(alarm.getPeriod());
    }

    private void deactivateAlarm(String alarmTime){
        realmController = RealmController.with(activity);
        realmController.deactivateAlarm(alarmTime);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView timeText;
        private TextView periodText;
        private SwitchCompat button;
        private TextView daysText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            periodText = itemView.findViewById(R.id.period);
            daysText = itemView.findViewById(R.id.days);
            button = itemView.findViewById(R.id.button);
            timeText = itemView.findViewById(R.id.timetextalarm);
        }
    }
}
