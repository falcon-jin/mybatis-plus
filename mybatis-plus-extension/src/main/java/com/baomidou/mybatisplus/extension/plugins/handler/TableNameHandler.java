
package com.baomidou.mybatisplus.extension.plugins.handler;

/**
 * 动态表名处理器
 *
 * @author miemie
 * @since 3.4.0
 */
public interface TableNameHandler {

    /**
     * 生成动态表名
     *
     * @param sql       当前执行 SQL
     * @param tableName 表名
     * @return String
     */
    String dynamicTableName(String sql, String tableName);
}
