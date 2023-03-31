package com.miv_dev.saferider.data

import com.miv_dev.saferider.data.local.BluetoothService
import com.miv_dev.saferider.data.local.Scan
import com.miv_dev.saferider.domain.Repository
import com.miv_dev.saferider.domain.entities.Device
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val bluetoothService: BluetoothService
) : Repository {


    override fun connect(peripheralAddress: String) = bluetoothService.connect(peripheralAddress)


    override fun scanDevice(): Flow<ScanState> = flow {
        delay(1250)
        bluetoothService.scan()
            .catch {
                emit(ScanError(it))
            }
            .collect { scan ->

                when (scan) {
                    is Scan.Error -> {
                        ScanError(scan.e)
                    }

                    is Scan.Found -> {
                        try {
                            with(scan.result.device) {
                                if (name != null) {
                                    emit(
                                        FoundedDevice(
                                            Device(
                                                name,
                                                scan.result.peripheral.address,
                                                type
                                            )
                                        )
                                    )
                                }
                            }
                        } catch (e: SecurityException) {
                            emit(ScanError(e))
                        }
                    }
                }
            }
    }

    companion object {
        const val TAG = "BleRepositoryImpl"
    }
}

sealed class ScanState

class ScanError(val e: Throwable) : ScanState()
class FoundedDevice(val device: Device) : ScanState()

