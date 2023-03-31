package com.miv_dev.saferider.domain

import com.miv_dev.saferider.data.ScanState
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun scanDevice(): Flow<ScanState>

    fun connect(peripheralAddress: String)

}
