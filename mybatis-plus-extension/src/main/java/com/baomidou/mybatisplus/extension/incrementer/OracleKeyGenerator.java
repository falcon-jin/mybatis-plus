
package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * Oracle 序列 生成器
 * 主键自增
 * @author hubin
 * @since 2017-05-08
 */
public class OracleKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "SELECT " + incrementerName + ".NEXTVAL FROM DUAL";
    }

    @Override
    public DbType dbType() {
        return DbType.ORACLE;
    }
}
