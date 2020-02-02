package me.detj.timelapse.timelapse;

import android.hardware.camera2.CameraAccessException;

import java.io.File;

import me.detj.timelapse.camera.TimeLapseCamera;
import me.detj.timelapse.settings.TimeLapseSettings;
import me.detj.timelapse.storage.SFTPStorage;
import me.detj.timelapse.timer.IntervalTask;

public class TakePhotoTask implements IntervalTask {

    private final TimeLapseCamera timeLapseCamera;

    public TakePhotoTask(TimeLapseCamera timeLapseCamera) {
        this.timeLapseCamera = timeLapseCamera;
    }

    @Override
    public void run() {
        try {
            timeLapseCamera.takePhoto();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
