package com.miv_dev.saferider.domain.usecases

import com.miv_dev.saferider.domain.Repository
import javax.inject.Inject

class ConnectUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(peripheralAddress: String) {
        repository.connect(peripheralAddress)
    }

}
