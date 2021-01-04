
@file:Suppress("unused", "NOTHING_TO_INLINE")
package com.example.baseframework.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.View
import com.example.baseframework.log.XLog
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*

inline fun bg(crossinline action: () -> Unit) = Thread { action() }.start()

inline fun bg(r: Runnable) = Thread(r).start()

//inline fun fg(crossinline action: () -> Unit) {
//    if (Looper.myLooper() == Looper.getMainLooper()) {
//        action()
//    } else {
//        GlobalHandler.post { action() }
//    }
//}
//
//inline fun fg(r: Runnable) {
//    if (Looper.myLooper() == Looper.getMainLooper()) {
//        r.run()
//    } else {
//        GlobalHandler.post(r)
//    }
//}

inline fun String?.emptyOrElse(defIfEmpty: String = ""): String {
    return if (this == null || this.isEmpty()) defIfEmpty else this
}

inline fun CharSequence?.emptyOrElse(defIfEmpty: CharSequence = ""): CharSequence {
    return if (this == null || this.isEmpty()) defIfEmpty else this
}

inline fun <reified T: Any> T?.nullOrElse(defIfNull: T): T = this ?: defIfNull

/**
 * 首字母大写
 */
inline fun String.capitalize(locale: Locale = Locale.US): String {
    return if (this.isNotEmpty() && this[0].isLowerCase()) this.substring(0, 1).toUpperCase(locale) + this.substring(1) else this
}

fun <T: Any> T?.toStringEx(includeStaticField: Boolean = false, includeChildFiled: Boolean = false, deep: Int = 3): String {
    if (this == null) {
        return "null"
    }
    if (deep == 0) {
        return this.toString()
    }

    if (this is View) {
        return "${this.javaClass.simpleName}@${Integer.toHexString(System.identityHashCode(this))}"
    }

    return this.getFields(includeStaticField).let { fields ->
        if (fields.isEmpty()) {
            return@let when (this::class) {
                String::class -> "\"${this}\""
                java.lang.String::class -> "\"${this}\""
                else -> this.toString()
            }
        }

        "${this.javaClass.simpleName}@${
        Integer.toHexString(System.identityHashCode(this))
        }(${
        fields.joinToString { field ->
            val includeChild = includeChildFiled && !field.isPrimitive()
            var type = when {
                field.type == Map::class.java -> {
                    val genericType = field.genericType as ParameterizedType
                    val typeArguments =  genericType.actualTypeArguments
                    val keyType = when (val cls = typeArguments.first() as Class<*>) {
                        java.lang.Integer::class.java -> "Int"
                        java.lang.Character::class.java -> "Char"
                        else -> {cls.simpleName}
                    }
                    val valueType = when (val cls = typeArguments[1] as Class<*>) {
                        java.lang.Integer::class.java -> "Int"
                        java.lang.Character::class.java -> "Char"
                        else -> {cls.simpleName}
                    }

                    "${field.type.simpleName}<$keyType, $valueType>"
                }

                Collection::class.java.isAssignableFrom(field.type) -> {
                    val genericType = field.genericType as ParameterizedType
                    val type = when (val cls = genericType.actualTypeArguments.first() as Class<*>) {
                        java.lang.Integer::class.java -> "Int"
                        java.lang.Character::class.java -> "Char"
                        else -> {cls.simpleName}
                    }
                    "${field.type.simpleName}<${type}>"
                }

                field.type.isArray -> {
                    when (val cls = field.genericType as Class<*>) {
                        java.lang.Integer::class.java -> "Int"
                        java.lang.Character::class.java -> "Char"
                        else -> cls.simpleName
                    }
                }

                field.type == java.lang.Integer::class.java -> "Int"
                field.type == java.lang.Character::class.java -> "Char"

                else -> field.type.simpleName
            }

            if (type == "int" ||
                type == "long" ||
                type == "short" ||
                type == "float" ||
                type == "double" ||
                type == "boolean" ||
                type == "byte" ||
                type == "char") {
                type = type.capitalize()
            }

            "${field.name}(${type})=${field.getValue(this, includeStaticField, includeChild, deep)}"
        }
        })"
    }
}

