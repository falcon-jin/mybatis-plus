
package com.baomidou.mybatisplus.extension.plugins.handler;

import net.sf.jsqlparser.expression.Expression;

/**
 * 数据权限处理器
 *
 * @author hubin
 * @since 3.4.1 +
 */
public interface DataPermissionHandler {

    /**
     * 获取数据权限 SQL 片段
     *
     * @param where             待执行 SQL Where 条件表达式
     * @param mappedStatementId Mybatis MappedStatement Id 根据该参数可以判断具体执行方法
     * @return JSqlParser 条件表达式
     */
    Expression getSqlSegment(Expression where, String mappedStatementId);
}
