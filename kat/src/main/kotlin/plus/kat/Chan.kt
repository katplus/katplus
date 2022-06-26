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

/**
 * @return String
 */
inline fun kat(
    space: CharSequence? = null,
    alias: CharSequence? = null,
    crossinline block: (Chan) -> Unit
) = Chan(space, alias) { block(it) }.toString()

/**
 * @receiver Any?
 * @return String
 */
fun Any?.toKat(
    flags: Long = 0
) = Chan(this, flags).toString()

/**
 * @receiver Any?
 * @return String
 */
fun Any?.toKat(
    alias: CharSequence? = null, flags: Long = 0
) = Chan(alias, this, flags).toString()

/**
 * @return String
 */
inline fun json(
    crossinline block: (Chan) -> Unit
) = Json { block(it) }.toString()

/**
 * @receiver Any?
 * @return String
 */
fun Any?.toJson(
    flags: Long = 0
) = Json(this, flags).toString()

/**
 * @return String
 */
inline fun doc(
    name: CharSequence,
    crossinline block: (Chan) -> Unit
) = Doc(name) { block(it) }.toString()

/**
 * @receiver Any?
 * @return String
 */
fun Any?.toDoc(
    flags: Long = 0
) = Doc(this, flags).toString()

/**
 * @receiver Any?
 * @return String
 */
fun Any?.toDoc(
    alias: CharSequence? = null, flags: Long = 0
) = Doc(alias, this, flags).toString()
