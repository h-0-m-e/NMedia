package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30,TimeUnit.SECONDS)
        .build()

    private val typeToken = object: TypeToken<List<Post>>(){}

    companion object{
        private val BASE_URL = "http://192.168.219.237:9999"
        private val jsonType = "application/json".toMediaType()
    }
    override fun getAll():List<Post> {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let{requireNotNull(it.body?.string()) {"body is null"} }
            .let {gson.fromJson(it,typeToken)}
    }

    override fun likeById(post: Post): Post {
        val id = post.id
        val request: Request = if (post.likedByMe) {
            Request.Builder()
                .delete("".toRequestBody())
        } else {
            Request.Builder()
                .post("".toRequestBody())
        }
                .url("${BASE_URL}/api/slow/posts/${id}/likes")
                .build()


        return client.newCall(request)
            .execute()
            .let{requireNotNull(it.body?.string()) {"body is null"} }
            .let {gson.fromJson(it, Post::class.java)}
    }



    override fun shareById(id: Long) {

    }

    override fun save(post: Post) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

}
