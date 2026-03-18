package org.fitnessConstructor.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


fun <T> emitFlow(action: suspend () -> T) = flow { emit(action()) }
fun <T> Flow<T>.subscribe(
    scope: CoroutineScope,
    success: (value: T) -> Unit
) = scope.launch { subscribe { success.invoke(it) } }

fun <T> Flow<T>.subscribe(
    scope: CoroutineScope,
    success: suspend (value: T) -> Unit,
    error: suspend (Throwable) -> Unit = { },
    complete: () -> Unit = { }
) = scope.launch {
    subscribe(
        success = { success.invoke(it) },
        error = { error.invoke(it) }
    )
}.apply { invokeOnCompletion { complete.invoke() } }

suspend fun <T> Flow<T>.subscribe(
    success: suspend (value: T) -> Unit,
    error: suspend (Throwable) -> Unit = { }
) {
    try {
        collect { success.invoke(it) }

    }catch (ce: CancellationException) {
        throw ce
    } catch (throwable: Throwable) {
        error.invoke(throwable)
        throwable.printStackTrace()
    }
}

suspend fun <T> Flow<T>.subscribe(success: suspend (value: T) -> Unit) {
    collect { success.invoke(it) }
}
