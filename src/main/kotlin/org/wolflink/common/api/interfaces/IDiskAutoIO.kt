package org.wolflink.common.api.interfaces

import com.google.gson.JsonObject
import org.wolflink.common.fileio.loadAsJsonObject
import org.wolflink.common.fileio.save
import java.io.File

// 实例自动读写到硬盘的接口
interface IDiskAutoIO : IAutoIO {

    fun fileType() = "wolf"

    override fun load() {
        val file = File("${dataFolderPath()}/${tableName()}","${primaryKey()}${if (fileType().isEmpty())"" else "."}${fileType()}")
        fromJsonObject(file.loadAsJsonObject())
    }

    override fun save() {
        val file = File("${dataFolderPath()}/${tableName()}","${primaryKey()}${if (fileType().isEmpty())"" else "."}${fileType()}")
        file.save(toJsonObject())
    }
}