package com.jafir.hookams

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.jafir.hookams.hook.ReflectUtil
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Created by jafir on 2017/12/27.
 */
class OtherActivity : AppCompatActivity() {
    private lateinit var person: Person
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        title = "other"
        findViewById(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById(R.id.test).setOnClickListener {
            test()
        }

        text = findViewById(R.id.text) as TextView
        person = Person("Jafir", 22)
        text.text = "$person"
    }

    private fun test() {
        val name = ReflectUtil.getField(person, "name")
        println("name:$name")
        val age = ReflectUtil.getField(person, "age")
        println("age:$age")

        ReflectUtil.setField(person, "name", "marry")
        ReflectUtil.setField(person, "age", 110)
        text.text = "$person"


        val cls: Class<*> = person.javaClass
        val cls1: Class<Any> = person.javaClass
        val cls2: Class<in Any> = person.javaClass
        val cls3: Class<Person> = person.javaClass

        val cc: Class<Any> = Any::class.java.javaClass
        val ccc: Class<Any> = Int::class.java.javaClass

        person.javaClass// javaClass
        person::class.java // javaClass
        Person::class// kClass
        person.javaClass.kotlin// kClass
        (Person::class as Any).javaClass// javaClass
        Person::class.java // javaClass

        println(Person::class.java.declaredFields.map {
            it.isAccessible = true
            "${it.name}: ${it.get(person)}"
        }.joinToString("1"))

        //第一种
        println(person::class.memberProperties.map {
            it.isAccessible = true
            "${it.name}: ${it.getUnsafed(person)}"
        }.joinToString("2"))

        //第二种
        println(person::class.memberProperties.map {
            it.isAccessible = true
            it as KProperty1<Person, Any>
            "${it.name}: ${it.get(person)}"
        }.joinToString("3"))
        //第三种
        println(person.javaClass.kotlin.memberProperties.map {
            it.isAccessible = true
            "${it.name}: ${it.get(person)}"
        }.joinToString("4"))

        println(Person::class.memberProperties.map {
            it.isAccessible = true
            "${it.name}: ${it.get(person)}"
        }.joinToString("5"))

        println(Person::class.memberProperties.map {
            it.isAccessible = true
            "${it.name}: ${it.invoke(person)}"
        }.joinToString("6"))

        println("description:"+person.description())

        println(person.javaClass == person::class.java) //true
        println(person.javaClass == Person::class.java)//true
        println(person::class.java == Person::class.java)//true
        //person.javaClass == person::class.java == Person::class.java

        println(person.javaClass == Person::class)//false
        println(person.javaClass.kotlin == Person::class)//true
        println(person::class == Person::class)//true
        println(cls)
        println(cls1)
        println(cls2)
    }

    inline fun <reified T : Any> T.description()
            = this::class.memberProperties
            .map {
                "${it.name}: ${it.getUnsafed(this@description)}"
                it as KProperty1<T, Any>
                "${it.name}: ${it.get(this@description)}"
            }
            .joinToString(separator = ";")

    fun <T, R> KProperty1<T, R>.getUnsafed(receiver: Any): R {
        return get(receiver as T)
    }

    override fun onPause() {
        super.onPause()
        Log.e("debug", "pause!!!!!")
    }


    data class Person(val name: String, val age: Int)
}