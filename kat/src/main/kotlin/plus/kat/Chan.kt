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
    "HasPlatformType",
    "NOTHING_TO_INLINE"
)

package plus.kat

/**
 * @return String
 */
fun kat(space: CharSequence?, block: Kat) = Kat(space, block).toString()

/**
 * @return Chan
 */
fun Kat(space: CharSequence?, block: Kat) = Chan().apply { set(null, space, block) }

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toKat() = Kat.encode(null, this)

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toKat(flags: Long) = Kat.encode(null, this, flags)

/**
 * @return String
 */
fun doc(name: CharSequence?, block: Kat) = Doc(name, block).toString()

/**
 * @return Doc
 */
fun Doc(name: CharSequence?, block: Kat) = Doc().apply { set(name, block) }

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toDoc(): String = Doc.encode(null, this)

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toDoc(flags: Long): String = Doc.encode(null, this, flags)

/**
 * @return String
 */
fun json(block: Kat) = Json(block).toString()

/**
 * @return Json
 */
fun Json(block: Kat) = Json().apply { set(null, block) }

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toJson() = Json.encode(this)

/**
 * @receiver Any?
 * @return String
 */
inline fun Any?.toJson(flags: Long): String = Json.encode(this, flags)
