
package com.baomidou.mybatisplus.extension.conditions.update;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;

/**
 * Lambda Update Chain Wrapper
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial"})
public class LambdaUpdateChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdateChainWrapper<T>, LambdaUpdateWrapper<T>>
    implements ChainUpdate<T>, Update<LambdaUpdateChainWrapper<T>, SFunction<T, ?>> {

    private final BaseMapper<T> baseMapper;

    public LambdaUpdateChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaUpdateWrapper<>();
    }

    @Override
    public LambdaUpdateChainWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        wrapperChildren.set(condition, column, val, mapping);
        return typedThis;
    }

    @Override
    public LambdaUpdateChainWrapper<T> setSql(boolean condition, String sql) {
        wrapperChildren.setSql(condition, sql);
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSet");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }
}
