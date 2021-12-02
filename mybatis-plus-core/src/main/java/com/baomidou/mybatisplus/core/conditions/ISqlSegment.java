
package com.baomidou.mybatisplus.core.conditions;


import java.io.Serializable;

/**
 * SQL 片段接口
 *
 * @author hubin miemie HCL
 * @since 2018-05-28
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * SQL 片段
     */
    String getSqlSegment();

}
