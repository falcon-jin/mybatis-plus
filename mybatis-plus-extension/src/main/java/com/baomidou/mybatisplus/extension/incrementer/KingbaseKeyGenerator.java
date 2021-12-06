
package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * Kingbase 序列
 * 主键自增
 * @author kingbase
 * @since 2019-10-17
 */
public class KingbaseKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "select nextval('" + incrementerName + "')";
    }

    @Override
    public DbType dbType() {
        return DbType.KINGBASE_ES;
    }
}
