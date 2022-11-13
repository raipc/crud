package io.githib.raipc.crud.util

import com.univocity.parsers.csv.CsvWriter
import com.univocity.parsers.csv.CsvWriterSettings
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class BeanCsvWriter<T> private constructor(
    private val props: Array<KProperty1<Any, *>>,
    private val header: Array<String>
) {
    private fun beanToArray(bean: Any, out: Array<String>) {
        props.forEachIndexed { index, prop ->
            out[index] = Objects.toString(prop.get(bean), "")
        }
    }

    fun writeBeans(beans: List<T>, out: OutputStream) {
        CsvWriter(out, StandardCharsets.UTF_8, CsvWriterSettings().apply {
            setHeaders(*header)
        }).apply {
            writeHeaders()
            if (beans.isNotEmpty()) {
                val array = Array(props.size) { _ -> "" }
                beans.forEach {
                    beanToArray(it as Any, array)
                    writeRow(*array)
                }
            }
            close()
        }
    }

    companion object {
        fun <T> create(clazz: KClass<*>): BeanCsvWriter<T> {
            val namesInOrder = if (clazz.isData) {
                clazz.constructors.maxBy { it.parameters.size }.parameters.mapNotNull { it.name }
            } else {
                clazz.memberProperties.map { it.name }
            }.toTypedArray()
            val propsByName = clazz.memberProperties.associateBy { it.name }
            @Suppress("UNCHECKED_CAST")
            val propsArray = namesInOrder.map { propsByName[it] as KProperty1<Any, *> }.toTypedArray()
            return BeanCsvWriter(propsArray, namesInOrder)
        }
    }
}