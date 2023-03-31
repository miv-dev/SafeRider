package com.miv_dev.saferider.presentation.scan

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miv_dev.saferider.data.FoundedDevice
import com.miv_dev.saferider.data.ScanError
import com.miv_dev.saferider.domain.entities.Device
import com.miv_dev.saferider.domain.usecases.ConnectUseCase
import com.miv_dev.saferider.domain.usecases.ScanDeviceUseCase
import com.miv_dev.saferider.presentation.main.Error
import com.miv_dev.saferider.presentation.main.Initial
import com.miv_dev.saferider.presentation.main.Loading
import com.miv_dev.saferider.presentation.main.UiState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScanScreenVM @Inject constructor(
    private val scanDeviceUseCase: ScanDeviceUseCase,
    private val connectUseCase: ConnectUseCase
) : ViewModel() {


    val foundDevice = mutableStateListOf<Device>()

    private val _uiState = MutableStateFlow<UiState>(Initial)
    val uiState = _uiState.asStateFlow()


    fun scan() {
        viewModelScope.launch(IO) {
            scanDeviceUseCase()
                .onStart {
                    foundDevice.clear()
                    _uiState.value = Loading
                }
                .onCompletion {
                    _uiState.value = Initial
                }
                .catch {
                    _uiState.value = Error(it.message ?: "Unhandled Error")
                }
                .collect { state ->
                    when (state) {
                        is FoundedDevice -> {
                            foundDevice.add(state.device)
                        }

                        is ScanError -> {
                            _uiState.value = Error(state.e.message ?: "Unhandled Error")
                            Log.e(TAG, "scan: ${state.e.suppressed}")
                        }


                    }
                }
        }

    }

    fun connect(device: Device) {
        connectUseCase(device.peripheralAddress)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
