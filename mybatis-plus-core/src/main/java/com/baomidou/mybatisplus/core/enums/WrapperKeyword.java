
package com.baomidou.mybatisplus.core.enums;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * wrapper 内部使用枚举
 *
 * @author miemie
 * @since 2018-07-30
 */
@AllArgsConstructor
public enum WrapperKeyword implements ISqlSegment {
    /**
     * 只用作于辨识,不用于其他
     */
    APPLY(null);

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return keyword;
    }
}
