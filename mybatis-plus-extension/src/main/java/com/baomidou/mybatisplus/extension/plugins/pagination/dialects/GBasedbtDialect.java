
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * GBase 8s V8.8 数据库分页语句组装实现
 * 通用分页版本
 *
 * @author liaojinqing
 * @since 2021-07-20
 */
public class GBasedbtDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql)
            .insert(6, " SKIP " + FIRST_MARK + " FIRST " + SECOND_MARK);
        return new DialectModel(sql.toString(), offset, limit).setConsumerChain();
    }
}
