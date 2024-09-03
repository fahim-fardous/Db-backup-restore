package com.example.backuprestore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Data)

    @Query("SELECT * FROM data")
    suspend fun getAll(): List<Data>

    @Query("DELETE FROM data")
    suspend fun deleteAll()


}