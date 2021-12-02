
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * DB2 数据库分页方言
 *
 * @author hubin
 * @since 2016-11-10
 */
public class DB2Dialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        String sql = "SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS ROW_ID FROM ( " + originalSql +
            " ) AS TMP_PAGE) TMP_PAGE WHERE ROW_ID BETWEEN " + FIRST_MARK + " AND " + SECOND_MARK;
        return new DialectModel(sql, firstParam, secondParam).setConsumerChain();
    }
}
