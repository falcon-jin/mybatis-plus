
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * 查询满足条件所有数据（并翻页）
 *
 * @author hubin
 * @since 2018-04-06
 */
public class SelectMapsPage extends AbstractMethod {

    public SelectMapsPage() {
        super(SqlMethod.SELECT_MAPS_PAGE.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public SelectMapsPage(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_MAPS_PAGE;
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlSelectColumns(tableInfo, true),
            tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo), sqlOrderBy(tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, getMethod(sqlMethod), sqlSource, Map.class);
    }
}
