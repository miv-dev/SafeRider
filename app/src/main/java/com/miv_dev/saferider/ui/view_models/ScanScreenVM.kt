package com.miv_dev.saferider.ui.view_models

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miv_dev.saferider.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanScreenVM @Inject constructor(private val bleRepository: BleRepositoryImpl): ViewModel() {



    val foundDevice = mutableStateListOf<BluetoothDevice>()

    private val _uiState = MutableStateFlow<UiState>(Empty)
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch(IO) {
            bleRepository.scanState.collect { state ->
                when (state) {
                    is FoundedDevice -> {
                        Log.d(TAG, "scan: found device ${state.device}")
                        foundDevice.add(state.device)
                    }

                    is ScanError -> {
                        _uiState.value = Error(state.e.message ?: "Unhandled Error")
                        Log.e(TAG, "scan: ${state.e.suppressed}")
                    }

                    NoScanning -> _uiState.value = Empty

                    Scanning -> _uiState.value = Loading

                }
            }

        }

    }

    fun scan() {
        foundDevice.clear()
        bleRepository.scan()
    }

    fun connect(mac: String){
        bleRepository.connect(mac)
    }
    companion object {
        const val TAG = "MainViewModel"
    }
}
