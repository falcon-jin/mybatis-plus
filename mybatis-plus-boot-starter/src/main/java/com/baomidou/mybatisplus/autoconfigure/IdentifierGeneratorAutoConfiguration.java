
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author nieqiurong 2021/1/29
 * @since 3.4.3
 */
@Lazy
@Configuration(proxyBeanMethods = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class IdentifierGeneratorAutoConfiguration {


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(InetUtils.class)
    public static class InetUtilsAutoConfig {

        @Bean
        @ConditionalOnMissingBean
        public IdentifierGenerator identifierGenerator(InetUtils inetUtils) {
            return new DefaultIdentifierGenerator(inetUtils.findFirstNonLoopbackAddress());
        }

    }

}
