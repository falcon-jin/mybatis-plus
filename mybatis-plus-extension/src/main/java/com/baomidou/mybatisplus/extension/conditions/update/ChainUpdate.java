
package com.baomidou.mybatisplus.extension.conditions.update;

import com.baomidou.mybatisplus.extension.conditions.ChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * 具有更新方法的定义
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainUpdate<T> extends ChainWrapper<T> {

    /**
     * 更新数据
     *
     * @return 是否成功
     */
    default boolean update() {
        return update(null);
    }

    /**
     * 更新数据
     *
     * @param entity 实体类
     * @return 是否成功
     */
    default boolean update(T entity) {
        return SqlHelper.retBool(getBaseMapper().update(entity, getWrapper()));
    }

    /**
     * 删除数据
     *
     * @return 是否成功
     */
    default boolean remove() {
        return SqlHelper.retBool(getBaseMapper().delete(getWrapper()));
    }
}
