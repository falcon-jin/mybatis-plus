
package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Gson 实现 JSON 字段类型处理器
 *
 * @author hubin
 * @since 2019-08-25
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class GsonTypeHandler extends AbstractJsonTypeHandler<Object> {
    private static Gson GSON;
    private final Class<?> type;

    public GsonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("GsonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        return getGson().fromJson(json, type);
    }

    @Override
    protected String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    public static Gson getGson() {
        if (null == GSON) {
            GSON = new Gson();
        }
        return GSON;
    }

    public static void setGson(Gson gson) {
        Assert.notNull(gson, "Gson should not be null");
        GsonTypeHandler.GSON = gson;
    }
}
