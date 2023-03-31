package com.miv_dev.saferider.di

import androidx.lifecycle.ViewModel
import com.miv_dev.saferider.presentation.scan.ScanScreenVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @IntoMap
    @ViewModelKey(ScanScreenVM::class)
    @Binds
    fun bindScanScreenVM(viewModel: ScanScreenVM): ViewModel
}