private inline fun Field.getValue(obj: Any?, includeStaticField: Boolean, includeChildFiled: Boolean, deep: Int): String {
    val value = if (Modifier.isStatic(this.modifiers)) this.get(null) else this.get(obj) ?: return "null"
    return when {
        this.type == Int::class.java -> "$value"
        this.type == java.lang.Integer::class.java -> "$value"

        this.type == Short::class.java -> "$value"
        this.type == java.lang.Short::class.java -> "$value"

        this.type == Long::class.java -> "$value"
        this.type == java.lang.Long::class.java -> "$value"

        this.type == Double::class.java -> "$value"
        this.type == java.lang.Double::class.java -> "$value"

        this.type == Float::class.java -> "$value"
        this.type == java.lang.Float::class.java -> "$value"

        this.type == Boolean::class.java -> "$value"
        this.type == java.lang.Boolean::class.java -> "$value"

        this.type == Byte::class.java -> "$value"
        this.type == java.lang.Byte::class.java -> "$value"

        this.type == Char::class.java -> "'${value}'"
        this.type == java.lang.Character::class.java -> "$value"

        this.type == String::class.java -> if (value == null) "null" else "\"${value}\""
        this.type == java.lang.String::class.java -> if (value == null) "null" else "\"${value}\""

        this.type == Map::class.java -> {
            val v = value as Map<*, *>
            "{${v.map { "${it.key.toStringEx(includeStaticField, includeChildFiled, deep - 1)}:${it.value.toStringEx(includeStaticField, includeChildFiled, deep - 1)}" }.joinToString() }}"
        }

        Collection::class.java.isAssignableFrom(this.type) -> {
            val v = value as Collection<*>
            "[${v.joinToString { it.toStringEx(includeStaticField, includeChildFiled, deep - 1) }}]"
        }

        this.type.isArray -> {
            val v = value as? Array<*>
            if (v == null) {
                value.toStringEx(includeStaticField, includeChildFiled, deep - 1)
            } else {
                "[${v.joinToString { it.toStringEx(includeStaticField, includeChildFiled, deep - 1) }}]"
            }
        }

        else -> value.toStringEx(includeStaticField, includeChildFiled, deep - 1)
    }
}

private inline fun Any?.getFields(includeStaticField: Boolean): List<Field> {
    if (this == null || this::class in arrayOf(
            Int::class,
            Short::class,
            Long::class,
            Double::class,
            Float::class,
            Boolean::class,
            Byte::class,
            Char::class,
            String::class
        )) {
        return listOf()
    }

    val list = this.javaClass.declaredFields.apply { forEach { it.isAccessible = true } }
    if (includeStaticField) {
        return list.flatMap {
            println("is isCompanion(${it.type.kotlin.isCompanion}): ${it.type.name}")
            if (it.type.kotlin.isCompanion || it.type.name.endsWith("\$Companion")) {
                it.get(null).javaClass.declaredFields.apply { forEach { f -> f.isAccessible = true } }.toList()
            } else {
                listOf(it)
            }
        }
    }

    return list.filter { !Modifier.isStatic(it.modifiers) }
}

private inline fun Field.isPrimitive(): Boolean {
    return this.type in arrayOf(
        Int::class.java,
        Short::class.java,
        Long::class.java,
        Double::class.java,
        Float::class.java,
        Boolean::class.java,
        Byte::class.java,
        Char::class.java,
        String::class.java,
        Unit::class.java,
        java.lang.Integer::class.java,
        java.lang.Short::class.java,
        java.lang.Long::class.java,
        java.lang.Double::class.java,
        java.lang.Float::class.java,
        java.lang.Boolean::class.java,
        java.lang.Byte::class.java,
        java.lang.Character::class.java,
        java.lang.String::class.java,
        java.lang.Void::class.java
    )
}

/**
 * 如果指定list中的所有字符串都包含在当前字符串中，则返回true，否则返回false。
 */
