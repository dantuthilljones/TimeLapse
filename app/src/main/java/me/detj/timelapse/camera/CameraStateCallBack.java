package me.detj.timelapse.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

public class CameraStateCallBack extends CameraDevice.StateCallback {

    private final Surface previewSurface;
    private final Surface photoSurface;
    private final TimeLapseCamera camera;

    public CameraStateCallBack(Surface previewSurface, Surface photoSurface, TimeLapseCamera camera) {
        this.previewSurface = previewSurface;
        this.photoSurface = photoSurface;
        this.camera = camera;
    }

    @Override
    public void onOpened(@NonNull CameraDevice device) {
        try {
            CaptureRequest previewRequest = createPreviewRequest(device);
            CaptureRequest photoRequest = createPhotoRequest(device);

            camera.setPhotoRequest(photoRequest);
            device.createCaptureSession(ImmutableList.of(previewSurface, photoSurface), new CameraSessionCallBack(previewRequest, photoRequest, camera), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CaptureRequest createPreviewRequest(CameraDevice camera) throws CameraAccessException {
        CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        builder.addTarget(previewSurface);
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        return builder.build();
    }

    public CaptureRequest createPhotoRequest(CameraDevice camera) throws CameraAccessException {
        CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        builder.addTarget(photoSurface);
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        return builder.build();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {

    }
}
