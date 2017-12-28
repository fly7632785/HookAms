package com.jafir.hookams.hook

import android.text.TextUtils
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by jafir on 2017/12/28.
 */
object ReflectUtil {
    /**
     * 反射的方式设置某个类的成员变量的值
     *
     * @param clazzObj  类对象
     * @param fieldString fieldString
     * @param newField    新的field
     *
     */
    fun setField(clazzObj: Any?, fieldString: String,
                 newField: Any) {
        if (clazzObj == null || TextUtils.isEmpty(fieldString)) return
        var field: Field? = null
        var cl: Class<*>? = clazzObj.javaClass
        while (field == null && cl != null) {
            try {
                field = cl.getDeclaredField(field)
                if (field != null) {
                    field.isAccessible = true
                }
            } catch (ignored: Throwable) {

            }

            if (field == null) {
                cl = cl.superclass
            }
        }
        if (field != null) {
            try {
                field.set(clazzObj, newField)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        } else {
            System.err.print(field + " is not found in " + clazzObj.javaClass.name)
        }
    }


    /**
     * 反射的方式获取某个类的方法
     *
     * @param cl             类的class
     * @param name           方法名称
     * @param parameterTypes 方法对应的输入参数类型
     * @return 方法
     */
    fun getMethod(cl: Class<*>?, name: String, vararg parameterTypes: Class<*>): Method? {
        var cl = cl
        var method: Method? = null
        while (method == null && cl != null) {
            try {
                method = cl.getDeclaredMethod(name, *parameterTypes)
                if (method != null) {
                    method.isAccessible = true
                }
            } catch (ignored: Exception) {

            }

            if (method == null) {
                cl = cl.superclass
            }
        }
        return method
    }


    /**
     * 反射的方式获取某个类的某个成员变量值
     *
     * @param clazzObj  类对象
     * @param paramString field的名字
     * @return field对应的值
     *
     */
    fun getField(clazzObj: Any?, paramString: String): Any? {
        if (clazzObj == null) return null
        var field: Field? = null
        var `object`: Any? = null
        var cl: Class<*>? = clazzObj.javaClass
        while (field == null && cl != null) {
            try {
                field = cl.getDeclaredField(paramString)
                if (field != null) {
                    field.isAccessible = true
                }
            } catch (ignored: Exception) {

            }

            if (field == null) {
                cl = cl.superclass
            }
        }
        try {
            if (field != null)
                `object` = field.get(clazzObj)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return `object`
    }
}