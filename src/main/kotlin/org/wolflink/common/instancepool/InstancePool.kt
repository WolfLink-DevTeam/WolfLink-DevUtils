package org.wolflink.common.instancepool

import org.wolflink.common.api.interfaces.IAutoIO
import java.util.*

/**
 *
 * 自动化实例资源管理池，自动完成实例的读取、加载等工作，通过统一的get()方法进行获取
 * 在项目关闭时务必调用saveAll方法把所有实例资源进行保存，否则会出现数据丢失的情况
 * 当一个实例超过 recycleTime 毫秒未被操作后，将会自动执行其持久化和引用销毁工作
 * 保存的实例默认为浅拷贝，如需深拷贝请自己实现((
 *
 * @param recycleTime 资源池中单个实例回收周期，毫秒(Long)
 * @param newInstance 自动创建新实例的匿名方法，入参为数据实例的主键，出参为实例类型
 * 只要实现了 IAutoIO 接口，资源池会自动为其完成保存任务
 */
class InstancePool<K,V>(private val recycleTime : Long, private val newInstance : (K) -> V) {
    init { instancePools.add(this) }
    companion object{
        init {
            Runtime.getRuntime().addShutdownHook(Thread { saveAll() })
        }
        val instancePools : MutableSet<InstancePool<*, *>> = mutableSetOf()

        @Deprecated("应该在程序终止时调用")
        fun saveAll()
        {
            for (instancePool in instancePools) {
                for (value in instancePool.instanceMap.values) {
                    if(value is IAutoIO) { value.save() }
                }
            }
            println("Instance pools have been saved.")
        }
    }

    private val timer = Timer()
    private val recyclerMap : MutableMap<K,TimerTask> = mutableMapOf()
    private val instanceMap : MutableMap<K,V> = mutableMapOf()
    /**
     * 向资源池中添加资源，如果已经存在则取消原资源回收计时器，重新设置新计时器
     */
    private fun add(key : K,value : V)
    {
        resetTimer(key)
        instanceMap[key] = value
    }
    // 重置资源回收计时器
    private fun resetTimer(key : K){
        recyclerMap[key]?.cancel()
        timer.schedule(recyclerMap[key] ?: object :TimerTask() {
            override fun run() {
                val value = recyclerMap[key] ?: return
                if(value is IAutoIO)value.save()
                instanceMap.remove(key)
            } },recycleTime)
    }
    // 获取指定对象，并且重置其回收倒计时
    fun get(key : K) : V
    {
        val value = instanceMap[key]
        if(value == null){
            val result = newInstance.invoke(key)
            if(result is IAutoIO)result.load()
            add(key,result)
            return result
        }
        resetTimer(key)
        return value
    }
    fun contains(key : K) : Boolean = instanceMap[key] != null
}