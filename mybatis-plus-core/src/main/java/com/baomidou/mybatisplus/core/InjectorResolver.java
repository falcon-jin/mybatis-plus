
package com.baomidou.mybatisplus.core;

import org.apache.ibatis.builder.annotation.MethodResolver;

/**
 * 继承 {@link MethodResolver}
 *
 * @author miemie
 * @since 2019-01-05
 */
public class InjectorResolver extends MethodResolver {

    private final MybatisMapperAnnotationBuilder annotationBuilder;

    public InjectorResolver(MybatisMapperAnnotationBuilder annotationBuilder) {
        super(annotationBuilder, null);
        this.annotationBuilder = annotationBuilder;
    }

    @Override
    public void resolve() {
        annotationBuilder.parserInjector();
    }
}
