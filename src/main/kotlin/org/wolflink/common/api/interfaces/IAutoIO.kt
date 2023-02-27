package org.wolflink.common.api.interfaces

import com.google.gson.JsonObject
import org.wolflink.common.fileio.getRunPath

// 自动读写(持久化)接口，配合InstancePool使用
interface IAutoIO {
    // 获取默认的数据库(文件夹)路径
    fun dataFolderPath() = getRunPath()
    // 获取读写相关的表单名称
    fun tableName() = this::class.simpleName
    // 获取当前实例的主键，以便查找读取
    fun primaryKey() : String
    // 保存到数据库或者硬盘
    fun save()
    // 从数据库或者硬盘中读取
    fun load()
    // 从JsonObject中加载
    fun fromJsonObject(jsonObject: JsonObject)
    // 保存到JsonObject
    fun toJsonObject() : JsonObject
}