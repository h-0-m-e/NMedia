package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
        Post(
            id = 7,
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
            likes = 95,
            shares = 8
        ),
        Post(
            id = 6,
            author = "Нетология. Интернет-университет который смог",
            content = "Решитесь на большее!\n" + "\n" +
                    "Вам есть что показать этому миру. Позвольте себе ставить большие цели, а" +
                    " навыки и знания дадим мы. Для этого у нас есть все инструменты.",
            published = "21 января в 12:31",
            likedByMe = false,
            likes = 12,
            shares = 1
        ),
        Post(
            id = 5,
            author = "Нетология. Интернет-университет который смог",
            content = "Выберите вектор развития!\n" + "\n" +
                    "С нами вы можете получить новую профессию, освоить навыки для развития " +
                    "карьеры или перенастроить свой бизнес. Выбирайте подходящую из " +
                    "более 80 программ.",
            published = "20 января в 14:24",
            likedByMe = false,
            likes = 64,
            shares = 0
        ),
        Post(
            id = 4,
            author = "Нетология. Интернет-университет который смог",
            content = "Мы создали комфортную среду обучения, чтобы у вас всегда была мотивация" +
                    " двигаться вперёд.\n" +
                    "Учитесь, практикуйтесь и применяйте знания сразу в работе.",
            published = "18 января в 21:02",
            likedByMe = false,
            likes = 71,
            shares = 4
        ),
        Post(
            id = 3,
            author = "Нетология. Интернет-университет который смог",
            content = "Присоединяйтесь к тем, кто уже встал на путь роста: делитесь открытиями," +
                    " обменивайтесь опытом, вдохновляйтесь и получайте поддержку единомышленников",
            published = "16 января в 21:32",
            likedByMe = false,
            likes = 9,
            shares = 1
        ),
        Post(
            id = 2,
            author = "Нетология. Интернет-университет который смог",
            content = "Всем привет, меня зовут Саша, я диктор канала «Мастерская Настроения»",
            published = "15 января в 02:32",
            likedByMe = false,
            likes = 96,
            shares = 9
        ),
        Post(
            id = 1,
            author = "Нетология. Интернет-университет который смог",
            content = "Всем привет, меня зовут Саша",
            published = "14 января в 22:32",
            likedByMe = false,
            likes = 996,
            shares = 99
        )
    )
    private val data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = data
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
                shares = it.shares + 1
            )
        }
        data.value = posts
    }
}
