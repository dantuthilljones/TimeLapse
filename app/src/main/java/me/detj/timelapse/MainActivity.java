package me.detj.timelapse;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import androidx.appcompat.app.AppCompatActivity;

import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import me.detj.timelapse.camera.TimeLapseCamera;
import me.detj.timelapse.camera.CameraController;
import me.detj.timelapse.settings.SettingsController;
import me.detj.timelapse.storage.SFTPStorage;
import me.detj.timelapse.timelapse.TimeLapseController;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private final Multimap<String, PermissionGrantedListener> permissionListeners;

    public MainActivity() {
        permissionListeners = ArrayListMultimap.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //storage
        SFTPStorage storage = new SFTPStorage();

        //settings Button
        Button settingsButton = findViewById(R.id.button_open_settings);
        SettingsController settingsController = new SettingsController(this, settingsButton);

        //cameras
        SurfaceView surfaceView = findViewById(R.id.camera_feed);
        final CameraController cameraController = new CameraController(
                this, surfaceView.getHolder(), storage, settingsController);
        addPermissionGrantedListener(cameraController);

        //timelapse debug
        Button startStopButton = findViewById(R.id.button_start_stop);
        TimeLapseController timeLapseController = new TimeLapseController(
                startStopButton,
                cameraController,
                settingsController,
                storage);

    }

    public void addPermissionGrantedListener(PermissionGrantedListener listener) {
        for(String permission : listener.getListeningPermissions()) {
            permissionListeners.put(permission, listener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for(int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean granted = grantResults[i] == PERMISSION_GRANTED;
            for(PermissionGrantedListener listener: permissionListeners.get(permission)) {
                listener.onPermissionGranted(granted);
            }
        }
    }

}
