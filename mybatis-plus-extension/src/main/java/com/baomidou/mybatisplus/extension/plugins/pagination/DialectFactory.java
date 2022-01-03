
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * 分页方言工厂类
 *
 * @author hubin
 * @since 2016-01-23
 */
public class DialectFactory {
    private static final Map<DbType, IDialect> DIALECT_ENUM_MAP = new EnumMap<>(DbType.class);

    public static IDialect getDialect(DbType dbType) {
        IDialect dialect = DIALECT_ENUM_MAP.get(dbType);
        if (null == dialect) {
            if (dbType == DbType.OTHER) {
                ExceptionUtils.mpe("%s database not supported.", dbType.getDb());
            }
            // mysql same type
            else if (dbType == DbType.MYSQL
                || dbType == DbType.MARIADB
                || dbType == DbType.GBASE
                || dbType == DbType.OSCAR
                || dbType == DbType.XU_GU
                || dbType == DbType.CLICK_HOUSE
                || dbType == DbType.OCEAN_BASE
                || dbType == DbType.CUBRID
                || dbType == DbType.GOLDILOCKS
                || dbType == DbType.CSIIDB) {
                dialect = new MySqlDialect();
            }
            // oracle same type
            else if (dbType == DbType.ORACLE
                || dbType == DbType.DM
                || dbType == DbType.GAUSS) {
                dialect = new OracleDialect();
            }
            // postgresql same type
            else if (dbType == DbType.POSTGRE_SQL
                || dbType == DbType.H2
                || dbType == DbType.SQLITE
                || dbType == DbType.HSQL
                || dbType == DbType.KINGBASE_ES
                || dbType == DbType.PHOENIX
                || dbType == DbType.SAP_HANA) {
                dialect = new PostgreDialect();
            } else if (dbType == DbType.HIGH_GO) {
                dialect = new HighGoDialect();
            } else if (dbType == DbType.ORACLE_12C) {
                dialect = new Oracle12cDialect();
            } else if (dbType == DbType.DB2) {
                dialect = new DB2Dialect();
            } else if (dbType == DbType.SQL_SERVER2005) {
                dialect = new SQLServer2005Dialect();
            } else if (dbType == DbType.SQL_SERVER) {
                dialect = new SQLServerDialect();
            } else if (dbType == DbType.SYBASE) {
                dialect = new SybaseDialect();
            } else if (dbType == DbType.GBASEDBT){
                dialect = new GBasedbtDialect();
            }
            DIALECT_ENUM_MAP.put(dbType, dialect);
        }
        return dialect;
    }
}
