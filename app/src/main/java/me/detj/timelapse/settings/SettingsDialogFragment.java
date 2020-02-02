package me.detj.timelapse.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import me.detj.timelapse.R;

public class SettingsDialogFragment extends DialogFragment {

    public static final String TAG = "settings_dialog_fragment";

    private final AppCompatActivity activity;
    private final DialogInterface.OnClickListener clickListener;
    private final DialogInterface.OnShowListener showListener;

    public SettingsDialogFragment(AppCompatActivity activity,
                                  DialogInterface.OnClickListener clickListener,
                                  DialogInterface.OnShowListener showListener) {
        this.activity = activity;
        this.clickListener = clickListener;
        this.showListener = showListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = activity.getLayoutInflater();

        Dialog dialog = new AlertDialog.Builder(activity)
                .setView(inflater.inflate(R.layout.settings_dialog, null))
                .setPositiveButton(R.string.button_save_settings, clickListener)
                .setNegativeButton(R.string.button_cancel_settings, clickListener)
                .create();

        dialog.setOnShowListener(showListener);

        return dialog;
    }


}
