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
@file:JvmName("Span")
@file:Suppress(
    "FunctionName",
    "HasPlatformType",
    "NOTHING_TO_INLINE"
)

package plus.kat

import java.io.*
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
 * Serialize the entity to Kat [String]
 *
 * E.g.
 * ```
 *  kat {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun kat(entity: Entity) = Kat(entity).use { it.toString() }

/**
 * Serialize the entity to Kat
 *
 * E.g.
 * ```
 *  Kat {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Kat]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Kat(entity: Entity) = Kat(Plan.DEF).also { it[null] = entity }

/**
 * Serialize the entity to Kat [String]
 *
 * E.g.
 * ```
 *  kat("User") {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun kat(space: String?, entity: Entity) = Kat(space, entity).use { it.toString() }

/**
 * Serialize the entity to Kat
 *
 * E.g.
 * ```
 *  Kat("User") {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Kat]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Kat(space: String?, entity: Entity) = Kat(Plan.DEF).also { it[null, space] = entity }

/**
 * Serialize [Any]? to Kat [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toKat()
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toKat() = Kat.encode(this)

/**
 * Serialize [Any]? to Kat [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toKat(Flag.UNICODE)
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toKat(flags: Long) = Kat.encode(this, flags)

/**
 * Serialize the entity to Doc [String]
 *
 * E.g.
 * ```
 *  doc("User") {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun doc(name: String?, entity: Entity) = Doc(name, entity).use { it.toString() }

/**
 * Serialize the entity to [Doc]
 *
 * E.g.
 * ```
 *  doc("User") {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Doc]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Doc(name: String?, entity: Entity) = Doc(Plan.DEF).also { it[name] = entity }

/**
 * Serialize [Any]? to Doc [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toDoc()
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toDoc(): String = Doc.encode(null, this)

/**
 * Serialize [Any]? to Doc [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toDoc(Flag.UNICODE)
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toDoc(flags: Long): String = Doc.encode(null, this, flags)

/**
 * Serialize the entity to Json [String]
 *
 * E.g.
 * ```
 *  json {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun json(entity: Entity) = Json(entity).use { it.toString() }

/**
 * Serialize the entity to [Json]
 *
 * E.g.
 * ```
 *  Json {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Json]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Json(entity: Entity) = Json(Plan.DEF).also { it[null] = entity }

/**
 * Serialize [Any]? to Json [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toJson()
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toJson() = Json.encode(this)

/**
 * Serialize [Any]? to Json [String]
 *
 * E.g.
 * ```
 *  val data = ...
 *  val text = data.toJson(Flag.UNICODE)
 * ```
 *
 * @return [String]
 * @since 0.0.4
 */
inline fun Any?.toJson(flags: Long): String = Json.encode(this, flags)

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

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val flow = ...
 *  val entity = supplier.read<User>(flow)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.revoke
 */
inline fun <reified T : Any> Supplier.read(flow: Flow) = read<T>(T::class.java, flow)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val flow = ...
 *  val entity = supplier.down<User>(flow)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.revoke
 */
inline fun <reified T : Any> Supplier.down(flow: Flow) = down<T>(T::class.java, flow)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val flow = ...
 *  val entity = supplier.parse<User>(flow)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.6
 * @see Context.revoke
 */
inline fun <reified T : Any> Supplier.parse(flow: Flow) = parse<T>(T::class.java, flow)
