package com.miv_dev.saferider.di

import android.content.Context
import com.miv_dev.saferider.data.RepositoryImpl
import com.miv_dev.saferider.data.local.BluetoothService
import com.miv_dev.saferider.data.local.BluetoothServiceImpl
import com.miv_dev.saferider.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    companion object {
        @ApplicationScope
        @Provides
        fun provideBluetoothService(context: Context): BluetoothService = BluetoothServiceImpl(context)
    }
}
