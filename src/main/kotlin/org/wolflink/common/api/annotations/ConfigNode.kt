package org.wolflink.common.api.annotations

@Target(AnnotationTarget.FIELD)
annotation class ConfigNode(val root : String = "",val comment : String = "")
