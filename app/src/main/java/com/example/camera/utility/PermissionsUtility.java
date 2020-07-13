package com.example.camera.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Dell on 2/10/2016.
 */
public class PermissionsUtility {
    private static PermissionsUtility permissionsUtility;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 102;
    public static final int MY_PERMISSIONS_REQUEST_AUDIO_RECORD = 103;
    public static final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 104;

    private PermissionsUtility() {
    }

    public static PermissionsUtility getInstance() {
        if (permissionsUtility == null) {
            permissionsUtility = new PermissionsUtility();
        }
        return permissionsUtility;
    }

    public boolean checkCourseLocationPermission(Context context) {
        if (!checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) || !checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COURSE_LOCATION);
            return false;
        }
        return true;
    }

    public boolean checkStoragePermission(Context context) {
        if (!checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public boolean checkCameraPermission(Context context) {
        if (!checkPermission(context, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    public boolean checkAudioRecordPermission(Context context) {
        if (!checkPermission(context, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_AUDIO_RECORD);
            return false;
        }
        return true;
    }

    private boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
