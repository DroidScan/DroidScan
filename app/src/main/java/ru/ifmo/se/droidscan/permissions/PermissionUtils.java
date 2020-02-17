package ru.ifmo.se.droidscan.permissions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.function.Consumer;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionUtils {
    public static boolean isPermissionGranted(final int permissionGrantResult) {
        return permissionGrantResult == PERMISSION_GRANTED;
    }

    public static boolean hasPermission(final Context context, final String permission) {
        return isPermissionGranted(ContextCompat.checkSelfPermission(context, permission));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void hasPermissions(final Context context, final String[] permissions, final Consumer<String[]> permissionsRequester) {
        final String[] neededPermissions = Arrays.stream(permissions)
                .filter(permission -> !hasPermission(context, permission))
                .toArray(String[]::new);

        if (neededPermissions.length > 0) {
            permissionsRequester.accept(neededPermissions);
        }
    }
}
