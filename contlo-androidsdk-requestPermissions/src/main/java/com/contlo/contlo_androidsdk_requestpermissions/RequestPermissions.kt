package com.contlo.contlo_androidsdk_requestpermissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry


class RequestPermissions(
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry
) {

    private val permissionLauncher: ActivityResultLauncher<String> =
        activityResultRegistry.register(
            "permission",
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, perform the action that requires this permission
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, inform the user or perform a different action
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, perform the action that requires this permission
                Toast.makeText(context, "Permission Already Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission not granted, request the permission
                Toast.makeText(context, "Requesting Permission", Toast.LENGTH_SHORT).show()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For devices running Android 12 and lower, permission is granted at installation time
            Toast.makeText(context, "Android Level = 12 or lower -> Permission granted at runtime", Toast.LENGTH_SHORT).show()
        }

    }
}
