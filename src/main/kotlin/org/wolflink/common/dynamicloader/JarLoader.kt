package org.wolflink.common.dynamicloader

import org.xeustechnologies.jcl.JarClassLoader
import org.xeustechnologies.jcl.JclObjectFactory
import java.io.File
import java.io.FileInputStream
import java.lang.reflect.Method

object JarLoader {

    /**
     * @param file File对象，应该指向一个Jar类型的文件
     * @param classPath 要加载的Jar的静态入口类的路径，例如 org.wolflink.minecraft.Main
     * @param methodMatcher 方法匹配器，符合条件才调用方法
     */
    fun runJar(file : File,classPath : String,methodMatcher : (Method)->Boolean,args : Array<String>? = null) : Boolean
    {
        try {
            val jcl = JarClassLoader();
            jcl.add(FileInputStream(file))
            val factory = JclObjectFactory.getInstance()
            val obj = factory.create(jcl,classPath)
            for(method in obj::class.java.declaredMethods)
            {
                if(methodMatcher(method))
                {
                    method.invoke(null,args)
                    return true
                }
            }
            return false
        }catch (e : Exception) {
            return false
        }

    }

}