package com.miv_dev.saferider.presentation.scan

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miv_dev.saferider.core.utils.SnackbarVisualsWithError
import com.miv_dev.saferider.presentation.components.ScanningAnimation
import com.miv_dev.saferider.presentation.main.Empty
import com.miv_dev.saferider.presentation.main.Error
import com.miv_dev.saferider.presentation.main.Loading
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(vm: ScanScreenVM = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    val devices: List<BluetoothDevice> = vm.foundDevice

    val uiState by vm.uiState.collectAsState()

    var isScanning by remember { mutableStateOf(true) }

    LaunchedEffect(uiState) {
        launch {
            isScanning = when (uiState) {
                Empty -> false
                is Error -> {
                    snackbarHostState.showSnackbar(SnackbarVisualsWithError((uiState as Error).msg, true))
                    false
                }

                Loading -> true
            }

        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackbarHostState
            ) { data ->
                Snackbar(
                    modifier = Modifier
                        .padding(12.dp),
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Column(
                Modifier.weight(3f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ScanningAnimation(isScanning = isScanning) {
                    vm.scan()
                }
            }


            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (isScanning)"Searching devices..." else "Tap to scan", style = MaterialTheme.typography.headlineSmall)

                Text("Make sure that device is enabled", style = MaterialTheme.typography.bodyMedium)
            }


            Spacer(Modifier.height(16.dp))

            ScannedDevices(Modifier.weight(4f).padding(horizontal = 12.dp), devices, {
                vm.connect(it.address)
            }) {

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScannedDevices(
    modifier: Modifier = Modifier,
    devices: List<BluetoothDevice>,
    onClick: (device: BluetoothDevice) -> Unit,
    onError: (e: String) -> Unit
) {
    val theme = MaterialTheme.colorScheme
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {

        item {
            Text("History")
        }
        items(devices) { device ->
            val name = try {
                device.name
            } catch (e: SecurityException) {
                onError("Couldn't get device name!")
                ""
            }
            ElevatedCard(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                    onClick(device)
                },

            ) {
                ListItem(
                    headlineContent = { Text(name) },
                    trailingContent = {
                        Icon(Icons.Rounded.Link, contentDescription = "Connect")
                    }
                )
            }

        }
    }
}
