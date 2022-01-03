
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * 根据columnMap 查询一条数据
 *
 * @author hubin
 * @since 2018-04-06
 */
public class SelectByMap extends AbstractMethod {

    public SelectByMap() {
        super(SqlMethod.SELECT_BY_MAP.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public SelectByMap(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, false),
            tableInfo.getTableName(), sqlWhereByMap(tableInfo));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        return this.addSelectMappedStatementForTable(mapperClass, getMethod(sqlMethod), sqlSource, tableInfo);
    }
}
