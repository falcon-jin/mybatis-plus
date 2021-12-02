
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * ORACLE 新版数据库分页语句组装实现
 *
 * @author 廖仑辉
 * @since 2019-11-29
 */
public class Oracle12cDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " OFFSET " + FIRST_MARK + " ROWS FETCH NEXT " + SECOND_MARK + " ROWS ONLY";
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
