package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

/**
 * @author falcon
 * @version 1.0.0
 * @ClassName InsertBatch
 * @Description TODO
 * @createTime 2021年05月31日 17:51:00
 */
public class InsertBatch extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        //  INSERT_BATCH("insertBatch", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>"),;
        SqlMethod sqlMethod = SqlMethod.INSERT_BATCH;
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        //foreach中的字段
        StringBuilder sb = new StringBuilder("#{item."+tableInfo.getKeyProperty()+"},");
        //插入的列
        StringBuilder colSb = new StringBuilder(tableInfo.getKeyColumn()+",");
        //值字段
        fieldList.stream().forEach(field->{
            boolean logicDelete = field.isLogicDelete();
            //给逻辑删除字段赋值
            if(logicDelete){
                sb.append("'").append(field.getLogicNotDeleteValue()).append("'").append(COMMA);
            }else {
                sb.append(field.getInsertSqlProperty("item."));
            }

            colSb.append(field.getInsertSqlColumn());
        });
        //列字段
        String sqlScript = colSb.toString();
        //获取 带 trim 标签的脚本
        String columnScript = SqlScriptUtils.convertTrim(sqlScript.substring(0,sqlScript.length()-1),
                LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String allInsertSqlPropertyMaybeIf = sb.toString();
        allInsertSqlPropertyMaybeIf= "("+allInsertSqlPropertyMaybeIf.substring(0,allInsertSqlPropertyMaybeIf.length()-1)+")";
        //完整的foreach
        String valuesScript = SqlScriptUtils.convertForeach(allInsertSqlPropertyMaybeIf,"list",null,"item", COMMA);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /** 自增主键 */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(sqlMethod.getMethod(), tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        //拼接完后的sql
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass,sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
