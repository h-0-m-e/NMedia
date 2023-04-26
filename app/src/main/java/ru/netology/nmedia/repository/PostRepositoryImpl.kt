package ru.netology.nmedia.repository


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import kotlin.random.Random

class PostRepositoryImpl : PostRepository {
//This variable can be true for simulate 2xx response codes and false to simulate others
    private var fakeResponseCode = Random.nextBoolean()

    override fun getAll(postsCallback: GeneralCallback<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object: Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                        postsCallback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }
            })
    }

    override fun likeById(id: Long, postsCallback: GeneralCallback<Post>) {

        PostApi.service.likeById(id)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    if(fakeResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    fakeResponseCode=!fakeResponseCode
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }

            })
    }

    override fun unlikeById(id: Long, postsCallback: GeneralCallback<Post>) {

        PostApi.service.unlikeById(id)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    if(fakeResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    fakeResponseCode=!fakeResponseCode
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }

            })
    }



    override fun shareById(id: Long) {
    }

    override fun save(post: Post, postsCallback: GeneralCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    if(fakeResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    fakeResponseCode=!fakeResponseCode
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }

            })
    }

    override fun removeById(id: Long,postsCallback: GeneralCallback<Unit>) {

        PostApi.service.removeById(id)
            .enqueue(object: Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    if(fakeResponseCode) {
                        postsCallback.onSuccess(Unit)
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    fakeResponseCode=!fakeResponseCode
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }

            })
    }

}
