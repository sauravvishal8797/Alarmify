package com.example.sauravvishal8797.alarmify.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sauravvishal8797.alarmify.R;

import java.util.ArrayList;

public class repeatAlarmAdapter extends RecyclerView.Adapter<repeatAlarmAdapter.ViewHolder>{

    private ArrayList<String> daysList;
    private Context context;

    public repeatAlarmAdapter(ArrayList<String> daysList, Context context) {
        this.daysList=daysList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repeat_dialog_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String day = daysList.get(i);
        viewHolder.day.setText(day);
        viewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView day;
        private CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            checkBox = (CheckBox) itemView.findViewById(R.id.hceck);
        }
    }
}
