package com.miv_dev.saferider.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    mainScreenContent: @Composable () -> Unit,
    scanScreenContent: @Composable () -> Unit,
    bikeScreenContent: @Composable () -> Unit,
    settingsScreenContent: @Composable () -> Unit,

    ) {
    NavHost(navHostController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            mainScreenContent()
        }

        composable(Screen.Bike.route){
            bikeScreenContent()
        }

        composable(Screen.Settings.route){
            settingsScreenContent()
        }

        composable(Screen.Scan.route){
            scanScreenContent()
        }
    }

}
