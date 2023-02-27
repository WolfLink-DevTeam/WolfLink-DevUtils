package org.wolflink.common.gson

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

// 将JsonElement转为带有换行符的格式化文本
fun JsonElement.toPrettyFormat() : String{
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(this)
}