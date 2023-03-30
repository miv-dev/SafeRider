package com.miv_dev.saferider.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.miv_dev.saferider.R
import com.welie.blessed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class BLEService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val channelId = "Wheel connection"

    private val central by lazy {
        BluetoothCentralManager(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: Start server on port:8000")
        val channel = NotificationChannel(channelId, "Safe Rider", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .build()
            .also { notification ->
                startForeground(1, notification);
            }

        central.observeConnectionState { peripheral, state ->
            when (state) {
                ConnectionState.DISCONNECTED -> {
                    Intent().also {
                        it.action = ACTION_CONNECTION_STATE
                        it.putExtra(EXTRA_IS_CONNECTED, false)
                        sendBroadcast(it)
                    }
                }

                ConnectionState.CONNECTED -> {
                    Intent().also {
                        it.action = ACTION_CONNECTION_STATE
                        it.putExtra(EXTRA_IS_CONNECTED, true)
                        sendBroadcast(it)
                    }
                    handlePeripheral(peripheral)

                }

                else -> {}
            }
        }
    }

    private fun responseError(msg: String) {
        val intent = Intent()
        intent.action = ACTION_ERROR
        intent.putExtra("error", msg)
        sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        intent?.let { intent ->
            when (intent.action) {
                ACTION_CONNECT_DEVICE -> {
                    intent.getStringExtra(EXTRA_DEVICE_MAC)?.let {
                        connect(it)
                    }
                }

                else -> responseError("This action doesn't exist")
            }
        }

        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .build()
            .also { notification ->
                startForeground(1, notification);
            }

        return START_NOT_STICKY
    }


    private fun connect(mac: String) {
        println("Start Connection to $mac")
        val peripheral = central.getPeripheral(mac)
        scope.launch {
            try {
                central.connectPeripheral(peripheral)
            } catch (connectionFailed: ConnectionFailedException) {
                responseError("Connection Failed!")
            }
        }

    }

    private fun handlePeripheral(peripheral: BluetoothPeripheral) {
        println("Handle peripheral")

        scope.launch {
            try {
//                val mtu = peripheral.requestMtu(185)
//                println("MTU = $mtu")

//                peripheral.requestConnectionPriority(ConnectionPriority.HIGH)

                val fWBatteryLevel =
                    peripheral.readCharacteristic(BTS_SERVICE_UUID, FW_BATTERY_LEVEL_CHARACTERISTIC_UUID).asUInt8()
                println("FW Battery level = $fWBatteryLevel")

                setupBTSNotifications(peripheral)

            } catch (e: IllegalArgumentException) {
                responseError(e.message ?: "Error in ble service")
            } catch (b: GattException) {
                responseError(b.message ?: "Error in ble service")
            }
        }
    }

    private suspend fun setupBTSNotifications(peripheral: BluetoothPeripheral) {
        peripheral.getCharacteristic(BTS_SERVICE_UUID, FW_BATTERY_LEVEL_CHARACTERISTIC_UUID)?.let { characteristic ->


            peripheral.observe(characteristic) { value ->
                val parser = BluetoothBytesParser(value)
                val value = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
                println("FW battery level = $value")
                Intent().also { intent ->
                    intent.action = ACTION_BATTERY_LEVEL
                    intent.putExtra(EXTRA_FW_BATTERY_LEVEL,value)
                    sendBroadcast(intent)
                }
            }
        }
    }

    companion object {
        private const val TAG = "BLE_SERVICE"

        private val BTS_SERVICE_UUID: UUID = UUID.fromString("1550b866-7e6a-4d50-be9d-e51c863be9a6")
        private val FW_BATTERY_LEVEL_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("094ad1bb3-982b-4f6b-a687-fb21f0fef3a4") // FRONT (MAIN) WHEEL BATTERY LEVEL


        const val ACTION_CONNECT_DEVICE = "CONNECT_DEVICE"
        const val ACTION_CONNECTION_STATE = "CONNECTION_STATE"
        const val ACTION_BATTERY_LEVEL = "BATTERY_LEVEL"

        const val EXTRA_DEVICE_MAC = "DEVICE_MAC"
        const val EXTRA_IS_CONNECTED = "IS_CONNECTED"
        const val EXTRA_FW_BATTERY_LEVEL = "FW_BATTERY_LEVEL"
        const val ACTION_ERROR = "ERROR"
    }
}
