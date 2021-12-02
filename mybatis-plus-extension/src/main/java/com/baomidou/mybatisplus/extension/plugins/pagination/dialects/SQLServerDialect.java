
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * SQLServer 数据库分页语句组装实现
 *
 * @author hubin
 * @since 2016-03-23
 */
public class SQLServerDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " OFFSET " + FIRST_MARK + " ROWS FETCH NEXT " + SECOND_MARK + " ROWS ONLY";
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
