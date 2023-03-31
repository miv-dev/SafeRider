package com.miv_dev.saferider.navigation

sealed class Screen(val route: String) {
    object Home : Screen(ROUTE_HOME) // Show device battery lvl, lightning mode
    object Settings : Screen(ROUTE_SETTINGS)
    object Bike : Screen(ROUTE_BIKE)
    object Scan : Screen(ROUTE_SCAN) // Allows the user to scan nearby devices to connect to their bike.

    private companion object {
        const val ROUTE_HOME = "home"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_SCAN = "scan"
        const val ROUTE_BIKE = "bike"
    }
}
