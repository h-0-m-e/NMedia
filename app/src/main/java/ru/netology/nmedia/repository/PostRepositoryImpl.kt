package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30,TimeUnit.SECONDS)
        .build()

    private val typeToken = object: TypeToken<List<Post>>(){}

    companion object{
        private const val BASE_URL = "http://192.168.100.237:9999"
        private val jsonType = "application/json".toMediaType()
    }
    override fun getAll(postsCallback: GeneralCallback<List<Post>>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    postsCallback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        postsCallback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()){"body is null"}
                    postsCallback.onSuccess(gson.fromJson(body, typeToken.type))
                }
            })
    }

    override fun likeById(post: Post, postsCallback: GeneralCallback<Post>) {
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


        client.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    postsCallback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        postsCallback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()){"body is null"}
                    postsCallback.onSuccess(gson.fromJson(body, Post::class.java))
                }
            })
    }



    override fun shareById(id: Long) {
    }

    override fun save(post: Post, postsCallback: GeneralCallback<Post>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    postsCallback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        postsCallback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()){"body is null"}
                    postsCallback.onSuccess(gson.fromJson(body, Post::class.java))
                }
            })
    }

    override fun removeById(id: Long,postsCallback: GeneralCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    postsCallback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        postsCallback.onError(Exception(response.message))
                    }
                    postsCallback.onSuccess(Unit)
                }
            })
    }

}
