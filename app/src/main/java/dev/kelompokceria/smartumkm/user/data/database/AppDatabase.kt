package dev.kelompokceria.smartumkm.user.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.kelompokceria.smartumkm.user.data.dao.ProductCategoryDao
import dev.kelompokceria.smartumkm.user.data.dao.ProductDao
import dev.kelompokceria.smartumkm.user.data.dao.TransactionDao
import dev.kelompokceria.smartumkm.user.data.dao.UserDao
import dev.kelompokceria.smartumkm.user.data.helper.Converter
import dev.kelompokceria.smartumkm.user.model.Product
import dev.kelompokceria.smartumkm.user.model.ProductCategory
import dev.kelompokceria.smartumkm.user.model.Transaction
import dev.kelompokceria.smartumkm.user.model.User

@Database(entities = [User::class, Product::class, Transaction::class, ProductCategory::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao() : TransactionDao
    abstract fun productCategoryDao() : ProductCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "userdata_db"
                )
                    .fallbackToDestructiveMigration()// Tambahkan callback untuk populasi data
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        fun getDatabase(application: Context): AppDatabase {
            return getInstance(application)
        }
    }

}
