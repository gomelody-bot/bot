package dev.gomelody.bot.config

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Helper class that allows you to specify a [prefix] for your whole config.
 *
 * Is intended to be used via composition or via inheritance
 */
public open class EnvironmentConfig internal constructor(private val prefix: String) {

    /**
     * @see getEnv
     */
    protected fun getEnv(default: String? = null): EnvironmentVariable<String> =
        getEnv(prefix, default)

    /**
     * @see getEnv
     */
    protected fun <T> getEnv(
        default: T? = null,
        transform: (String) -> T?
    ): EnvironmentVariable<T> =
        getEnv(prefix, default, transform)
}

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if
 * the found variable is empty or invalid
 */
private fun getEnv(
    prefix: String? = null,
    default: String? = null
): EnvironmentVariable<String> =
    EnvironmentVariable(prefix, { it }, default)

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if the found variable
 * is empty or invalid.
 *
 * The variable is transformed to [T] by [transform]
 */
private fun <T> getEnv(
    prefix: String? = null,
    default: T? = null,
    transform: (String) -> T?
): EnvironmentVariable<T> =
    EnvironmentVariable(prefix, transform, default)

/**
 * Delegated property for a environment variable.
 *
 * @param prefix the prefix for the variable
 * @param transform a transformer to map the value to another type
 * @param default an optional default value
 *
 * @param T the type of the (transformed) variable
 *
 * @see getEnv
 * @see Config
 * @see ReadOnlyProperty
 */
@Suppress("LocalVariableName")
public sealed class EnvironmentVariable<T>(
    private val prefix: String?,
    protected val transform: (String) -> T?,
    protected val default: T?,
) : ReadOnlyProperty<Any, T> {

    /**
     * Computes the name of the variable prefixed by [prefix].
     */
    protected val KProperty<*>.prefixedName: String
        get() = prefix?.let { it + name } ?: name

    /**
     * Makes this variable optional.
     *
     * @return a new [EnvironmentVariable] being optional
     */
    public open fun optional(): EnvironmentVariable<T?> = Optional(prefix, transform, default)

    /**
     * Internal getter.
     */
    protected fun <T> getEnv(
        property: KProperty<*>,
        default: T? = null,
        transform: (String) -> T?
    ): T? = System.getenv(property.prefixedName)?.let(transform) ?: default

    private class Required<T>(prefix: String?, transform: (String) -> T?, default: T?) :
        EnvironmentVariable<T>(prefix, transform, default) {
        @Volatile
        private var _value: T? = null
        private fun missing(name: String): Nothing = error("Missing env variable: $name")

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val v1 = _value
            if (v1 != null) {
                return v1
            }

            return synchronized(this) {
                val v2 = _value
                if (v2 != null) {
                    v2
                } else {
                    val typedValue = getEnv(property, default, transform)
                    _value = typedValue
                    typedValue ?: missing(property.prefixedName)
                }
            }
        }
    }

    private class Optional<T>(prefix: String?, transform: (String) -> T?, default: T?) :
        EnvironmentVariable<T?>(prefix, transform, default) {
        private object UNINITIALIZED

        @Volatile
        private var _value: Any? = UNINITIALIZED

        override fun optional(): EnvironmentVariable<T?> = this

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val v1 = _value
            if (v1 != UNINITIALIZED) {
                return v1 as T
            }

            return synchronized(this) {
                val v2 = _value
                if (v2 != UNINITIALIZED) {
                    v2 as T
                } else {
                    val typedValue = getEnv(property, default, transform)
                    _value = typedValue
                    typedValue as T
                }
            }
        }
    }

    public companion object {
        /**
         * @see EnvironmentVariable
         */
        public operator fun <T> invoke(
            prefix: String?,
            transform: (String) -> T?,
            default: T?,
        ): EnvironmentVariable<T> = Required(prefix, transform, default)
    }
}
