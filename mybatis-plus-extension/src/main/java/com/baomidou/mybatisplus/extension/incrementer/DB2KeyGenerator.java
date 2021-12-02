
package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * DB2 Sequence
 *
 * @author Caratacus
 * @since 2017-06-12
 */
public class DB2KeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "values nextval for " + incrementerName;
    }

    @Override
    public DbType dbType() {
        return DbType.DB2;
    }
}
