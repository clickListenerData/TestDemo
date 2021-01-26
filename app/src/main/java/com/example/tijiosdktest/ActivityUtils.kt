package com.example.tijiosdktest

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.viewbinding.ViewBinding
import com.example.tijiosdktest.databinding.ActivityRvBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun<reified T: Activity> Context.startActivity(vararg params: Pair<String, Any>) {
    startActivity(Intent(this,T::class.java).apply { putParams(*params) })
}

inline fun<T> Intent.putParams(vararg params: Pair<String,T>) {
    params.forEach {
        when(val value = it.second) {
            is Int -> putExtra(it.first,value)
            is Long -> putExtra(it.first,value)
            is Byte -> putExtra(it.first,value)
            is String -> putExtra(it.first,value)
            is Short -> putExtra(it.first,value)
            is Boolean -> putExtra(it.first,value)
            is Char -> putExtra(it.first,value)
            is Float -> putExtra(it.first,value)
            is Double -> putExtra(it.first,value)
        }
    }
}

fun startActivityForResult(requestCode: Int) {

}

inline fun <T: ViewBinding>inflateViewBind(clz: Class<T>) : T {
    return clz.getMethod("inflate").invoke(null,null) as T
}

inline fun <reified T: ViewBinding>viewBind() = ViewBindLazy(T::class.java)

inline fun <reified T: ViewBinding>viewCustom() = CustomLazy(T::class.java)

class ViewBindLazy<T : ViewBinding>(val clz: Class<T>) : Lazy<T> {
    private var _value : Any? = null

    override val value: T
        get() {
            return if (_value != null) _value as T  else inflateViewBind(clz)
        }
    override fun isInitialized(): Boolean {
        return _value != null
    }
}

class CustomLazy<T: ViewBinding>(val clz: Class<T>) {
    operator fun getValue(thisRef: Any, property: KProperty<*>) : T{
        return inflateViewBind(clz)
    }
}
