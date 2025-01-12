
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;


/**
 * 快捷构造 chain 式调用的工具类
 *
 * @author miemie
 * @since 2019-11-28
 * @since 3.3.0
 */
public final class ChainWrappers {

    private ChainWrappers() {
        // ignore
    }

    /**
     * 链式查询 普通
     *
     * @return QueryWrapper 的包装类
     */
    public static <T> QueryChainWrapper<T> queryChain(BaseMapper<T> mapper) {
        return new QueryChainWrapper<>(mapper);
    }

    /**
     * 链式查询 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaQueryWrapper 的包装类
     */
    public static <T> LambdaQueryChainWrapper<T> lambdaQueryChain(BaseMapper<T> mapper) {
        return new LambdaQueryChainWrapper<>(mapper);
    }



    /**
     * 链式更改 普通
     *
     * @return UpdateWrapper 的包装类
     */
    public static <T> UpdateChainWrapper<T> updateChain(BaseMapper<T> mapper) {
        return new UpdateChainWrapper<>(mapper);
    }

    /**
     * 链式更改 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaUpdateWrapper 的包装类
     */
    public static <T> LambdaUpdateChainWrapper<T> lambdaUpdateChain(BaseMapper<T> mapper) {
        return new LambdaUpdateChainWrapper<>(mapper);
    }





}
