package com.example.esp_p2p

import android.content.Context
import androidx.room.Room
import com.example.esp_p2p.data.retrofit.DrawingAPI
import com.example.esp_p2p.data.room.AppDatabase
import com.example.esp_p2p.data.room.DrawingDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun provideDrawingAPI() : DrawingAPI {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.0.105/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(DrawingAPI::class.java)
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