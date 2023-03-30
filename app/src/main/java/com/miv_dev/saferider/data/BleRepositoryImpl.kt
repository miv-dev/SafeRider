package com.miv_dev.saferider.data

import android.bluetooth.BluetoothDevice
import com.miv_dev.saferider.data.local.BluetoothService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class BleRepositoryImpl @Inject constructor(
    private val bluetoothService: BluetoothService
) {


    private val scope = CoroutineScope(IO)

    private val _scanState = MutableSharedFlow<ScanState>()
    val scanState: SharedFlow<ScanState> = _scanState.asSharedFlow()


    init {
        subscribeForScanningDevices()
    }

    private fun subscribeForScanningDevices() {
        scope.launch {
            bluetoothService.scanning
                .collect { result ->
                    result.fold({ device ->
                        try {
                            if (device.name != null) {
                                _scanState.emit(FoundedDevice(device))
                            }
                        } catch (e: SecurityException) {
                            _scanState.emit(ScanError(e))
                        }
                    }, {
                        _scanState.emit(ScanError(it))
                    })
                }
        }

    }

    fun connect(mac: String) = bluetoothService.connect(mac)

    fun scan() {
        scope.launch {
            launch {
                delay(10_000)
                bluetoothService.stopScan()
                _scanState.emit(NoScanning)
            }
            _scanState.emit(Scanning)
            bluetoothService.startScan()

        }
    }

    companion object {
        const val TAG = "BleRepositoryImpl"
    }
}

sealed class ScanState

class ScanError(val e: Throwable) : ScanState()
class FoundedDevice(val device: BluetoothDevice) : ScanState()

object Scanning : ScanState()
object NoScanning : ScanState()
