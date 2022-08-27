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
@file:JvmName("Sugar")
@file:Suppress(
    "FunctionName",
    "HasPlatformType",
    "NOTHING_TO_INLINE"
)

package plus.kat

import java.io.IOException
import kotlin.jvm.Throws

/**
 * Serialize block to Kat [String]
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
fun kat(block: Kat) = Kat(null, block).toString()

/**
 * Serialize block to Kat [String]
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
fun kat(space: CharSequence?, block: Kat) = Kat(space, block).toString()

/**
 * Serialize block to Kat
 *
 * E.g.
 * ```
 *  Kat("User") {
 *    it["id"] = 1
 *    it["name"] = "kraity"
 *  }
 * ```
 *
 * @return [Chan]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Kat(space: CharSequence?, block: Kat) = Chan(Plan.DEF).also { it[null, space] = block }

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
inline fun Any?.toKat() = Kat.encode(null, this)

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
inline fun Any?.toKat(flags: Long) = Kat.encode(null, this, flags)

/**
 * Serialize block to Doc [String]
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
fun doc(name: CharSequence?, block: Kat) = Doc(name, block).toString()

/**
 * Serialize block to [Doc]
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
fun Doc(name: CharSequence?, block: Kat) = Doc(Plan.DEF).also { it[name] = block }

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
 * Serialize block to Json [String]
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
fun json(block: Kat) = Json(block).toString()

/**
 * Serialize block to [Json]
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
fun Json(block: Kat) = Json(Plan.DEF).also { it[null] = block }

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
 * Read Kat [Event]? to [T]
 *
 * E.g.
 * ```
 *  val event = ...
 *  val data = event.read<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    Event<out T>?.read() = Kat.decode(T::class.java, this)

/**
 * Read Kat [ByteArray]? to [T]
 *
 * E.g.
 * ```
 *  val bytes = ...
 *  val data = bytes.read<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    ByteArray?.read() = Kat.decode(T::class.java, this)

/**
 * Read Kat [CharSequence]? to [T]
 *
 * E.g.
 * ```
 *  val chars = ...
 *  val data = chars.read<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    CharSequence?.read() = Kat.decode(T::class.java, this)

/**
 * Read Doc [Event]? to [T]
 *
 * E.g.
 * ```
 *  val event = ...
 *  val data = event.down<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    Event<out T>?.down() = Doc.decode(T::class.java, this)

/**
 * Read Doc [ByteArray]? to [T]
 *
 * E.g.
 * ```
 *  val bytes = ...
 *  val data = bytes.down<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    ByteArray?.down() = Doc.decode(T::class.java, this)

/**
 * Read Doc [CharSequence]? to [T]
 *
 * E.g.
 * ```
 *  val chars = ...
 *  val data = chars.down<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    CharSequence?.down() = Doc.decode(T::class.java, this)

/**
 * Read Json [Event]? to [T]
 *
 * E.g.
 * ```
 *  val event = ...
 *  val data = event.parse<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    Event<out T>?.parse() = Json.decode(T::class.java, this)

/**
 * Read Json [ByteArray]? to [T]
 *
 * E.g.
 * ```
 *  val bytes = ...
 *  val data = bytes.parse<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    ByteArray?.parse() = Json.decode(T::class.java, this)

/**
 * Read Json [CharSequence]? to [T]
 *
 * E.g.
 * ```
 *  val chars = ...
 *  val data = chars.parse<User>()
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 */
inline fun <reified T : Any>
    CharSequence?.parse() = Json.decode(T::class.java, this)

/**
 * E.g.
 * ```
 *  val spare = lookup<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Spare.lookup
 */
inline fun <reified T : Any>
    lookup() = Spare.lookup(T::class.java)

/**
 * E.g.
 * ```
 *  val spare = ...
 *  val previous = embed<User>(spare)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Spare.embed
 */
inline fun <reified T : Any>
    embed(spare: Spare<*>) = Spare.embed(T::class.java, spare)

/**
 * E.g.
 * ```
 *  val previous = revoke<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Spare.revoke
 */
inline fun <reified T : Any>
    revoke() = Spare.revoke(T::class.java)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val spare = ...
 *  val previous = supplier.embed<User>(spare)
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Supplier.embed
 */
inline fun <reified T : Any>
    Supplier.embed(spare: Spare<*>) = embed(T::class.java, spare)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val previous = supplier.revoke<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Supplier.revoke
 */
inline fun <reified T : Any>
    Supplier.revoke() = revoke(T::class.java)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val spare = supplier.lookup<User>()
 * ```
 *
 * @return [Spare]?
 * @since 0.0.4
 * @see Supplier.lookup
 */
inline fun <reified T : Any>
    Supplier.lookup() = lookup(T::class.java)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val coder = supplier.activate<UserCoder>()
 * ```
 *
 * @return Coder?
 * @since 0.0.4
 * @see Supplier.activate
 */
inline fun <reified T : Any>
    Supplier.activate() = activate<T>(T::class.java)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val previous = supplier.deactivate<UserCoder>()
 * ```
 *
 * @return Coder?
 * @since 0.0.4
 * @see Supplier.deactivate
 */
inline fun <reified T : Any>
    Supplier.deactivate() = deactivate(T::class.java)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val data = ...
 *  val result = supplier.cast<User>(data)
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 * @see Supplier.cast
 */
inline fun <reified T : Any>
    Supplier.cast(data: Any?) = cast(T::class.java, data)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val event = ...
 *  val result = supplier.read<User>(event)
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 * @see Supplier.read
 */
inline fun <reified T : Any>
    Supplier.read(data: Event<out T>) = read(T::class.java, data)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val event = ...
 *  val result = supplier.down<User>(event)
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 * @see Supplier.down
 */
inline fun <reified T : Any>
    Supplier.down(data: Event<out T>) = down(T::class.java, data)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val event = ...
 *  val result = supplier.parse<User>(event)
 * ```
 *
 * @return [T]?
 * @since 0.0.4
 * @see Supplier.parse
 */
inline fun <reified T : Any>
    Supplier.parse(data: Event<out T>) = parse(T::class.java, data)

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val data = ...
 *  val alias = ...
 *  val result = supplier.mark(data, alias)
 * ```
 *
 * @return [Doc]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Supplier?.mark(value: Any?, alias: CharSequence?) = Doc(Plan.DEF, this).also { it[alias] = value }

/**
 * E.g.
 * ```
 *  val supplier = ...
 *  val data = ...
 *  val alias = ...
 *  val result = supplier.write(data, alias)
 * ```
 *
 * @return [Chan]
 * @since 0.0.4
 */
@Throws(IOException::class)
fun Supplier?.write(value: Any?, alias: CharSequence?) = Chan(Plan.DEF, this).also { it[alias] = value }
