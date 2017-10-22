package me.qiushui.update

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * 事件总线
 *
 * Created by Qiushui on 2017/10/21.
 */
internal object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}