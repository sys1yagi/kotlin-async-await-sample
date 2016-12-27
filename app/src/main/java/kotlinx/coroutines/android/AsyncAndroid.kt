package kotlinx.coroutines.android

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.AsyncSubject
import rx.subscriptions.CompositeSubscription
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

fun asyncAndroid(
        c: suspend RxAndroidController.() -> Unit
): CompositeSubscription {
    val subscriptions = CompositeSubscription()
    val controller = RxAndroidController(subscriptions)
    c.startCoroutine(
            controller, controller
    )
    return subscriptions
}

class RxAndroidController internal constructor(val subscriptions: CompositeSubscription) : Continuation<Unit> {
    val result = AsyncSubject.create<Unit>()
    override fun resumeWithException(exception: Throwable) {
        result.onError(exception)
    }

    override fun resume(data: Unit) {
        result.onNext(data)
        result.onCompleted()
    }

    suspend fun <T> Observable<T>.await() = suspendCoroutine<T> { x ->
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
