
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 根据 whereEntity 条件，更新记录
 *
 * @author hubin
 * @since 2018-04-06
 */
public class Update extends AbstractMethod {

    public Update() {
        super(SqlMethod.UPDATE.getMethod());
    }

    /**
     * @since 3.5.0
     * @param name 方法名
     */
    public Update(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.UPDATE;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
            sqlSet(true, true, tableInfo, true, ENTITY, ENTITY_DOT),
            sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
    }
}
