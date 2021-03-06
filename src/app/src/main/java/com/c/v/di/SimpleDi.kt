package com.c.v.di

import java.lang.reflect.Type

@Deprecated("Использовать даггер", level = DeprecationLevel.WARNING)
class SimpleDi private constructor() {

    private val references = mutableMapOf<Type, Any>()

    fun <TInterface> register(interfaceType: Type, objects: TInterface) {
        references[interfaceType] = objects as Any
    }

    fun <TInterface> resolve(interfaceType: Type): TInterface {
        @Suppress("UNCHECKED_CAST") return references[interfaceType] as TInterface
    }

    companion object {
        val Instance = SimpleDi()
    }
}