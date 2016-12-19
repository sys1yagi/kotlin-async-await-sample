package kotlinx.coroutines.android

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

fun asyncAndroid(
        coroutine c: RxAndroidController.() -> Continuation<Unit>
): CompositeSubscription {
    val subscriptions = CompositeSubscription()
    val controller = RxAndroidController(subscriptions)
    c(controller).resume(Unit)
    return subscriptions
}

class RxAndroidController internal constructor(val subscriptions: CompositeSubscription) {
    suspend fun <T> Observable<T>.awaitSingle(x: Continuation<T>) {
        this.single()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWithContinuation(x)
    }

    private fun <T> Observable<T>.subscribeWithContinuation(x: Continuation<T>) {
        val subscription = subscribe(x::resume, x::resumeWithException)
        subscriptions.add(subscription)
    }
}
