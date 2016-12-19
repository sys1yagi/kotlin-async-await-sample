package com.sys1yagi.kotlin_async_await_sample.api

import com.sys1yagi.kotlin_async_await_sample.model.User
import rx.Observable

class UserApiClient {
    fun get(id: Long): Observable<User> {
        return Observable.create {
            Thread.sleep(1000)
            it.onNext(User())
            it.onCompleted()
        }
    }
}
