package org.wolflink.common.string


@SuppressWarnings("StringList 被转化为 String 后，可能出现格式错误，例如元素中带有, 字段，则在再次转化为List之后会出现误差")
fun String.decodeStringList() : List<String>
{
    if(this.length <= 2)return mutableListOf()
    val str = this.substring(1,this.length-1)
    return str.split(", ")
}