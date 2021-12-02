
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * ORACLE 数据库分页语句组装实现
 * 通用分页版本
 *
 * @author hubin
 * @since 2016-01-23
 */
public class OracleDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        limit = (offset >= 1) ? (offset + limit) : limit;
        String sql = "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( " +
            originalSql + " ) TMP WHERE ROWNUM <=" + FIRST_MARK + ") WHERE ROW_ID > " + SECOND_MARK;
        return new DialectModel(sql, limit, offset).setConsumerChain();
    }
}
