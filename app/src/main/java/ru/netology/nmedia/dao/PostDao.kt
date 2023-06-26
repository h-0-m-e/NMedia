package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.types.AttachmentType

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity  WHERE hidden = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
    fun getAllVisible(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + 1,
        likedByMe = 1
        WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes - 1,
        likedByMe = 0
        WHERE id = :id
        """
    )
    suspend fun unlikeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        shares = shares + 1,
        sharedByMe = 1
        WHERE id = :id
        """
    )
    suspend fun shareById(id: Long)

    @Query("UPDATE PostEntity SET hidden = 0 WHERE hidden = 1")
    suspend fun showAll()

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun countPosts(): Int
}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}

