package org.wolflink.common.reflection

import org.reflections.Reflections
import org.reflections.ReflectionsException
import org.wolflink.common.string.decodeStringList
import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectionUtils {

    // 根据给定的注解扫描指定包下所有类
    fun getClassesByAnnotation(packageName : String,specifiedAnnotation: Class<out Annotation>) : MutableSet<Class<*>>
    {
        var classes = mutableSetOf<Class<*>>()
        try {
            val reflections = Reflections(packageName)
            classes = reflections.getTypesAnnotatedWith(specifiedAnnotation)
        } catch (ignore : ReflectionsException) { }
        return classes
    }
    // 根据指定的注解获取给定类中所有匹配的方法
    fun getMethodsByAnnotation(classes: Set<Class<*>>,specifiedAnnotation: Class<*>) : MutableSet<Method>
    {
        val set = mutableSetOf<Method>()
        for (clazz in classes)
        {
            for (method in clazz.declaredMethods)
            {
                for (annotation in method.annotations)
                {
                    if(annotation.annotationClass.qualifiedName == specifiedAnnotation.name)
                    {
                        set.add(method)
                        break
                    }
                }
            }
        }
        return set
    }
    // 根据指定的注解筛选所有成员变量
    fun getFieldsByAnnotation(classes: Set<Class<*>>, specifiedAnnotation: Class<*>) : MutableSet<Field>
    {
        val set = mutableSetOf<Field>()
        for (clazz in classes)
        {
            for (field in clazz.declaredFields)
            {
                for (annotation in field.annotations)
                {
                    if(annotation.annotationClass.qualifiedName == specifiedAnnotation.name)
                    {
                        set.add(field)
                        break
                    }
                }
            }
        }
        return set
    }
}
fun Field.setValue(obj : Any, valueString : String)
{
//    println("正在为 ${this.name} 设置值，字符串：$valueString")
    when(this.type)
    {
        String::class.java->{
            this.set(obj,valueString)
        }
        Int::class.java->{
            this.set(obj,valueString.toInt())
        }
        Double::class.java->{
            this.set(obj,valueString.toDouble())
        }
        Float::class.java->{
            this.set(obj,valueString.toFloat())
        }
        List::class.java->{
            this.set(obj,valueString.decodeStringList())
        }
        Boolean::class.java->{
            this.set(obj,valueString.toBoolean())
        }
        Byte::class.java->{
            this.set(obj,valueString.toByte())
        }
        Short::class.java->{
            this.set(obj,valueString.toShort())
        }
        Long::class.java->{
            this.set(obj,valueString.toLong())
        }
    }
//    println("${this.name} 值更新为：${this.get(obj)}")
}