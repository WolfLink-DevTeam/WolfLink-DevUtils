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

/**
 * 声明式自动化配置文件，请继承该类
 * 使用 @HeadComment 注解为配置文件添加头部注释
 * 使用 @ConfigNode 声明配置项 ( 类型不能为 final 或者 val )
 * 集合类型必须可变
 * 最终配置文件会被保存为 PrettyJson 格式
 * 需要自行调用 load() 和 save() 方法完成配置文件的加载和保存工作
 *
 * @param configName 配置文件名称
 * @param configPath 配置文件所处目录的路径，默认为Jar运行路径
 */
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

    open fun load()
    {
        if(!configFile.exists())save()
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
    open fun save()
    {
        if(!configFile.exists())configFile.save()
        configFile.writeText(toPrettyJsonString())
    }

}