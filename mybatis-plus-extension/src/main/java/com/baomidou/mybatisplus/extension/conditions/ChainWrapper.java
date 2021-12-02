
package com.baomidou.mybatisplus.extension.conditions;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 此接口没特殊意义,只是为了减少实现类的代码量,主要在 AbstractChainWrapper 抽象类上实现
 * <p>以及 继承该接口的子接口能直接获取到 BaseMapper 和相应的 Wrapper</p>
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainWrapper<T> {

    /**
     * 获取 BaseMapper
     *
     * @return BaseMapper
     */
    BaseMapper<T> getBaseMapper();

    /**
     * 获取最终拿去执行的 Wrapper
     *
     * @return Wrapper
     */
    Wrapper<T> getWrapper();
}
