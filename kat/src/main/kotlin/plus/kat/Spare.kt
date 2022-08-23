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
@file:Suppress(
    "HasPlatformType"
)

package plus.kat

/**
 * @receiver Event<out T>?
 * @return T?
 */
inline fun <reified T : Any>
    Event<out T>?.read() = Kat.decode(T::class.java, this)

/**
 * @receiver ByteArray?
 * @return T?
 */
inline fun <reified T : Any>
    ByteArray?.read() = Kat.decode(T::class.java, this)

/**
 * @receiver CharSequence?
 * @return T?
 */
inline fun <reified T : Any>
    CharSequence?.read() = Kat.decode(T::class.java, this)

/**
 * @receiver Event?
 * @return T?
 */
inline fun <reified T : Any>
    Event<out T>?.down() = Doc.decode(T::class.java, this)

/**
 * @receiver ByteArray?
 * @return T?
 */
inline fun <reified T : Any>
    ByteArray?.down() = Doc.decode(T::class.java, this)

/**
 * @receiver CharSequence?
 * @return T?
 */
inline fun <reified T : Any>
    CharSequence?.down() = Doc.decode(T::class.java, this)

/**
 * @receiver Event?
 * @return T?
 */
inline fun <reified T : Any>
    Event<out T>?.parse() = Json.decode(T::class.java, this)

/**
 * @receiver ByteArray?
 * @return T?
 */
inline fun <reified T : Any>
    ByteArray?.parse() = Json.decode(T::class.java, this)

/**
 * @receiver CharSequence?
 * @return T?
 */
inline fun <reified T : Any>
    CharSequence?.parse() = Json.decode(T::class.java, this)
