package com.miv_dev.saferider.di

import android.content.Context
import com.miv_dev.saferider.data.local.BluetoothService
import com.miv_dev.saferider.data.local.BluetoothServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideBluetoothService(@ApplicationContext context: Context): BluetoothService = BluetoothServiceImpl(context)

}
