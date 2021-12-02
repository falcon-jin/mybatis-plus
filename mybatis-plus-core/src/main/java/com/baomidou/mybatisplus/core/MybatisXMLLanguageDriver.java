
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 继承 {@link XMLLanguageDriver} 重装构造函数, 使用自己的 MybatisParameterHandler
 *
 * @author hubin
 * @since 2016-03-11
 */
public class MybatisXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement,
                                                   Object parameterObject, BoundSql boundSql) {
        // 使用 MybatisParameterHandler 而不是 ParameterHandler
        return new MybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        GlobalConfig.DbConfig config = GlobalConfigUtils.getDbConfig(configuration);
        if (config.isReplacePlaceholder()) {
            List<String> find = SqlUtils.findPlaceholder(script);
            if (CollectionUtils.isNotEmpty(find)) {
                try {
                    script = SqlUtils.replaceSqlPlaceholder(script, find, config.getEscapeSymbol());
                } catch (MybatisPlusException e) {
                    throw new IncompleteElementException();
                }
            }
        }
        return super.createSqlSource(configuration, script, parameterType);
    }
}
