
package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * Postgres 序列
 * 主键自增
 * @author Caratacus
 * @since 2017-06-12
 */
public class PostgreKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "select nextval('" + incrementerName + "')";
    }

    @Override
    public DbType dbType() {
        return DbType.POSTGRE_SQL;
    }
}
