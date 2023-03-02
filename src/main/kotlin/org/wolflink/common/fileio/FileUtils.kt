package org.wolflink.common.fileio

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.wolflink.common.gson.toPrettyFormat
import java.io.File

// 获取当前运行路径(在动态加载的环境下此值为主动加载方Jar的路径，而不是调用该方法的Jar的路径
fun getRunPath() : String = System.getProperties().getProperty("user.dir")
// 创建父文件夹并创建文件
fun File.save()
{
    this.parentFile.mkdirs()
    this.createNewFile()
}
// 将JsonElement转为格式化字符串以后写入保存文件中
fun File.save(jsonElement : JsonElement)
{
    // 如果当前文件不存在于硬盘当中，则先保存模板文件
    if(!this.exists()) save()
    var content = ""
    if(jsonElement.isJsonObject)content = jsonElement.asJsonObject.toPrettyFormat()
    if(jsonElement.isJsonArray)content = jsonElement.asJsonArray.toPrettyFormat()
    this.writeText(content)
}
// 将文件中所有内容视为一个JsonObject并获取
fun File.loadAsJsonObject() : JsonObject
{
    if(!this.exists()) return JsonObject()
    val jsonText = this.readText()
    return Gson().fromJson(jsonText,JsonObject::class.java) ?: JsonObject()
}
// 将文件中所有内容视为一个JsonArray并获取
fun File.loadAsJsonArray() : JsonArray
{
    if(!this.exists()) return JsonArray()
    val jsonText = this.readText()
    return Gson().fromJson(jsonText,JsonArray::class.java) ?: JsonArray()
}
// 将文件中的内容作为一个JsonObject读取并转为指定类型的实例
inline fun <reified T> File.loadAsJsonObject() : T?
{
    if(!this.exists()) return null
    val jsonText = this.readText()
    return Gson().fromJson(jsonText,T::class.java)
}
// 将文件中的内容视为JsonArray并将其中所有JsonObject转为指定类型的对象最后返回MutableList<T>
inline fun <reified T> File.loadJsonArrayAsList() : MutableList<T>
{
    if(!this.exists()) return mutableListOf()
    val list = mutableListOf<T>()
    val jsonText = this.readText()
    val jsonArray = JsonParser.parseString(jsonText).asJsonArray ?: return mutableListOf()
    for (jsonObj in jsonArray) list.add(Gson().fromJson(jsonObj,T::class.java) ?: continue)
    return list
}