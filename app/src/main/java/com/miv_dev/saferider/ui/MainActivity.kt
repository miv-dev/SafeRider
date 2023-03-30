package com.miv_dev.saferider.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.miv_dev.saferider.ui.screens.ScanScreen
import com.miv_dev.saferider.ui.theme.SafeRiderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeRiderTheme {
                // A surface container using the 'background' color from the theme
                val bleScanPermissionState = rememberPermissionState(
                    Manifest.permission.BLUETOOTH_SCAN
                )

                if (bleScanPermissionState.status.isGranted) {
                    ScanScreen()
                } else {
                    Column {
                        val textToShow =
                            if (bleScanPermissionState.status.shouldShowRationale) {
                                // If the user has denied the permission but the rationale can be shown,
                                // then gently explain why the app requires this permission
                                "The camera is important for this app. Please grant the permission."
                            } else {
                                // If it's the first time the user lands on this feature, or the user
                                // doesn't want to be asked again for this permission, explain that the
                                // permission is required
                                "Camera permission required for this feature to be available. " +
                                        "Please grant the permission"
                            }
                        Text(textToShow)
                        Button(onClick = {
                            bleScanPermissionState.launchPermissionRequest()
                        }) {
                            Text("Request permission")
                        }
                    }
                }


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeRiderTheme {
    }
}
