package me.detj.timelapse.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;

import androidx.annotation.NonNull;

public class CameraSessionCallBack extends CameraCaptureSession.StateCallback {

    private final CaptureRequest previewRequest;
    private final CaptureRequest photoRequest;
    private final TimeLapseCamera camera;

    public CameraSessionCallBack(CaptureRequest previewRequest, CaptureRequest photoRequest, TimeLapseCamera camera) {
        this.previewRequest = previewRequest;
        this.photoRequest = photoRequest;
        this.camera = camera;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        try {
            camera.setSession(session);
            session.setRepeatingRequest(previewRequest, new CameraCaptureCallBack(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

    }
}
