package com.sys1yagi.kotlin_async_await_sample

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sys1yagi.kotlin_async_await_sample.api.ArticleApiClient
import com.sys1yagi.kotlin_async_await_sample.api.UserApiClient
import com.sys1yagi.kotlin_async_await_sample.databinding.ActivityMainBinding
import com.sys1yagi.kotlin_async_await_sample.model.Article
import kotlinx.coroutines.android.asyncAndroid
import kotlinx.coroutines.asyncRx
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    val userApiClient = UserApiClient()

    val articleApiClient = ArticleApiClient()

    val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.asyncRx.setOnClickListener {
            asyncRxCall()
        }
        binding.asyncAndroid.setOnClickListener {
            asyncAndroidCall()
        }
    }

    fun asyncRxCall() {
        binding.textArea.text = "asyncRx loading..."
        val observable = asyncRx<List<Article>> {
            val user = userApiClient.get(1L)
                    .subscribeOn(Schedulers.io())
                    .awaitSingle() // await and run other thread and continue

            articleApiClient.getArticles(user).awaitSingle() // await and run other thread and return value
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { articles ->

                    binding.textArea.text = "asyncRx finish loading. size=${articles.size}"
                }
    }

    fun asyncAndroidCall() {
        binding.textArea.text = "asyncAndroid loading..."

        asyncAndroid {
            val user = userApiClient.get(1L).awaitSingle() // await and run other thread and continue

            binding.textArea.text = "asyncAndroid loading articles..." // run main thread

            val articles = articleApiClient.getArticles(user).awaitSingle() // await and run other thread and continue

            binding.textArea.text = "asyncAndroid finish loading. size=${articles.size}" // run main thread
        }
    }
}
