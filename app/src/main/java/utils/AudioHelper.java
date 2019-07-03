package utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

public class AudioHelper {
    public static boolean hasAudioPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static void requestPermission(Activity activity, int permissionRequestId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(activity, "audio permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, permissionRequestId);
        }
    }
}
