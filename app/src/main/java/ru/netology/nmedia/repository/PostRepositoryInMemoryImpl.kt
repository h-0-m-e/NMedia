package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepositoryInMemoryImpl : PostRepository {

    private var nextId = 1L

    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Как сдавать задачи:\n" +
                    "1. Откройте ваш проект из предыдущего ДЗ.\n" +
                    "2. Сделайте необходимые коммиты.\n" +
                    "3. Сделайте push. Убедитесь, что ваш код появился на GitHub.\n" +
                    "4. Ссылку на ваш проект отправьте в личном кабинете на сайте netology.ru.\n" +
                    "5. Задачи, отмеченные как необязательные, можно не сдавать. Это не повлияет" +
                    " на получение зачёта. В этом ДЗ все задачи обязательные.",
            published = "24 января в 16:42",
            likedByMe = false,
            sharedByMe = false,
            likes = 95,
            shares = 8,
            views = 298,
            video = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Решитесь на большее!\n" + "\n" +
                    "Вам есть что показать этому миру. Позвольте себе ставить большие цели, а" +
                    " навыки и знания дадим мы. Для этого у нас есть все инструменты.",
            published = "21 января в 12:31",
            likedByMe = false,
            sharedByMe = false,
            likes = 12,
            shares = 1,
            views = 128
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Выберите вектор развития!\n" + "\n" +
                    "С нами вы можете получить новую профессию, освоить навыки для развития " +
                    "карьеры или перенастроить свой бизнес. Выбирайте подходящую из " +
                    "более 80 программ.",
            published = "20 января в 14:24",
            likedByMe = false,
            sharedByMe = false,
            likes = 64,
            shares = 0,
            views = 210,
            video = "https://www.youtube.com/watch?v=b6Ppp5902Yg"
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Мы создали комфортную среду обучения, чтобы у вас всегда была мотивация" +
                    " двигаться вперёд.\n" +
                    "Учитесь, практикуйтесь и применяйте знания сразу в работе.",
            published = "18 января в 21:02",
            likedByMe = false,
            sharedByMe = false,
            likes = 71,
            shares = 4,
            views = 249,
            video = "https://www.youtube.com/watch?v=jNQXAC9IVRw"
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Присоединяйтесь к тем, кто уже встал на путь роста: делитесь открытиями," +
                    " обменивайтесь опытом, вдохновляйтесь и получайте поддержку единомышленников",
            published = "16 января в 21:32",
            likedByMe = false,
            sharedByMe = false,
            likes = 9,
            shares = 1,
            views = 19
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Всем привет, меня зовут Саша, я диктор канала «Мастерская Настроения»",
            published = "15 января в 02:32",
            likedByMe = false,
            sharedByMe = false,
            likes = 96,
            shares = 9,
            views = 170
        ),
        Post(
            id = nextId++,
            author = "Нетология. Интернет-университет который смог",
            content = "Всем привет, меня зовут Саша",
            published = "14 января в 22:32",
            likedByMe = false,
            sharedByMe = false,
            likes = 996,
            shares = 99,
            views = 1498
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1,
                likedByMe = !it.likedByMe
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                shares = it.shares + 1,
                sharedByMe = true
            )
        }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {


            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    sharedByMe = false,
                    published = "now"
                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}
