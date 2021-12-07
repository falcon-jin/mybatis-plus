package com.baomidou.mybatisplus.extension.plugins.inner;


import com.baomidou.mybatisplus.core.toolkit.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.*;

/**
 * 用于输出每条 SQL 语句及其执行时间
 *
 * @author hubin nieqiurong TaoYu
 * @since 2016-07-07
 */
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
        @Signature(type = StatementHandler.class, method = "batch", args = Statement.class)
})
public class SqlLogInterceptor implements Interceptor {
    private static final String DRUID_POOLED_PREPARED_STATEMENT = "com.alibaba.druid.pool.DruidPooledPreparedStatement";
    private static final String T4C_PREPARED_STATEMENT = "oracle.jdbc.driver.T4CPreparedStatement";
    private static final String ORACLE_PREPARED_STATEMENT_WRAPPER = "oracle.jdbc.driver.OraclePreparedStatementWrapper";

    private Method oracleGetOriginalSqlMethod;
    private Method druidGetSqlMethod;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        String originalSql = null;
        List value = null;
        if (Proxy.isProxyClass(firstArg.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(firstArg);

            value =(List) metaObject.getValue("h.columnValues");
            statement = (Statement) metaObject.getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);
        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }
        if (stmtMetaObj.hasGetter("delegate")) {
            //Hikari
            try {
                statement = (Statement) stmtMetaObj.getValue("delegate");
                MetaObject metaObject = SystemMetaObject.forObject(statement);
                if(metaObject.hasGetter("sqlStatement")){
                    String sqlStatement = (String) metaObject.getValue("sqlStatement");
                    originalSql = format(sqlStatement,value);
                }
            } catch (Exception ignored) {

            }
        }

        if (originalSql == null) {
            String stmtClassName = statement.getClass().getName();
            //mysql驱动
            if(stmtClassName.contains("mysql")){
                originalSql = statement.toString();
            }else if (DRUID_POOLED_PREPARED_STATEMENT.equals(stmtClassName)) {
                //druid
                try {
                    if (druidGetSqlMethod == null) {
                        Class<?> clazz = Class.forName(DRUID_POOLED_PREPARED_STATEMENT);
                        druidGetSqlMethod = clazz.getMethod("getSql");
                    }
                    Object stmtSql = druidGetSqlMethod.invoke(statement);
                    if (stmtSql instanceof String) {
                        originalSql = (String) stmtSql;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (T4C_PREPARED_STATEMENT.equals(stmtClassName)
                || ORACLE_PREPARED_STATEMENT_WRAPPER.equals(stmtClassName)) {
                try {
                    if (oracleGetOriginalSqlMethod != null) {
                        Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                        if (stmtSql instanceof String) {
                            originalSql = (String) stmtSql;
                        }
                    } else {
                        Class<?> clazz = Class.forName(stmtClassName);
                        oracleGetOriginalSqlMethod = getMethodRegular(clazz, "getOriginalSql");
                        if (oracleGetOriginalSqlMethod != null) {
                            //OraclePreparedStatementWrapper is not a public class, need set this.
                            oracleGetOriginalSqlMethod.setAccessible(true);
                            if (null != oracleGetOriginalSqlMethod) {
                                Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                                if (stmtSql instanceof String) {
                                    originalSql = (String) stmtSql;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
        }

        if (originalSql == null) {
            originalSql = statement.toString();
        }
        originalSql = originalSql.replaceAll("[\\s]+", StringPool.SPACE);
        int index = indexOfSqlStart(originalSql);
        if (index > 0) {
            originalSql = originalSql.substring(index);
        }

        // 计算执行 SQL 耗时
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;

        // SQL 打印执行结果
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        // 打印 sql
        System.err.println(
                format(
                        "\n==============  Sql Start  ==============" +
                                "\nExecute ID  ：{}" +
                                "\nExecute SQL ：{}" +
                                "\nExecute Time：{} ms" +
                                "\n==============  Sql  End   ==============\n",
                        ms.getId(), originalSql, timing));
        return result;
    }

    private String format(String sqlStatement, List value) {
        String[] split = sqlStatement.split("\\?");
        StringBuilder sb = new StringBuilder(split[0]);
        for (int i = 0; i < value.size(); i++) {
            Object o = value.get(i);
           if(o instanceof String){
               sb.append("'").append(o).append("'").append(split[i+1]);
           }else {
               sb.append(o).append(split[i+1]);
           }
        }
        return sb.toString();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * 获取此方法名的具体 Method
     *
     * @param clazz      class 对象
     * @param methodName 方法名
     * @return 方法
     */
    private Method getMethodRegular(Class<?> clazz, String methodName) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return getMethodRegular(clazz.getSuperclass(), methodName);
    }

    /**
     * 获取sql语句开头部分
     *
     * @param sql ignore
     * @return ignore
     */
    private int indexOfSqlStart(String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }

    private String format(final String strPattern, final Object... argArray) {
        if (Objects.isNull(argArray)||argArray.length==0) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();

        /**
         * 初始化定义好的长度以获得更好的性能
         */
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        /**
         * 记录已经处理到的位置
         */
        int handledPosition = 0;

        /**
         * 占位符所在位置
         */
        int delimIndex;
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(StringPool.EMPTY_JSON, handledPosition);
            /**
             * 剩余部分无占位符
             */
            if (delimIndex == -1) {
                /**
                 * 不带占位符的模板直接返回
                 */
                if (handledPosition == 0) {
                    return strPattern;
                } else {
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            } else {
                /**
                 * 转义符
                 */
                if (delimIndex > 0 && toStr(strPattern.charAt(delimIndex - 1)).equals(StringPool.BACK_SLASH)) {
                    /**
                     * 双转义符
                     */
                    if (delimIndex > 1 &&toStr(strPattern.charAt(delimIndex - 2)).equals(StringPool.BACK_SLASH)) {
                        //转义符之前还有一个转义符，占位符依旧有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(toStr(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    } else {
                        //占位符被转义
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(StringPool.LEFT_BRACE);
                        handledPosition = delimIndex + 1;
                    }
                } else {//正常占位符
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(toStr(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        //加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }

    private  String toStr(Object o) {
        return toStr(o,"");
    }
    private  String toStr(Object str, String defaultValue) {
        if (null == str) {
            return defaultValue;
        }
        return String.valueOf(str);
    }
}
