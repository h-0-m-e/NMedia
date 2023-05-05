package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.enum.AttachmentType

@Entity
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val url: String,
    val description: String?,
    val type: AttachmentType
) {
    fun toDto() = Attachment( url, description, type )

    companion object {
        fun fromDto(dto: Attachment) =
            AttachmentEntity( dto.url, dto.description, dto.type )

    }
}
