package dev.kelompokceria.smartumkm.user.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.kelompokceria.smartumkm.user.model.User

@Dao
interface UserDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: User)

    @Query("SELECt * FROM user_table WHERE username = :username")
    suspend fun getUserByName(username: String): User?

}