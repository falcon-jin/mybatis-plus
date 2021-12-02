
package com.baomidou.mybatisplus.core.incrementer;

import com.baomidou.mybatisplus.core.toolkit.Sequence;

import java.net.InetAddress;

/**
 * 默认生成器
 *
 * @author  nieqiuqiu
 * @since 2019-10-15
 * @since 3.3.0
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {
    private final Sequence sequence;

    public DefaultIdentifierGenerator() {
        this.sequence = new Sequence(null);
    }

    public DefaultIdentifierGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public DefaultIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public DefaultIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }
}
