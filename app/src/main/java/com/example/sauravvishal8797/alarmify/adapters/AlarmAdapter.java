package com.example.sauravvishal8797.alarmify.adapters;

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

    public AlarmAdapter(ArrayList<Alarm> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Alarm alarm = list.get(i);
        viewHolder.timeText.setText(alarm.getTime());
        viewHolder.periodText.setText(alarm.getPeriod());
        viewHolder.daysText.setText("Mo Tu Thr");
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
