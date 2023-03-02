package org.wolflink.common.configuration

import com.google.gson.JsonObject
import org.wolflink.common.api.annotations.ConfigNode
import org.wolflink.common.api.annotations.HeadComment
import org.wolflink.common.fileio.getRunPath
import org.wolflink.common.fileio.loadAsJsonObject
import org.wolflink.common.fileio.save
import org.wolflink.common.gson.toPrettyFormat
import org.wolflink.common.reflection.setValue
import org.wolflink.common.string.decodeStringList
import java.io.File
import java.lang.reflect.Field

open class AutoConfig(configName : String, configPath : String = getRunPath()) {
    private val configFile = File(configPath,"$configName.wolf")

    private fun toPrettyJsonString() : String
    {
        val headComment = this::class.java.getAnnotation(HeadComment::class.java)?.comment ?: ""
        val root = JsonObject()
        if(headComment.isNotEmpty()) root.addProperty("# FILE-COMMENT #",headComment)
        for (field in this::class.java.declaredFields)
        {
            field.isAccessible = true
            val annotation = field.getAnnotation(ConfigNode::class.java) ?: continue
            // 无根，平级成员变量
            if(annotation.root.isEmpty())root.addProperty(field.name,field.get(this)?.toString() ?: "错误值")
            // 有根，层层注册
            else{
                val nodes = annotation.root.split(".")
                var lastNode = root
                var nextNode : JsonObject?
                for (node in nodes)
                {
                    nextNode = lastNode.getAsJsonObject(node)
                    if(nextNode == null)
                    {
                        nextNode = JsonObject()
                        lastNode.add(node,nextNode)
                    }
                    lastNode = nextNode
                }
                lastNode.addProperty(field.name,field.get(this)?.toString() ?: "错误值")
                if(annotation.comment.isNotEmpty()) lastNode.addProperty("#${field.name}",annotation.comment)
            }
//            jsonObject.addProperty()
        }
        return root.toPrettyFormat()
    }

    fun load()
    {
        if(!configFile.exists())configFile.save()
        val jsonObject = configFile.loadAsJsonObject()
        for (field in this::class.java.declaredFields)
        {
            field.isAccessible = true
            val annotation = field.getAnnotation(ConfigNode::class.java) ?: continue
            val root = annotation.root
            if(root.isEmpty())field.setValue(this,jsonObject.get(field.name)?.asString?:"")
            else{
                val nodes = root.split(".")
                var goalJsonObject : JsonObject? = jsonObject
                for(node in nodes)
                {
                    goalJsonObject = goalJsonObject?.getAsJsonObject(node)
                    if(goalJsonObject == null)break
                }
                if(goalJsonObject != null)field.setValue(this,goalJsonObject.get(field.name)?.asString?:"")
            }
        }
    }
    fun save()
    {
        if(!configFile.exists())configFile.save()
        configFile.writeText(toPrettyJsonString())
    }

}