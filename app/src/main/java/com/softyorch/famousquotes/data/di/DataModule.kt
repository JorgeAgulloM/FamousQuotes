package com.softyorch.famousquotes.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.softyorch.famousquotes.core.InternetConnection
import com.softyorch.famousquotes.data.datastore.DatastoreImpl
import com.softyorch.famousquotes.data.defaultDatabase.DefaultDatabaseImpl
import com.softyorch.famousquotes.data.network.AuthServiceImpl
import com.softyorch.famousquotes.data.network.databaseService.DatabaseServiceImpl
import com.softyorch.famousquotes.data.network.StorageServiceImpl
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.AuxFireStoreLists
import com.softyorch.famousquotes.data.network.databaseService.auxFireStore.IAuxFireStoreLists
import com.softyorch.famousquotes.domain.interfaces.IAuthService
import com.softyorch.famousquotes.domain.interfaces.IDatabaseService
import com.softyorch.famousquotes.domain.interfaces.IDatastore
import com.softyorch.famousquotes.domain.interfaces.IDefaultDatabase
import com.softyorch.famousquotes.domain.interfaces.IStorageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesDatabaseService(
        firestore: FirebaseFirestore,
        auxFirebaseLists: IAuxFireStoreLists,
        internetConnection: InternetConnection,
        dispatcherDefault: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): IDatabaseService = DatabaseServiceImpl(
        firestore,
        auxFirebaseLists,
        internetConnection,
        dispatcherDefault,
        context
    )

    @Singleton
    @Provides
    fun providesStorageService(
        storage: FirebaseStorage,
        @ApplicationContext context: Context,
    ): IStorageService = StorageServiceImpl(storage, context)

    @Singleton
    @Provides
    fun providesDatastore(@ApplicationContext context: Context):
            IDatastore = DatastoreImpl(context)

    @Singleton
    @Provides
    fun providesDefaultDatabase(@ApplicationContext context: Context):
            IDefaultDatabase = DefaultDatabaseImpl(context)

    @Singleton
    @Provides
    fun providesAuthService(auth: FirebaseAuth):
            IAuthService = AuthServiceImpl(auth)

    @Singleton
    @Provides
    fun providesAuxFirebaseLists(firestore: FirebaseFirestore): IAuxFireStoreLists =
        AuxFireStoreLists(firestore)
}
