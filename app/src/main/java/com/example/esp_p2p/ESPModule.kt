package com.example.esp_p2p

import android.content.Context
import androidx.room.Room
import com.example.esp_p2p.auth.CredentialManagerAuth
import com.example.esp_p2p.data.retrofit.DrawingAPI
import com.example.esp_p2p.data.retrofit.DynamicHostInterceptor
import com.example.esp_p2p.data.room.AppDatabase
import com.example.esp_p2p.data.room.DrawingDAO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
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
    fun provideDynamicHostInterceptor() : DynamicHostInterceptor {
        return DynamicHostInterceptor("esp32.local")
    }

    @Singleton
    @Provides
    fun provideDrawingAPI(hostInterceptor: DynamicHostInterceptor) : DrawingAPI {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(hostInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://${hostInterceptor.hostname}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(DrawingAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDrawingFirestore(): FirebaseFirestore{
        return Firebase.firestore
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher() : CoroutineContext = Dispatchers.IO

}
