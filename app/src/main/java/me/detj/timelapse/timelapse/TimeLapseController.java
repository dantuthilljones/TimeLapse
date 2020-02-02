package me.detj.timelapse.timelapse;

import android.view.View;
import android.widget.Button;

import me.detj.timelapse.R;
import me.detj.timelapse.camera.CameraController;
import me.detj.timelapse.settings.SettingsController;
import me.detj.timelapse.settings.TimeLapseSettings;
import me.detj.timelapse.storage.SFTPStorage;
import me.detj.timelapse.timer.IntervalTimer;

public class TimeLapseController implements View.OnClickListener {

    private static final int RUNNING = 0;
    private static final int WAITING = 1;

    private final CameraController cameraController;
    private final SettingsController settingsController;
    private final SFTPStorage storage;
    private final Button startStopButton;

    private int status;
    private IntervalTimer timer;

    public TimeLapseController(Button startStopButton,
                               CameraController cameraController,
                               SettingsController settingsController,
                               SFTPStorage storage) {
        this.cameraController = cameraController;
        this.settingsController = settingsController;
        this.storage = storage;
        this.startStopButton = startStopButton;

        this.status = WAITING;
        startStopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (status == RUNNING) {
            stopTimeLapse();
            settingsController.enableButton();
        } else if (status == WAITING) {
            TimeLapseSettings settings = settingsController.load();
            if (storageIsWorking(settings)) {
                settingsController.disableButton();
                startTimeLapse(settings);
            } else {
                showStorageError();
                return;
            }
        }

        status = nextStatus();
        updateButtonText();
    }

    private void startTimeLapse(TimeLapseSettings settings) {
        timer = new IntervalTimer(new TakePhotoTask(cameraController.getTimeLapseCamera()), settings.getIntervalMinutes());
        timer.start();
    }

    private void showStorageError() {
        //TODO
    }

    private boolean storageIsWorking(TimeLapseSettings settings) {
        return storage.validate(settings);
    }

    private void stopTimeLapse() {
        timer.stop();
    }

    private int nextStatus() {
        return status == WAITING ? RUNNING : WAITING;
    }

    private void updateButtonText() {
        if (status == WAITING) {
            startStopButton.setText(R.string.button_start);
        } else if (status == RUNNING) {
            startStopButton.setText(R.string.button_stop);
        }
    }
}
