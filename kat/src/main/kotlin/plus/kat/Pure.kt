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
@file:JvmName("Pure")
@file:Suppress(
    "HasPlatformType",
    "NOTHING_TO_INLINE"
)

package plus.kat

import plus.kat.chain.*
import plus.kat.spare.*

/**
 * Returns the spare of the
 * klass from the default supplier
 *
 * E.g.
 * ```
 *  val spare = spare<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Spare.of
 */
inline fun <reified T : Any>
    spare() = Spare.of(T::class.java)

/**
 * Returns the spare of the
 * klass from the default supplier
 *
 * E.g.
 * ```
 *  val name = Space(
 *     "plus.kat.entity.UserVO"
 *  )
 *  val spare = spare<User>(name)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Spare.of
 */
inline fun <reified T : Any>
    spare(name: Space) = Spare.of(name, T::class.java)

/**
 * Encodes the entity to kat [Chan]
 *
 * E.g.
 * ```
 *  val chan = kat {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Chan]
 * @since 0.0.6
 */
inline fun kat(entity: Entity?) = Kat.encode(entity)

/**
 * Encodes the entity to doc [Chan]
 *
 * E.g.
 * ```
 *  val chan = doc {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Chan]
 * @since 0.0.6
 */
inline fun doc(entity: Entity?) = Doc.encode(entity)

/**
 * Encodes the entity to json [Chan]
 *
 * E.g.
 * ```
 *  val chan = json {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Chan]
 * @since 0.0.6
 */
inline fun json(entity: Entity?) = Json.encode(entity)

/**
 * E.g.
 * ```
 *  val context = ...
 *  val spare = context.assign<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.assign
 */
inline fun <reified T : Any>
    Context.assign() = assign<T>(T::class.java)

/**
 * E.g.
 * ```
 *  val context = ...
 *  val name = ...
 *  val spare = context.assign<User>(name)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.assign
 */
inline fun <reified T : Any>
    Context.assign(space: Space) = assign<T>(T::class.java, space)

/**
 * E.g.
 * ```
 *  val context = ...
 *  val spare = ...
 *  val previous = context.active<User>(spare)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.active
 */
inline fun <reified T : Any>
    Context.active(spare: Spare<*>) = active(T::class.java, spare)

/**
 * E.g.
 * ```
 *  val context = ...
 *  val spare = ...
 *  val previous = context.revoke<User>(spare)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.revoke
 */
inline fun <reified T : Any>
    Context.revoke(spare: Spare<*>) = revoke(T::class.java, spare)
