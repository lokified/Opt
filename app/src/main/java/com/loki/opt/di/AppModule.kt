package com.loki.opt.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.loki.opt.data.database.OptDatabase
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.data.repository.OptRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOptDatabase(app: Application): OptDatabase {

        return Room.databaseBuilder(
            app,
            OptDatabase::class.java,
            OptDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideScheduleDao(optDatabase: OptDatabase): OptRepository {
        return OptRepositoryImpl(optDatabase.optDao)
    }

    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext context: Context): Context {
        return context
    }
}