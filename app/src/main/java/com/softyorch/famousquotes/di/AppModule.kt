package com.softyorch.famousquotes.di

import android.content.Context
import com.softyorch.famousquotes.core.ISend
import com.softyorch.famousquotes.core.SendImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun providesSendInterface(
        @ApplicationContext context: Context,
        dispatcherDefault: CoroutineDispatcher,
    ): ISend = SendImpl(context, dispatcherDefault)
}
