package com.example.esp_p2p

import android.content.Context
import androidx.room.Room
import com.example.esp_p2p.data.DrawingLocalDataSource
import com.example.esp_p2p.data.DrawingRepository
import com.example.esp_p2p.data.room.AppDatabase
import com.example.esp_p2p.data.room.DrawingDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ESPModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "esp-p2p.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDrawingDAO(appDatabase: AppDatabase) : DrawingDAO {
        return appDatabase.drawingDAO()
    }

    /*@Singleton
    @Provides
    fun provideDrawingLocalDataSource(drawingDAO: DrawingDAO) : DrawingLocalDataSource {
        return DrawingLocalDataSource(drawingDAO)
    }

    @Singleton
    @Provides
    fun provideDrawingRepository(drawingLocalDataSource: DrawingLocalDataSource, @IoDispatcher dispatcher: CoroutineContext) : DrawingRepository {
        return DrawingRepository(drawingLocalDataSource, dispatcher)
    }*/

    @Provides
    @IoDispatcher
    fun provideIoDispatcher() : CoroutineContext = Dispatchers.IO

}