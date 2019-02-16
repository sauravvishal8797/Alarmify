package com.example.sauravvishal8797.alarmify.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.activities.AlarmDetailActivity;
import com.example.sauravvishal8797.alarmify.activities.DismissAlarmActivity;
import com.example.sauravvishal8797.alarmify.helpers.BasicCallback;
import com.example.sauravvishal8797.alarmify.helpers.PreferenceUtil;
import com.example.sauravvishal8797.alarmify.receivers.AlarmReceiver;
import com.example.sauravvishal8797.alarmify.helpers.AlertDialogHelper;
import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.models.Alarm;
import com.example.sauravvishal8797.alarmify.realm.RealmController;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private ArrayList<Alarm> list;
    private Context context;
    private RealmController realmController;
    private Activity activity;
    private BasicCallback basicCallback;
    private PreferenceUtil SP;

    public AlarmAdapter(ArrayList<Alarm> list, Context context, Activity activity, BasicCallback basicCallback) {
        this.list=list;
        this.context=context;
        this.activity = activity;
        this.basicCallback = basicCallback;
        SP = PreferenceUtil.getInstance(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final boolean[] deactivateAlert = {true};
        final Alarm alarm = list.get(i);
        Log.i("papapapapapa", String.valueOf(alarm.getTime()));
        if(alarm.getTime().startsWith("0")&&alarm.getTime().substring(0, alarm.getTime().indexOf(":")).length()==3){
            viewHolder.timeText.setText(alarm.getTime().substring(1));
        } else {
            viewHolder.timeText.setText(alarm.getTime());
        }
        viewHolder.periodText.setText(alarm.getPeriod());
        viewHolder.daysText.setText(alarm.getDays());
        viewHolder.button.setOnCheckedChangeListener(null);
        if(alarm.isActivated()){
            viewHolder.button.setChecked(true);
            deactivateAlert[0]=true;
        } else {
            viewHolder.button.setChecked(false);
            deactivateAlert[0]=false;
        }
        viewHolder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b && deactivateAlert[0]){
                    final AlertDialog dialog = AlertDialogHelper.getTextDialog(context, context.getResources().getString
                            (R.string.deactivate_alarm_dialog_title), context.getResources().getString(R.string.deactivate_alarm_dialog_mssg));
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.dialog_postive_button).
                            toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            deactivateAlarm(alarm.getTime(), alarm.getHour(), alarm.getMinute(), alarm.getPendingIntentId());
                            deactivateAlert[0]=false;
                            viewHolder.button.setChecked(false);
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_negative_mssg).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    viewHolder.button.setChecked(true);
                                    deactivateAlert[0]=true;

                                }
                            });
                    dialog.show();
                } else if(b && !deactivateAlert[0]){
                    final AlertDialog dialog = AlertDialogHelper.getTextDialog(context, context.getResources().getString
                            (R.string.reactivate_alarm_dialog_title), context.getResources().getString(R.string.reactive_alarm_dialog_mssg));
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.dialog_postive_button).
                            toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            reActivateAlarm(alarm.getTime(), alarm.getHour(), alarm.getMinute());
                            viewHolder.button.setChecked(true);
                            deactivateAlert[0]=true;
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_negative_mssg).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    viewHolder.button.setChecked(false);
                                    deactivateAlert[0] =false;
                                }
                            });
                    dialog.show();
                }
            }
        });
        viewHolder.periodText.setText(alarm.getPeriod());
        viewHolder.sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){

                            case R.id.delete_alarm:
                                realmController = RealmController.with(view.getContext());
                                if(alarm.isActivated()){
                                    deactivateAlarm(alarm.getTime(), alarm.getHour(), alarm.getMinute(), alarm.getPendingIntentId());
                                }
                                realmController.deleteAlarm(alarm.getTime(), alarm.getPeriod());
                                list.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, list.size());
                                if(list.size() == 0){
                                    basicCallback.callBack(2);
                                }
                                return true;

                            case R.id.preview_alarm:
                                Intent intent = new Intent(view.getContext(), DismissAlarmActivity.class);
                                intent.putExtra("preview", true);
                                SharedPreferences.Editor editor = SP.getEditor();
                                if(SP.getString("previewMode", "off").equals("off")){
                                    editor.putString("previewMode", "on");
                                    editor.commit();
                                }
                                view.getContext().startActivity(intent);
                                return true;

                            case R.id.edit_alarm:
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
                                Log.i("oaoaoaoaoaoajuioo", String.valueOf(alarm.getHour()));
                                view.getContext().startActivity(intent1);
                                return true;

                        }
                        return false;
                    }
                });
                inflater.inflate(R.menu.alarm_item_view_popup, popupMenu.getMenu());
                if(!SP.getBoolean(context.getResources().getString(R.string.edit_saved_alarm_action_mssg), true)){
                    popupMenu.getMenu().findItem(R.id.edit_alarm).setVisible(false);
                } else {
                    popupMenu.getMenu().findItem(R.id.edit_alarm).setVisible(true);
                }
                popupMenu.show();
            }
        });
    }

    private void deactivateAlarm(String alarmTime, int hour, int min, int pendingIntentId){
        realmController = RealmController.with(activity);
        realmController.deactivateAlarm(alarmTime);
    }

    private void reActivateAlarm(String alarmTime, int hour, int min){
        realmController = RealmController.with(activity);
        realmController.reActivateAlarm(alarmTime);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        if(calendar.before(now)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(context, AlarmReceiver.class);
        final int pendingIntentId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
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
        private ImageView sideMenu;
        //private TextView doysText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            periodText = itemView.findViewById(R.id.period);
            daysText = itemView.findViewById(R.id.days);
            button = itemView.findViewById(R.id.button);
            timeText = itemView.findViewById(R.id.timetextalarm);
            sideMenu = itemView.findViewById(R.id.side_menu);
        }
    }
}
