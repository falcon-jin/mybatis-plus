
package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * 内置插件的一些过滤规则
 * <p>
 * 支持注解在 Mapper 上以及 Mapper.Method 上
 * 同时存在则 Mapper.method 比 Mapper 优先级高
 * <p>
 * 支持:
 * true 和 false , 1 和 0 , on 和 off
 * <p>
 * 各属性返回 true 表示不走插件(在配置了插件的情况下,不填则默认表示 false)
 *
 * @author miemie
 * @since 2020-07-31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InterceptorIgnore {

    /**
     * 行级租户 {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor}
     */
    String tenantLine() default "";

    /**
     * 动态表名 {@link com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor}
     */
    String dynamicTableName() default "";

    /**
     * 攻击 SQL 阻断解析器,防止全表更新与删除 {@link com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor}
     */
    String blockAttack() default "";

    /**
     * 垃圾SQL拦截 {@link com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor}
     */
    String illegalSql() default "";

    /**
     * 数据权限 {@link com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor}
     * <p>
     * 默认关闭，需要注解打开
     */
    String dataPermission() default "1";

    /**
     * 分表 {@link com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor}
     */
    String sharding() default "";

    /**
     * 其他的
     * <p>
     * 格式应该为:  "key"+"@"+可选项[false,true,1,0,on,off]
     * 例如: "xxx@1" 或 "xxx@true" 或 "xxx@on"
     * <p>
     * 如果配置了该属性的注解是注解在 Mapper 上的,则如果该 Mapper 的一部分 Method 需要取反则需要在 Method 上注解并配置此属性为反值
     * 例如: "xxx@1" 在 Mapper 上, 则 Method 上需要 "xxx@0"
     */
    String[] others() default {};
}
