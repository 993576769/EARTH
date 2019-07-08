package top.addoneseconds.earth1;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class permissionUtil {
    public static boolean isOwnPermisson(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }
    }
}

