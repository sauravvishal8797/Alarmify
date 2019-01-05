package com.example.sauravvishal8797.alarmify.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.R;
import com.example.sauravvishal8797.alarmify.models.Alarm;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private ArrayList<Alarm> list;
    StringBuilder builder = new StringBuilder();
    private Context context;

    public AlarmAdapter(ArrayList<Alarm> list, Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        StringBuilder builder = new StringBuilder();

        Alarm alarm = list.get(i);
        viewHolder.timeText.setText(alarm.getTime());
        viewHolder.periodText.setText(alarm.getPeriod());
        viewHolder.daysText.setText(alarm.getDays());
        viewHolder.button.setChecked(true);
        viewHolder.periodText.setText(alarm.getPeriod());
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
            periodText = (TextView) itemView.findViewById(R.id.period);
            daysText = (TextView) itemView.findViewById(R.id.days);
            button = (SwitchCompat) itemView.findViewById(R.id.button);
            timeText = (TextView) itemView.findViewById(R.id.timetextalarm);
        }
    }
}
