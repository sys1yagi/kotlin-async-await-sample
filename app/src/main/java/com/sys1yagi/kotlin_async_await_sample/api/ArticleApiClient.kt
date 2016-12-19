package com.sys1yagi.kotlin_async_await_sample.api

import com.sys1yagi.kotlin_async_await_sample.model.Article
import com.sys1yagi.kotlin_async_await_sample.model.User
import rx.Observable
import java.util.*

class ArticleApiClient {

    fun getArticles(user: User):Observable<List<Article>>{
        return Observable.create {
            Thread.sleep(1000)
            it.onNext(0.rangeTo(Random().nextInt(100)).toList().map { Article() })
            it.onCompleted()
        }
    }
}
