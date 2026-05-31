package com.yousefalaa.electronicmuezzin.data.repositories

import androidx.room.*
import com.yousefalaa.electronicmuezzin.data.models.Dhikr
import com.yousefalaa.electronicmuezzin.data.models.DhikrCategory
import com.yousefalaa.electronicmuezzin.data.models.QuranKhatma
import kotlinx.coroutines.flow.Flow

// ========================
// DAOs
// ========================

@Dao
interface DhikrDao {
    @Query("SELECT * FROM adhkar WHERE category = :category")
    fun getByCategory(category: DhikrCategory): Flow<List<Dhikr>>

    @Query("SELECT * FROM adhkar")
    fun getAll(): Flow<List<Dhikr>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adhkar: List<Dhikr>)

    @Query("SELECT COUNT(*) FROM adhkar")
    suspend fun count(): Int
}

@Dao
interface QuranKhatmaDao {
    @Query("SELECT * FROM quran_khatma ORDER BY startDate DESC")
    fun getAll(): Flow<List<QuranKhatma>>

    @Query("SELECT * FROM quran_khatma WHERE isCompleted = 0 ORDER BY startDate DESC LIMIT 1")
    fun getActive(): Flow<QuranKhatma?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(khatma: QuranKhatma): Long

    @Update
    suspend fun update(khatma: QuranKhatma)

    @Delete
    suspend fun delete(khatma: QuranKhatma)
}

// ========================
// Database
// ========================

@Database(
    entities = [Dhikr::class, QuranKhatma::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dhikrDao(): DhikrDao
    abstract fun quranKhatmaDao(): QuranKhatmaDao
}

class Converters {
    @TypeConverter
    fun fromCategory(value: DhikrCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): DhikrCategory = DhikrCategory.valueOf(value)
}
