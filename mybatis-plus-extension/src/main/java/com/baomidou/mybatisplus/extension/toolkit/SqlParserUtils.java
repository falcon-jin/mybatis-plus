
package com.baomidou.mybatisplus.extension.toolkit;

/**
 * SQL 解析工具类
 *
 * @author hubin
 * @since 2018-07-22
 */
public class SqlParserUtils {

    /**
     * 获取 COUNT 原生 SQL 包装
     *
     * @param originalSql ignore
     * @return ignore
     */
    public static String getOriginalCountSql(String originalSql) {
        return String.format("SELECT COUNT(*) FROM (%s) TOTAL", originalSql);
    }
}
