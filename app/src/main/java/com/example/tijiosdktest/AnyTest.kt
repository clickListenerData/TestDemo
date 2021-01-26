package com.example.tijiosdktest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

open class AnyTest<E> {

    val elements : Array<E?>? = null

    override fun equals(other: Any?): Boolean {
//        elements = Array(10){null}
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    suspend fun test() = withContext(Dispatchers.Default) {
        return@withContext 1
    }
}