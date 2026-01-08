package com.gravity.ourmoments.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gravity.ourmoments.data.local.database.converter.DateConverter
import com.gravity.ourmoments.data.model.Comment
import com.gravity.ourmoments.data.model.Notification
import com.gravity.ourmoments.data.model.Post
import com.gravity.ourmoments.data.model.User

@Database(
    entities = [
        User::class,
        Post::class,
        Comment::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // DAOs will be added here when we create them
}