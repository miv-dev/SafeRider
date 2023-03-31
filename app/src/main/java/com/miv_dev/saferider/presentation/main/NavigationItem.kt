package com.miv_dev.saferider.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PedalBike
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.miv_dev.saferider.R
import com.miv_dev.saferider.navigation.Screen

sealed class NavigationItem(
    val screen: Screen,
    val titleResId: Int,
    val icon: ImageVector,
){
    object Bike: NavigationItem(
        screen = Screen.Bike,
        titleResId = R.string.navigation_item_bike,
        icon = Icons.Rounded.PedalBike
    )

    object Home: NavigationItem(
        screen = Screen.Home,
        titleResId = R.string.navigation_item_home,
        icon = Icons.Rounded.Home
    )
    object Settings: NavigationItem(
        screen = Screen.Settings,
        titleResId = R.string.navigation_item_settings,
        icon = Icons.Rounded.Settings
    )
}
