package utils;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class CameraHelper {
    public static boolean hasCameraPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static void requestPermission(Activity activity, int permissionRequestId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(activity, "Camera permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionRequestId);
        }
    }
}
