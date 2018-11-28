package com.rocky.contactfetcher;
// using this class

//permissionGranted = PermissionUtil.getPermission(activity, PermissionUtil.Permissions.READ_PHONE_STATE,
//        PermissionUtil.PermissionCode.READ_PHONE_STATE,
//        PermissionUtil.PermissionMessage.READ_PHONE_STATE,
//        ResourceUtils.getString(R.string.phone_state_permission_message));

//
//@Override
//public void onRequestPermissionsResult(int requestCode,@NonNull String[]permissions,@NonNull int[]grantResults){
//        switch(requestCode){
//        case RunTimePermission.PermissionCode.READ_PHONE_STATE:
//        if(grantResults.length>0&&grantResults[0]==PackageManager
//        .PERMISSION_GRANTED){
//        // do code after permission is given
//        }
//        break;
//        }
//        }

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class PermissionUtil {
    public static boolean getPermission(final Activity activity, final String permission, final
    int permissionCode, String permissionDialogTitle, String permissionDialogMessage) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                    alertBuilder.setCancelable(true);
                    if (TextUtils.isEmpty(permissionDialogMessage)) {
                        alertBuilder.setMessage(permissionDialogTitle);
                    } else {
                        alertBuilder.setTitle(permissionDialogTitle);
                        alertBuilder.setMessage(permissionDialogMessage);
                    }
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode);
                        }
                    });

                    final AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    activity.requestPermissions(new String[]{permission},
                            permissionCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean getPermission(final Fragment fragment, final String permission, final
    int permissionCode, String permissionDialogTitle, String permissionDialogMessage) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager
                    .PERMISSION_GRANTED) {
                if (fragment.shouldShowRequestPermissionRationale(permission)) {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(fragment.getContext());
                    alertBuilder.setCancelable(true);
                    if (TextUtils.isEmpty(permissionDialogMessage)) {
                        alertBuilder.setMessage(permissionDialogTitle);
                    } else {
                        alertBuilder.setTitle(permissionDialogTitle);
                        alertBuilder.setMessage(permissionDialogMessage);
                    }
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragment.requestPermissions(new String[]{permission}, permissionCode);
                        }
                    });

                    final AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    fragment.requestPermissions(new String[]{permission},
                            permissionCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return currentAPIVersion < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public interface Permissions {
        String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    }

    public interface PermissionCode {
        int READ_CONTACTS = 1002;
    }
}
