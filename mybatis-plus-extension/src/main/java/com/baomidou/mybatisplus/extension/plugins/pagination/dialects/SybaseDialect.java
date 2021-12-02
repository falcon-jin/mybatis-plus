
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * sybase 数据库分页方言
 *
 * @author sdsyjh
 * @since 2020-07-30
 */
public class SybaseDialect implements IDialect {

    private final boolean hasTop; // sybase12.5.4以前，不支持select top

    public SybaseDialect() {
        this(false);
    }

    public SybaseDialect(boolean hasTop) {
        this.hasTop = hasTop;
    }

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        int index = originalSql.indexOf("FROM");
        String sql = "select";
        if (hasTop) {
            sql += " top " + (offset + limit);
        }
        sql += " rownum=identity(12)," + originalSql.substring(6, index) + " into #t " + originalSql.substring(index);
        sql += " select * from #t where rownum > ? and rownum <= ? ";
        sql += "drop table #t ";
        return new DialectModel(sql, offset, offset + limit).setConsumerChain();
    }
}
