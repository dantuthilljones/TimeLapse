package me.detj.timelapse.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.util.Size;
import android.view.SurfaceHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import me.detj.timelapse.PermissionGrantedListener;
import me.detj.timelapse.settings.SettingsController;
import me.detj.timelapse.storage.SFTPStorage;

public class CameraController implements SurfaceHolder.Callback, PermissionGrantedListener {

    private final SurfaceHolder previewSurface;
    private final AppCompatActivity activity;
    private final CameraManager cameraManager;
    private final TimeLapseCamera timeLapseCamera;
    private String backCamera;

    public CameraController(AppCompatActivity activity, SurfaceHolder previewSurface, SFTPStorage storage, SettingsController controller) {
        this.previewSurface = previewSurface;
        this.activity = activity;
        this.cameraManager = activity.getSystemService(CameraManager.class);

        try {
            backCamera = getBackCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        this.timeLapseCamera = new TimeLapseCamera(createImageReader(backCamera), storage, controller);

        previewSurface.addCallback(this);
    }

    private ImageReader createImageReader(String backCamera) {
        try {
            Size[] sizes = cameraManager.getCameraCharacteristics(backCamera)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .getOutputSizes(ImageFormat.JPEG);

            Size maxSize = null;
            long maxResolution = 0;

            for(Size size : sizes) {
                long resolution = size.getHeight() * size.getWidth();
                if (resolution > maxResolution) {
                    maxResolution = resolution;
                    maxSize = size;
                }
            }

            return ImageReader.newInstance(maxSize.getWidth(), maxSize.getHeight(), ImageFormat.JPEG, 10);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraPermissionCheck();
        openCamera();
    }

    private void cameraPermissionCheck() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[] {Manifest.permission.CAMERA}, 0);
        }
    }

    private void openCamera() {
        try {
            cameraManager.openCamera(backCamera, new CameraStateCallBack(previewSurface.getSurface(), timeLapseCamera.getSurface(), timeLapseCamera), null);
        } catch (SecurityException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private String getBackCamera() throws CameraAccessException {
        for(String camera : cameraManager.getCameraIdList()) {
            CameraCharacteristics chars = cameraManager.getCameraCharacteristics(camera);
            if (chars.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                return camera;
            }
        }
        return null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPermissionGranted(boolean granted) {
        if (granted) {
            openCamera();
        } else {
            Snackbar.make(activity.getCurrentFocus(), "Camera permissions are required for the app to work", Snackbar.LENGTH_LONG)
                    .setAction("ERROR", null).show();
        }
    }

    @Override
    public Collection<String> getListeningPermissions() {
        return ImmutableList.of(Manifest.permission.CAMERA);
    }

    public TimeLapseCamera getTimeLapseCamera() {
        return timeLapseCamera;
    }
}
