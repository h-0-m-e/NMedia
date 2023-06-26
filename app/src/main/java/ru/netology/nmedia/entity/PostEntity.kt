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
    val authorId: Long,
    val authorAvatar: String,
    val author: String,
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
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        authorAvatar = authorAvatar,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        sharedByMe = sharedByMe,
        likes = likes,
        shares = shares,
        views = views,
        video = video,
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post, hidden: Boolean) =
            PostEntity(
                id = dto.id,
                authorId = dto.authorId,
                authorAvatar = dto.authorAvatar,
                author = dto.author,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                sharedByMe = dto.sharedByMe,
                likes = dto.likes,
                shares = dto.shares,
                views = dto.views,
                video = dto.video,
                hidden = hidden,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment)
            )

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
