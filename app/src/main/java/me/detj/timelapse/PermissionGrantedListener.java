package me.detj.timelapse;

import java.util.Collection;

public interface PermissionGrantedListener {

    void onPermissionGranted(boolean granted);

    Collection<String> getListeningPermissions();
}
