package com.miv_dev.saferider.data.local

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.miv_dev.saferider.core.ScanError
import com.miv_dev.saferider.core.services.BLEService
import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothPeripheral
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


interface BluetoothService {

    val scanning: SharedFlow<Result<BluetoothDevice>>
    fun startScan()
    fun stopScan()
    fun disconnect()
    fun connect(mac: String)

}

class BluetoothServiceImpl @Inject constructor(
    private val context: Context,
) : BluetoothService {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val central by lazy {
        BluetoothCentralManager(context)
    }


    private val _scanning = MutableSharedFlow<Result<BluetoothDevice>>()
    override val scanning = _scanning.asSharedFlow()

    private val scannedDevices = mutableListOf<Device>()

    override fun startScan() {
        scannedDevices.clear()
        try {
            central.scanForPeripherals({ peripheral, scanResult ->
                scope.launch {
                    val device = Device(
                        peripheral, scanResult.device
                    )

                    if (!scannedDevices.contains(device)) {
                        scannedDevices.add(device)
                        _scanning.emit(Result.success(scanResult.device))
                    }
                }
            }) {
                scope.launch {
                    _scanning.emit(Result.failure(ScanError(it.name)))
                }
            }
        } catch (e: SecurityException) {
            scope.launch {
                _scanning.emit(
                    Result.failure(
                        Throwable(
                            "Permission Denied!"
                        )
                    )
                )
            }
        }


    }

    override fun stopScan() = central.stopScan()
    override fun disconnect() {
        Intent(context, BLEService::class.java).also { intent ->
            context.stopService(intent)
        }
    }

    override fun connect(mac:String) {
        Intent(context, BLEService::class.java).also { intent ->
            intent.action = BLEService.ACTION_CONNECT_DEVICE
            intent.putExtra(BLEService.EXTRA_DEVICE_MAC, mac)

            context.startForegroundService(intent)
        }

        context.registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    val btLevel = it.getIntExtra(BLEService.EXTRA_FW_BATTERY_LEVEL, -1)
                    println("Battery lvl = $btLevel")
                }
            }

        }, IntentFilter(BLEService.ACTION_BATTERY_LEVEL))
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    val isConnected = it.getBooleanExtra(BLEService.EXTRA_IS_CONNECTED, false)

                    println("Device connection is $isConnected")
                }
            }

        }, IntentFilter(BLEService.ACTION_CONNECTION_STATE))
    }


    companion object {
        const val TAG = "BluetoothServiceImpl"
    }
}


data class Device(
    val peripheral: BluetoothPeripheral,
    val info: BluetoothDevice
)
