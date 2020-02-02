package me.detj.timelapse.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;

import me.detj.timelapse.MainActivity;
import me.detj.timelapse.R;

public class SettingsController implements View.OnClickListener,  DialogInterface.OnClickListener, DialogInterface.OnShowListener {

    private static final String PREFERENCE_KEY = "settings";

    private static final Gson gson = new Gson();

    private final MainActivity activity;
    private final Button settingsButton;

    public SettingsController(MainActivity activity, Button settingsButton) {
        this.activity = activity;
        this.settingsButton = settingsButton;
        settingsButton.setOnClickListener(this);
    }

    public TimeLapseSettings load() {
        try {
            SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
            String json = prefs.getString(PREFERENCE_KEY, "");
            return gson.fromJson(json, TimeLapseSettings.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void save(TimeLapseSettings settings) {
        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
        prefs.edit()
                .putString(PREFERENCE_KEY, gson.toJson(settings))
                .apply();
    }

    //On settings button click
    @Override
    public void onClick(View v) {
        SettingsDialogFragment dialog = new SettingsDialogFragment(activity, this, this);
        dialog.show(activity.getSupportFragmentManager(), SettingsDialogFragment.TAG);
    }

    //On settings dialog button click
    @Override
    public void onClick(DialogInterface dialogInterface, int result) {
        if(result == Dialog.BUTTON_POSITIVE) {
            Dialog dialog = (Dialog) dialogInterface;
            TimeLapseSettings settings = extractSettings(dialog);
            save(settings);
        }
    }

    private TimeLapseSettings extractSettings(Dialog dialog) {
        EditText fieldHostname = dialog.findViewById(R.id.field_hostname);
        EditText fieldUsername = dialog.findViewById(R.id.field_username);
        EditText fieldPassword = dialog.findViewById(R.id.field_password);
        EditText fieldDirectory = dialog.findViewById(R.id.field_directory);
        EditText fieldInterval = dialog.findViewById(R.id.field_interval);

        String hostname = fieldHostname.getText().toString();
        String username = fieldUsername.getText().toString();
        String password = fieldPassword.getText().toString();
        String directory = fieldDirectory.getText().toString();
        Integer interval = Ints.tryParse(fieldInterval.getText().toString());

        return new TimeLapseSettings(hostname, username, password, directory, interval);
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Dialog dialog = (Dialog) dialogInterface;
        fillSavedSettings(dialog);
    }

    private void fillSavedSettings(Dialog dialog) {
        TimeLapseSettings settings = load();
        if (settings != null) {
            EditText fieldHostname = dialog.findViewById(R.id.field_hostname);
            EditText fieldUsername = dialog.findViewById(R.id.field_username);
            EditText fieldPassword = dialog.findViewById(R.id.field_password);
            EditText fieldDirectory = dialog.findViewById(R.id.field_directory);
            EditText fieldInterval = dialog.findViewById(R.id.field_interval);

            fieldHostname.setText(settings.getHostname());
            fieldUsername.setText(settings.getUsername());
            fieldPassword.setText(settings.getPassword());
            fieldDirectory.setText(settings.getDirectory());
            fieldInterval.setText(String.valueOf(settings.getIntervalMinutes()));
        }
    }

    public void enableButton() {
        settingsButton.setEnabled(true);
    }

    public void disableButton() {
        settingsButton.setEnabled(false);
    }
}
