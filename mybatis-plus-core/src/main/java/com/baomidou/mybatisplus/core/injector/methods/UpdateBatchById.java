
package com.baomidou.mybatisplus.core.injector.methods;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 根据 ID 批量更新有值字段
 * mysql
 * @author hubin
 * @since 2018-04-06
 */
public class UpdateBatchById extends AbstractMethod {

    public UpdateBatchById() {
        super(SqlMethod.UPDATE_BATCH_BY_ID.getMethod());
    }

    /**
     * @since 3.5.0
     * @param name 方法名
     */
    public UpdateBatchById(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String databaseProductName = "MySQL";
        try {
             databaseProductName = super.configuration.getEnvironment().getDataSource().getConnection()
                .getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if("MySQL".equals(databaseProductName)){
            String  sqlScr = "<script>\nUPDATE %s INNER JOIN (  SELECT  * from (   values <foreach collection=\"recordList\" item=\"item\" index=\"index\" separator=\",\" >  ROW( %s )  </foreach>\n" +
                "    )  as temp ( %s ) ) temp1  on %s.id=temp1.id   <set> %s  </set>  %s\n</script>";
            final String additional = optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
            String tableName = tableInfo.getTableName();
            List<TableFieldInfo> fieldList = tableInfo.getFieldList();
            StringBuilder proSb = new StringBuilder();
            StringBuilder fieldsSb = new StringBuilder();
            StringBuilder updateSb = new StringBuilder();
            proSb.append("#{item.").append( StringUtils.underlineToCamel(tableInfo.getKeyColumn())).append("},");
            fieldsSb.append(tableInfo.getKeyColumn()).append(",");
            for (TableFieldInfo tableFieldInfo : fieldList) {
                String str = StringUtils.underlineToCamel(tableFieldInfo.getColumn());
                proSb.append("#{item.").append(str).append("}").append(",");
                fieldsSb.append(tableFieldInfo.getColumn()).append(",");
                updateSb.append(tableName).append(".").append(tableFieldInfo.getColumn()).append(" = temp1.").append(tableFieldInfo.getColumn()).append(",");
            }
            String props = proSb.toString();
            if(props.endsWith(",")){
                props = props.substring(0,props.length()-1);
            }
            String fields = fieldsSb.toString();
            if(fields.endsWith(",")){
                fields = fields.substring(0,fields.length()-1);
            }
            String update = updateSb.toString();
            if(update.endsWith(",")){
                update = update.substring(0,update.length()-1);
            }

            String sql = String.format(
                sqlScr,
                tableName,//表名
                props,
                fields,
                tableName,
                update,
                additional);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addUpdateMappedStatement(mapperClass, modelClass, "updateBatchById", sqlSource);
        }else {
            SqlMethod sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID;
            final String additional = optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
            List<TableFieldInfo> fieldList = tableInfo.getFieldList();
            StringBuilder proSb = new StringBuilder();
            StringBuilder fieldsSb = new StringBuilder();
            StringBuilder updateSb = new StringBuilder();
            proSb.append("#{item.").append( StringUtils.underlineToCamel(tableInfo.getKeyColumn())).append("},");
            fieldsSb.append(tableInfo.getKeyColumn()).append(",");
            for (TableFieldInfo tableFieldInfo : fieldList) {
                String str = StringUtils.underlineToCamel(tableFieldInfo.getColumn());
                proSb.append("#{item.").append(str).append("}");
                if(tableFieldInfo.getJdbcType().TYPE_CODE==93){
                    proSb.append("::timestamp");
                }
                if(tableFieldInfo.getJdbcType().TYPE_CODE==92){
                    proSb.append("::time");
                }

                proSb.append(",");
                fieldsSb.append(tableFieldInfo.getColumn()).append(",");
                updateSb.append(tableFieldInfo.getColumn()).append(" = temp.").append(tableFieldInfo.getColumn()).append(",");
            }
            String props = proSb.toString();
            if(props.endsWith(",")){
                props = props.substring(0,props.length()-1);
            }
            String fields = fieldsSb.toString();
            if(fields.endsWith(",")){
                fields = fields.substring(0,fields.length()-1);
            }
            String update = updateSb.toString();
            if(update.endsWith(",")){
                update = update.substring(0,update.length()-1);
            }

            String tableName = tableInfo.getTableName();
            String sql = String.format(
                sqlMethod.getSql(),
                tableName,//表名
                update,
                props,
                fields,
                tableName,
                additional);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
        }



    }
}
