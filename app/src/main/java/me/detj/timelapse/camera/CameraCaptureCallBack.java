package me.detj.timelapse.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;

public class CameraCaptureCallBack extends CameraCaptureSession.CaptureCallback {

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);
        //Toast.makeText(MainActivity.this, "Saved image", Toast.LENGTH_SHORT).show();
        //createCameraPreview();
    }
}
