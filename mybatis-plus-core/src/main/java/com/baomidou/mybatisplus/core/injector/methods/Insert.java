/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 插入一条数据（选择字段插入）
 *
 * @author hubin
 * @since 2018-04-06
 */
public class Insert extends AbstractMethod {

    public Insert() {
        this(SqlMethod.INSERT_ONE.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public Insert(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
        String columnScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String valuesScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
