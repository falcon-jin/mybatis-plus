
package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import kotlin.reflect.KProperty

/**
 * Lambda 语法使用 Wrapper
 *
 * 统一处理解析 lambda 获取 column
 *
 * @author yangyuhan,MieMie,HanChunLin
 * @since 2018-11-07
 */
abstract class AbstractKtWrapper<T, Children : AbstractKtWrapper<T, Children>> : AbstractWrapper<T, KProperty<*>, Children>() {

    /**
     * 列 Map
     */
    protected lateinit var columnMap: Map<String, ColumnCache>

    /**
     * 重载方法，默认 onlyColumn = true
     */
    override fun columnToString(kProperty: KProperty<*>): String? = columnToString(kProperty, true)

    /**
     * 核心实现方法，供重载使用
     */
    private fun columnToString(kProperty: KProperty<*>, onlyColumn: Boolean): String? {
        return columnMap[LambdaUtils.formatKey(kProperty.name)]?.let { if (onlyColumn) it.column else it.columnSelect }
    }

    /**
     * 批量处理传入的属性，正常情况下的输出就像：
     *
     * "user_id" AS "userId" , "user_name" AS "userName"
     */
    fun columnsToString(onlyColumn: Boolean, vararg columns: KProperty<*>): String =
        columns.mapNotNull { columnToString(it, onlyColumn) }.joinToString(separator = StringPool.COMMA)

    override fun initNeed() {
        super.initNeed()
        if (!::columnMap.isInitialized) {
            columnMap = LambdaUtils.getColumnMap(this.entityClass)
        }
    }
}