@Suppress("INAPPLICABLE_OPERATOR_MODIFIER")
operator fun <T: CharSequence> T.contains(others: List<T>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (!this.contains(other, ignoreCase)) {
            return false
        }
    }

    return true
}

/**
 * 如果指定Array中的所有字符串都包含在当前字符串中，则返回true，否则返回false。
 */
@Suppress("INAPPLICABLE_OPERATOR_MODIFIER")
operator fun <T: CharSequence> T.contains(others: Array<T>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (!this.contains(other, ignoreCase)) {
            return false
        }
    }

    return true
}



inline fun Context.getLocale(index: Int = 0): Locale {
    return this.resources.configuration.getLocale(index)
}

inline fun Context.getLocaleList(): MutableList<Locale> {
    return this.resources.configuration.getLocaleList()
}

inline fun Context.getLocaleString(index: Int = 0): String {
    return this.resources.configuration.getLocaleString(index)
}

inline fun Resources.getLocale(index: Int = 0): Locale {
    return this.configuration.getLocale(index)
}

inline fun Resources.getLocaleList(): MutableList<Locale> {
    return this.configuration.getLocaleList()
}

inline fun Resources.getLocaleString(index: Int = 0): String {
    return this.configuration.getLocaleString(index)
}

inline fun Configuration.getLocale(index: Int = 0): Locale {
    return getLocaleList()[index]
}

inline fun Configuration.getLocaleList(): MutableList<Locale> {
    val list = mutableListOf<Locale>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        for (i in 0 until this.locales.size()) {
            list.add(this.locales[i])
        }
    } else {
        @Suppress("DEPRECATION")
        list.add(this.locale)
    }

    return list
}

inline fun Configuration.getLocaleString(index: Int = 0): String {
    return this.getLocale(index).getLocaleString()
}

inline val Configuration.localeSize: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) this.locales.size() else 1

inline fun Locale.getLocaleString(): String {
    return if (this.country.isEmpty()) this.language else "${this.language}_${this.country.toLowerCase(Locale.US)}"
}


/**
 * API: 17（4.2）
 */
inline val isJellyBeanMr1 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

/**
 * API: 18（4.3）
 */
inline val isJellyBeanMr2 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2

/**
 * API: 19（4.4）
 */
inline val isKitKat get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

/**
 * API: 20（4.4W）
 */
inline val isKitKatWatch get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH

/**
 * API: 21（5.0）
 */
inline val isLollipop get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

/**
 * API: 22（5.1）
 */
inline val isLollipopMr1 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1

/**
 * API: 23（6.0）
 */
inline val isMarshmallow get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

/**
 * API: 24（7.0）
 */
inline val isNougat get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

/**
 * API: 25（7.1）
 */
inline val isNougatMr1 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

/**
 * API: 26（8.0）
 */
inline val isOreo get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

/**
 * API: 27（8.1）
 */
inline val isOreoMr1 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

/**
 * API: 28（9.0）
 */
inline val isPie get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

/**
 * API: 29（10.0）
 */
inline val isQ get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

/**
 * API: 30（10+）
 */
inline val isR get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

//inline fun Throwable.string() = Logger.toString(this)

inline fun <T: Any> T.logV(tag: String = "") {
    XLog.v(tag, this.toStringEx())
}

inline fun <T: Any> T.logI(tag: String = "") {
    XLog.i(tag, this.toStringEx())
}

inline fun <T: Any> T.logW(tag: String = "") {
    XLog.w(tag, this.toStringEx())
}

inline fun <T: Any> T.logE(tag: String = "") {
    XLog.e(tag, this.toStringEx())
}

fun <K, V> Map<K, V>.joinToString(kvSeparator: CharSequence = "=", separator: CharSequence = ", ", prefix: CharSequence = "", transform: ((Map.Entry<K, V>) -> CharSequence)? = null): String {
    return if (transform == null) {
        this.map { "${it.key.toString()}$kvSeparator${it.value.toString()}" }
    } else {
        this.map(transform)
    }.joinToString(separator, prefix)
}

