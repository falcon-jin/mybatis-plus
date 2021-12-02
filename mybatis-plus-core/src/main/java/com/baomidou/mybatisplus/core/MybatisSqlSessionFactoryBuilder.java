
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * 重写SqlSessionFactoryBuilder
 *
 * @author nieqiurong 2019/2/23.
 */
public class MybatisSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    @SuppressWarnings("Duplicates")
    @Override
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            //TODO 这里换成 MybatisXMLConfigBuilder 而不是 XMLConfigBuilder
            MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                reader.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            //TODO 这里换成 MybatisXMLConfigBuilder 而不是 XMLConfigBuilder
            MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    // TODO 使用自己的逻辑,注入必须组件
    @Override
    public SqlSessionFactory build(Configuration configuration) {
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);

        final IdentifierGenerator identifierGenerator;
        if (null == globalConfig.getIdentifierGenerator()) {
            identifierGenerator = new DefaultIdentifierGenerator();
            globalConfig.setIdentifierGenerator(identifierGenerator);
        } else {
            identifierGenerator = globalConfig.getIdentifierGenerator();
        }
        IdWorker.setIdentifierGenerator(identifierGenerator);

        if (globalConfig.isEnableSqlRunner()) {
            new SqlRunnerInjector().inject(configuration);
        }

        SqlSessionFactory sqlSessionFactory = super.build(configuration);

        // 缓存 sqlSessionFactory
        globalConfig.setSqlSessionFactory(sqlSessionFactory);

        return sqlSessionFactory;
    }
}
