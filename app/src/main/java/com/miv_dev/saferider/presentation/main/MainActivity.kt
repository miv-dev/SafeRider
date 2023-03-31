package com.miv_dev.saferider.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.miv_dev.saferider.navigation.AppNavGraph
import com.miv_dev.saferider.navigation.rememberNavigationState
import com.miv_dev.saferider.ui.theme.SafeRiderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeRiderTheme {
                MainScreen()


//                // A surface container using the 'background' color from the theme
//                val bleScanPermissionState = rememberPermissionState(
//                    Manifest.permission.BLUETOOTH_SCAN
//                )
//
//                if (bleScanPermissionState.status.isGranted) {
//                    ScanScreen()
//                } else {
//                    Column {
//                        val textToShow =
//                            if (bleScanPermissionState.status.shouldShowRationale) {
//                                // If the user has denied the permission but the rationale can be shown,
//                                // then gently explain why the app requires this permission
//                                "The camera is important for this app. Please grant the permission."
//                            } else {
//                                // If it's the first time the user lands on this feature, or the user
//                                // doesn't want to be asked again for this permission, explain that the
//                                // permission is required
//                                "Camera permission required for this feature to be available. " +
//                                        "Please grant the permission"
//                            }
//                        Text(textToShow)
//                        Button(onClick = {
//                            bleScanPermissionState.launchPermissionRequest()
//                        }) {
//                            Text("Request permission")
//                        }
//                    }
//                }
//

            }
        }
    }
}

@Composable
fun MainScreen() {
    val navigatorState = rememberNavigationState()

    Scaffold(
        bottomBar = {
            BottomAppBar(

            ) {
                val navBackStackEntry by navigatorState.navHostController.currentBackStackEntryAsState()

                val items = listOf(
                    NavigationItem.Bike,
                    NavigationItem.Home,
                    NavigationItem.Settings
                )
                items.forEach { item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it.route == item.screen.route
                    } ?: false

                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = false,
                        onClick = {
                            if (!selected) {
                                navigatorState.navigateTo(item.screen.route)
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = null)
                        },

                        label = {
                            Text(text = stringResource(item.titleResId))
                        }
                    )

                }


            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navHostController = navigatorState.navHostController,
            mainScreenContent = {
                Column(Modifier.padding(paddingValues)) {
                    Text("main")
                }
            },
            scanScreenContent = {
                Column(Modifier.padding(paddingValues)) {
                    Text("scan")

                }
            },
            bikeScreenContent = {
                Column(Modifier.padding(paddingValues)) {
                    Text("bike")

                }
            },
            settingsScreenContent = {
                Column(Modifier.padding(paddingValues)) {
                    Text("settings")
                }
            }
        )
    }
}
