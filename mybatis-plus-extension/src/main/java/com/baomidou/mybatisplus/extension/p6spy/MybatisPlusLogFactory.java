
package com.baomidou.mybatisplus.extension.p6spy;

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

/**
 * 扩展 p6spy
 *
 * @author nieqiurong
 * @since 2019-11-10
 */
public class MybatisPlusLogFactory implements P6Factory {

    @Override
    public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
        return new P6LogOptions(optionsRepository);
    }

    @Override
    public JdbcEventListener getJdbcEventListener() {
        return MybatisPlusLoggingEventListener.getInstance();
    }
}
