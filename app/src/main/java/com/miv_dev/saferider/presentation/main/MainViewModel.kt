package com.miv_dev.saferider.presentation.main

import androidx.lifecycle.ViewModel
import com.miv_dev.saferider.data.BleRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class UiState
object Loading : UiState()
object Empty : UiState()
class Error(val msg: String) : UiState()


@HiltViewModel
class MainViewModel @Inject constructor(private val bleRepository: BleRepositoryImpl) : ViewModel() {

}



