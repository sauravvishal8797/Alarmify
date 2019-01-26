package com.example.sauravvishal8797.alarmify;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AlertDialogHelper {

    public static AlertDialog getTextDialog(Context context, String dialogTitle, String dialogMessage){

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_display_mssg);
        title.setText(dialogTitle);
        message.setText(dialogMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog_Dark);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
