package me.detj.timelapse.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;

import java.sql.Time;

import me.detj.timelapse.settings.SettingsController;
import me.detj.timelapse.storage.SFTPStorage;

public class TimeLapseCamera extends CameraCaptureSession.CaptureCallback implements ImageReader.OnImageAvailableListener {

    private final ImageReader reader;
    private final SFTPStorage storage;

    private CaptureRequest photoRequest;
    private CameraCaptureSession session;
    private SettingsController settingsController;

    public TimeLapseCamera(ImageReader reader, SFTPStorage storage, SettingsController settingsController) {
        this.reader = reader;
        this.storage = storage;
        this.settingsController = settingsController;
        reader.setOnImageAvailableListener(this, null);
    }

    public String takePhoto() throws CameraAccessException {
        if (session != null) {
            session.capture(photoRequest, this, null);
        }
        return "12345";
    }

    public Surface getSurface() {
        return reader.getSurface();
    }

    public void setSession(CameraCaptureSession session) {
        this.session = session;
    }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);
        System.out.println("CaptureCompleted");
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();

        if (image != null) {
            System.out.println("Image Available and height is " + image.getHeight());
            storage.store(image, settingsController.load());

        } else {
            System.out.println("Image Available nut latest is null");
        }
    }

    public void setPhotoRequest(CaptureRequest photoRequest) {
        this.photoRequest = photoRequest;
    }
}
