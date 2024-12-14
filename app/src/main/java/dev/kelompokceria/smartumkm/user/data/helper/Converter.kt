package dev.kelompokceria.smartumkm.user.data.helper

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.kelompokceria.smartumkm.user.model.TransactionProduct
import java.util.Date

class Converter {
//    @TypeConverter
//    fun fromDate(date: Date?): Long? {
//        return date?.time
//    }
//
//    @TypeConverter
//    fun toDate(timestamp: Long?): Date? {
//        return timestamp?.let { Date(it) }
//    }


    @TypeConverter
    fun fromTransactionProductList(value: List<TransactionProduct>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTransactionProductList(value: String): List<TransactionProduct> {
        val gson = Gson()
        val type = object : TypeToken<List<TransactionProduct>>() {}.type
        return gson.fromJson(value, type)
    }
}
