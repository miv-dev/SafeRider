package com.miv_dev.saferider.domain.usecases

import com.miv_dev.saferider.data.ScanState
import com.miv_dev.saferider.domain.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanDeviceUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<ScanState> {
        return  repository.scanDevice()
    }

}
