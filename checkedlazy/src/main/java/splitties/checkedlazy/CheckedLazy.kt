/*
 * Copyright (c) 2017. Louis Cognault Ayeva Derman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package splitties.checkedlazy

/**
 * Returns a lazy that throws an [IllegalStateException] if its value is accessed outside of UI thread.
 */
fun <T> uiLazy(initializer: () -> T): Lazy<T> = CheckedAccessLazyImpl<T>(initializer, uiChecker)

/**
 * Creates a new instance of the [Lazy] that uses the specified initialization
 * function [initializer] and calls [readCheck] on each access.
 *
 * If you want to check access to always happen on a specific thread, you can pass the result of
 * a call to the [accessOn] helper function.
 *
 * Let's say you want to throw an [IllegalStateException] for a non allowed access such as a
 * prohibited [Thread] it's [readCheck] responsibility to do it. [check] or [require] from stdlib
 * may help you implement this kind of check.
 *
 * This lazy is as safe as the [readCheck] is: an empty one will be like calling [kotlin.lazy] with
 * [kotlin.LazyThreadSafetyMode.NONE], which is potentially unsafe.
 *
 * @param readCheck This method may check any condition external to this [Lazy].
 */
fun <T> checkedLazy(readCheck: () -> Unit, initializer: () -> T): Lazy<T> {
    return CheckedAccessLazyImpl<T>(initializer, readCheck)
}

/**
 * This is a modified version of [kotlin.UnsafeLazyImpl] which calls [readCheck] on each access.
 * If you also supplied [firstAccessCheck] (using secondary constructor), it will be called on the
 * first access, then [readCheck] will be called for subsequent accesses.
 */
internal class CheckedAccessLazyImpl<out T>(initializer: () -> T,
                                            private val readCheck: (() -> Unit)? = null,
                                            private var firstAccessCheck: (() -> Unit)? = null
) : Lazy<T> {

    private var initializer: (() -> T)? = initializer
    private var _value: Any? = uninitializedValue

    override val value: T
        get() {
            if (_value === uninitializedValue) {
                firstAccessCheck?.invoke() ?: readCheck?.invoke()
                _value = initializer!!()
                firstAccessCheck = null
                initializer = null
            } else readCheck?.invoke()
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== uninitializedValue
    override fun toString(): String = if (isInitialized()) value.toString() else NOT_INITIALIZED
}

internal val uninitializedValue = Any()
internal const val NOT_INITIALIZED = "Lazy value not initialized yet."
