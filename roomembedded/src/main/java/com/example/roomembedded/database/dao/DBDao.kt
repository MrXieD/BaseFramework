package com.example.roomembedded.database.dao

import androidx.room.*
import com.example.roomembedded.database.table.Animal
import com.example.roomembedded.database.table.DataConverters
import com.example.roomembedded.database.table.Pepole
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/6/9
@desription:
 */
@Dao
abstract class DBDao {


    @Query("SELECT * FROM pepole")
    abstract fun getAllPepole(): Flow<List<Pepole>>

    @Insert
    abstract suspend fun insertPepole(list: List<Pepole>): List<Long>


    @Transaction
    open suspend fun clearData() {
        clearPepole()
        clearAnimal()
        clearAutoIncClomn()
    }

    @Query("DELETE  FROM pepole")
    abstract suspend fun clearPepole()

    @Query("DELETE  FROM animal")
    abstract suspend fun clearAnimal()

    /**
     * 删除自增记录，不然即时清空了数据，以后填充的数据id不会从0开始
     * https://blog.csdn.net/aiynmimi/article/details/50999625
     */
    @Query("DELETE FROM sqlite_sequence")
    abstract suspend fun clearAutoIncClomn()


    @Insert
    abstract suspend fun insertAnimal(list: List<Animal>): List<Long>

    @Query("SELECT * FROM animal")
    abstract fun getAllAnimal(): Flow<List<Animal>>


}
