
package com.baomidou.mybatisplus.core;

import org.apache.ibatis.builder.annotation.MethodResolver;

import java.lang.reflect.Method;

/**
 * 继承 {@link MethodResolver}
 *
 * @author miemie
 * @since 2019-01-05
 */
public class MybatisMethodResolver extends MethodResolver {

    private final MybatisMapperAnnotationBuilder annotationBuilder;
    private final Method method;

    public MybatisMethodResolver(MybatisMapperAnnotationBuilder annotationBuilder, Method method) {
        super(null, null);
        this.annotationBuilder = annotationBuilder;
        this.method = method;
    }

    @Override
    public void resolve() {
        annotationBuilder.parseStatement(method);
    }
}
