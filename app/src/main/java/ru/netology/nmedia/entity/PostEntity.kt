package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.types.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val sharedByMe: Boolean,
    val likes: Long = 0,
    val shares: Long,
    val views: Long,
    val video: String? = null,
    val hidden: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable?
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likedByMe, sharedByMe,likes,shares,views,video,attachment?.toDto())

    companion object {
        fun fromDto(dto: Post, hidden: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.sharedByMe,
                dto.likes,
                dto.shares,
                dto.views,
                dto.video,
                hidden = hidden,
                AttachmentEmbeddable.fromDto(dto.attachment))

    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean): List<PostEntity> = map {
    PostEntity.fromDto(it, hidden)
}
