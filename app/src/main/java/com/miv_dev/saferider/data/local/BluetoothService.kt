package com.miv_dev.saferider.data.local

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.miv_dev.saferider.core.services.BLEService
import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothPeripheral
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


interface BluetoothService {

    fun disconnect()
    fun connect(mac: String)
    fun scan(): Flow<Scan>

}

class BluetoothServiceImpl @Inject constructor(
    private val context: Context,
) : BluetoothService {


    private val central by lazy {
        BluetoothCentralManager(context)
    }


    private val scannedDevices = mutableListOf<ScanResult>()



    override fun scan(): Flow<Scan> = channelFlow {
        launch {
            delay(5_000)
            central.stopScan()

        }
        central.scanForPeripherals({ peripheral, scanResult ->
            launch {
                ScanResult(
                    peripheral, scanResult.device
                ).also { device ->
                    if (!scannedDevices.contains(device)) {
                        scannedDevices.add(device)
                        send(Scan.Found(device))
                    }
                }


            }
        }) {
            launch {
                send(Scan.Error(Throwable(it.name)))
            }
        }
    }

    override fun disconnect() {
        Intent(context, BLEService::class.java).also { intent ->
            context.stopService(intent)
        }
    }

    override fun connect(mac: String) {
        Intent(context, BLEService::class.java).also { intent ->
            intent.action = BLEService.ACTION_CONNECT_DEVICE
            intent.putExtra(BLEService.EXTRA_DEVICE_MAC, mac)

            context.startForegroundService(intent)
        }

        context.registerReceiver(object : BroadcastReceiver() {
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


sealed class Scan {
    class Found(val result: ScanResult): Scan()
    class Error(val e: Throwable) : Scan()
}

data class ScanResult(
    val peripheral: BluetoothPeripheral,
    val device: BluetoothDevice
)
