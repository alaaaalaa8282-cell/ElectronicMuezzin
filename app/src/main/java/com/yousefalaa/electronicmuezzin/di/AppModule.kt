package com.yousefalaa.electronicmuezzin.di

import android.content.Context
import androidx.room.Room
import com.yousefalaa.electronicmuezzin.data.repositories.AppDatabase
import com.yousefalaa.electronicmuezzin.data.repositories.DhikrDao
import com.yousefalaa.electronicmuezzin.data.repositories.QuranKhatmaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "muezzin_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDhikrDao(db: AppDatabase): DhikrDao = db.dhikrDao()

    @Provides
    fun provideQuranKhatmaDao(db: AppDatabase): QuranKhatmaDao = db.quranKhatmaDao()
}
