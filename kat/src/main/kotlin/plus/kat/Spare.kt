/*
 * Copyright 2022 Kat+ Team
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
package plus.kat

import plus.kat.anno.Nullable

import kotlin.reflect.KClass

/**
 * @param klass KClass<T>
 * @return Spare<T>?
 */
@Nullable
@Suppress("HasPlatformType")
fun <T : Any> lookup(
    klass: KClass<T>
) = Spare.lookup(
    klass.java
)

/**
 * @param data ByteArray?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> read(
    data: ByteArray?
) = Kat.decode(
    T::class.java, data
)

/**
 * @param data CharSequence?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> read(
    data: CharSequence?
) = Kat.decode(
    T::class.java, data
)

/**
 * @param data Event?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> read(
    data: Event<out T>?
) = Kat.decode(
    T::class.java, data
)

/**
 * @param data ByteArray?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> down(
    data: ByteArray?
) = Doc.decode(
    T::class.java, data
)

/**
 * @param data CharSequence?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> down(
    data: CharSequence?
) = Doc.decode(
    T::class.java, data
)

/**
 * @param data Event?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> down(
    data: Event<out T>?
) = Doc.decode(
    T::class.java, data
)

/**
 * @param data ByteArray?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> parse(
    data: ByteArray?
) = Json.decode(
    T::class.java, data
)

/**
 * @param data CharSequence?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> parse(
    data: CharSequence?
) = Json.decode(
    T::class.java, data
)

/**
 * @param data Event?
 * @return T?
 */
@Nullable
@Suppress("HasPlatformType")
inline fun <reified T : Any> parse(
    data: Event<out T>?
) = Json.decode(
    T::class.java, data
)
