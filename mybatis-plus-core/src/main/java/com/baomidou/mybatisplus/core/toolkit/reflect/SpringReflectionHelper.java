
package com.baomidou.mybatisplus.core.toolkit.reflect;

import org.springframework.core.GenericTypeResolver;

/**
 * Spring 反射辅助类
 *
 * @author noear
 * @author hubin
 * @since 2021-09-03
 */
public class SpringReflectionHelper {

    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }
}
