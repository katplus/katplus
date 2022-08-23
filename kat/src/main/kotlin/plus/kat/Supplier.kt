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
 * @receiver Supplier
 * @param spare Spare<*>
 * @return Spare<T>?
 */
inline fun <reified T : Any>
    Supplier.embed(spare: Spare<*>) = embed(T::class.java, spare)

/**
 * @receiver Supplier
 * @return Spare<T>?
 */
inline fun <reified T : Any>
    Supplier.lookup() = lookup(T::class.java)

/**
 * @param data Any?
 * @return T?
 */
inline fun <reified T : Any>
    Supplier.cast(data: Any?) = cast(T::class.java, data)

/**
 * @param data Event
 * @return T?
 */
inline fun <reified T : Any>
    Supplier.read(data: Event<out T>) = read(T::class.java, data)

/**
 * @param data Event
 * @return T?
 */
inline fun <reified T : Any>
    Supplier.down(data: Event<out T>) = down(T::class.java, data)

/**
 * @param data Event
 * @return T?
 */
inline fun <reified T : Any>
    Supplier.parse(data: Event<out T>) = parse(T::class.java, data)
