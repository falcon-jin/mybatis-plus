
package com.baomidou.mybatisplus.extension;

import com.baomidou.mybatisplus.extension.handlers.MybatisMapWrapper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

/**
 * 开启返回map结果集的下划线转驼峰
 *
 * <p> Map 的 key 下划线转驼峰 </p>
 * <p>configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());</p>
 *
 * @author yuxiaobin
 * @since 2017-12-19
 */
public class MybatisMapWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MybatisMapWrapper(metaObject, (Map<String, Object>) object);
    }
}
