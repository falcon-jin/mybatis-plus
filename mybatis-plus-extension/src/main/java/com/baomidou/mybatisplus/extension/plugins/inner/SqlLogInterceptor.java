package com.baomidou.mybatisplus.extension.plugins.inner;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;


import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 用于输出每条 SQL 语句及其执行时间
 */
@Slf4j
@Intercepts(
    {
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
    }
)
public class SqlLogInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object target = invocation.getTarget();
            if (target instanceof StatementHandler) {

                MetaObject metaObject = SystemMetaObject.forObject(target);
                MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
                BoundSql boundSql = ((StatementHandler) target).getBoundSql();
                String sql = showSql(ms.getConfiguration(), boundSql, 1L, ms.getId());
                long start = SystemClock.now();
                Object result = invocation.proceed();
                long timing = SystemClock.now() - start;
                System.err.println(this.format("\n==============  Sql Start  ==============\nExecute ID  ：{}\nExecute SQL ：{}\nExecute Time：{} ms\n==============  Sql  End   ==============\n", ms.getId(), sql, timing));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invocation.proceed();
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
            delimIndex = strPattern.indexOf("{}", handledPosition);
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
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    private  String showSql(Configuration configuration, BoundSql boundSql, long time, String sqlId){
        Object parameterObject = boundSql.getParameterObject();

        List parameterMappings = boundSql.getParameterMappings();

//替换空格、换行、tab缩进等

        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);

                for (Object parameterMapping : parameterMappings) {
                    String propertyName = ((ParameterMapping)parameterMapping).getProperty();

                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);

                        sql = sql.replaceFirst("\\?", getParameterValue(obj));

                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);

                        sql = sql.replaceFirst("\\?", getParameterValue(obj));

                    }

                }

            }

        }
        return sql;

    }
    private  String getParameterValue(Object obj){
        String value;

        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";

        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);

            value = "'" + formatter.format(new Date()) + "'";

        } else {
            if (obj != null) {
                value = obj.toString();

            } else {
                value = "null";

            }

        }

        return value.replace("$", "\\$");

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
