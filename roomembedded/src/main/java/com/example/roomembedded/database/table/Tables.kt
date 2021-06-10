package com.example.roomembedded.database.table

import android.util.Log
import androidx.room.*
import com.google.gson.Gson

/**
@author Anthony.H
@date: 2021/6/9
@desription:
Embedded可以一直加下去
 */

@Entity
data class Pepole(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    @Embedded(prefix = "ad_") val adress: Adress?
)

data class Adress(val name: String, @Embedded(prefix = "coordinate_") val coordinate: Coordinate)


data class Coordinate(val name: String, val longitude: Float, val latitude: Float)

/////////////////////////////
@Entity
data class Animal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val animalFood: AnimalFood
)

data class AnimalFood(val name: String, val foodArea: FoodArea)

data class FoodArea(val name: String, val longitude: Float, val latitude: Float)


class DataConverters {
    companion object {
        private const val TAG = "DataConverters"
    }

    @TypeConverter
    fun animalFood2Json(animalFood: AnimalFood): String {
        val jsonFood = Gson().toJson(animalFood, AnimalFood::class.java)
        Log.e(TAG, "animalFood2Json: $jsonFood")
        return jsonFood
    }

    @TypeConverter
    fun json2AnimalFood(jsonFood: String): AnimalFood {
        val animalFood = Gson().fromJson(jsonFood, AnimalFood::class.java)
        return animalFood
    }
}