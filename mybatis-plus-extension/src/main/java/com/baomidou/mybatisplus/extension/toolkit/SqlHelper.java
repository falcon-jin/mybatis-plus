
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import lombok.SneakyThrows;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SQL 辅助类
 *
 * @author hubin
 * @since 2016-11-06
 */
public final class SqlHelper {

    /**
     * 主要用于 service 和 ar
     */
    public static SqlSessionFactory FACTORY;

    /**
     * 批量操作 SqlSession
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        // TODO 暂时让能用先,但日志会显示Closing non transactional SqlSession,因为这个并没有绑定.
        return sqlSessionFactory(clazz).openSession(ExecutorType.BATCH);
    }

    /**
     * 获取SqlSessionFactory
     *
     * @param clazz 实体类
     * @return SqlSessionFactory
     * @since 3.3.0
     */
    public static SqlSessionFactory sqlSessionFactory(Class<?> clazz) {
        return GlobalConfigUtils.currentSessionFactory(clazz);
    }

    /**
     * 获取Session
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz) {
        return SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(clazz));
    }

    /**
     * 获取TableInfo
     *
     * @param clazz 对象类
     * @return TableInfo 对象表信息
     */
    public static TableInfo table(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        Assert.notNull(tableInfo, "Error: Cannot execute table Method, ClassGenericType not found.");
        return tableInfo;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Long result) {
        return null != result && result >= 1;
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static long retCount(Long result) {
        return (null == result) ? 0 : result;
    }

    /**
     * 从list中取第一条数据返回对应List中泛型的单个结果
     *
     * @param list ignore
     * @param <E>  ignore
     * @return ignore
     */
    public static <E> E getObject(Log log, List<E> list) {
        return getObject(() -> log, list);
    }

    /**
     * @since 3.4.3
     */
    public static <E> E getObject(Supplier<Log> supplier, List<E> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int size = list.size();
            if (size > 1) {
                Log log = supplier.get();
                log.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param consumer    consumer
     * @return 操作结果
     * @since 3.4.0
     */
    @SneakyThrows
    public static boolean executeBatch(Class<?> entityClass, Log log, Consumer<SqlSession> consumer) {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactory(entityClass);
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
        boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
        if (sqlSessionHolder != null) {
            SqlSession sqlSession = sqlSessionHolder.getSqlSession();
            //原生无法支持执行器切换，当存在批量操作时，会嵌套两个session的，优先commit上一个session
            //按道理来说，这里的值应该一直为false。
            sqlSession.commit(!transaction);
        }
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        if (!transaction) {
            log.warn("SqlSession [" + sqlSession + "] Transaction not enabled");
        }
        try {
            consumer.accept(sqlSession);
            //非事务情况下，强制commit。
            sqlSession.commit(!transaction);
            return true;
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof PersistenceException) {
                MyBatisExceptionTranslator myBatisExceptionTranslator
                    = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true);
                Throwable throwable = myBatisExceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
                if (throwable != null) {
                    throw throwable;
                }
            }
            throw ExceptionUtils.mpe(unwrapped);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体类
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param consumer    consumer
     * @param <E>         T
     * @return 操作结果
     * @since 3.4.0
     */
    public static <E> boolean executeBatch(Class<?> entityClass, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return !CollectionUtils.isEmpty(list) && executeBatch(entityClass, log, sqlSession -> {
            int size = list.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            for (E element : list) {
                consumer.accept(sqlSession, element);
                if (i == idxLimit) {
                    sqlSession.flushStatements();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
        });
    }

    /**
     * 优化批量保存方法
     * @param entityClass
     * @param log
     * @param list
     * @param batchSize
     * @param consumer
     * @param <E>
     * @return
     */
    public static <E> boolean executeBatchSave(Class<?> entityClass, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, Collection<E>> consumer) {
        if(batchSize>200){
            batchSize=200;
        }
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        int finalBatchSize = batchSize;
        return !CollectionUtils.isEmpty(list) && executeBatch(entityClass, log, sqlSession -> {
            int size = list.size();
            int idxLimit = Math.min(finalBatchSize, size);
            Collection<Collection<E>> collections = subCollection(list, idxLimit);
            for (Collection<E> collection : collections) {
                consumer.accept(sqlSession, collection);
                sqlSession.flushStatements();
            }
        });
    }
    /**
     * 将一组数据平均分成n组
     *
     * @param collections 要分组的数据源
     * @param maxNum      每组最大数量
     * @param <T>
     * @return
     */

    public static <T> Collection<Collection<T>> subCollection(Collection<T> collections, int maxNum) {

        int limit = (collections.size() + maxNum - 1) / maxNum;
        //方法一：使用流遍历操作
//        Collection<Collection<T>> mglist = new ArrayList<>();
//        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
//            mglist.add(collections.stream().skip(i * maxNum).limit(maxNum).collect(Collectors.toList()));
//        });
//        return mglist;

        //方法二：获取分割后的集合
        Collection<Collection<T>> splitCollection = Stream.iterate(
            0, n -> n + 1).limit(limit).parallel().map(
            a -> collections.stream().skip(a * maxNum).limit(maxNum).parallel()
                .collect(Collectors.toList())).collect(Collectors.toList());

        return splitCollection;
    }


    /**
     * 批量更新或保存
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param predicate   predicate(新增条件) notNull
     * @param consumer    consumer（更新处理） notNull
     * @param <E>         E
     * @return 操作结果
     * @since 3.4.0
     */
    public static <E> boolean saveOrUpdateBatch(Class<?> entityClass, Class<?> mapper, Log log, Collection<E> list, int batchSize, BiPredicate<SqlSession, E> predicate, BiConsumer<SqlSession, E> consumer) {
        String sqlStatement = getSqlStatement(mapper, SqlMethod.INSERT_ONE);
        return executeBatch(entityClass, log, list, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
            } else {
                consumer.accept(sqlSession, entity);
            }
        });
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    public static String getSqlStatement(Class<?> mapper, SqlMethod sqlMethod) {
        return mapper.getName() + StringPool.DOT + sqlMethod.getMethod();
    }


    /**
     * 通过entityClass获取Mapper
     *
     * @param entityClass 实体
     * @param <T>         实体类型
     * @return Mapper
     * @deprecated 使用后未释放连接 {@link SqlHelper#getMapper(Class, SqlSession)}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> BaseMapper<T> getMapper(Class<T> entityClass) {
        Optional.ofNullable(entityClass).orElseThrow(() -> ExceptionUtils.mpe("entityClass can't be null!"));
        TableInfo tableInfo = Optional.ofNullable(TableInfoHelper.getTableInfo(entityClass)).orElseThrow(() -> ExceptionUtils.mpe("Can not find TableInfo from Class: \"%s\".", entityClass.getName()));
        String namespace = tableInfo.getCurrentNamespace();

        Configuration configuration = tableInfo.getConfiguration();
        SqlSession sqlSession = sqlSession(entityClass);
        BaseMapper<T> mapper;
        try {
            mapper = (BaseMapper<T>) configuration.getMapper(Class.forName(namespace), sqlSession);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.mpe(e);
        }
        return mapper;
    }

    /**
     * 通过entityClass获取Mapper，记得要释放连接
     * 例： {@code
     * SqlSession sqlSession = SqlHelper.sqlSession(entityClass);
     * try {
     *     BaseMapper<User> userMapper = getMapper(User.class, sqlSession);
     * } finally {
     *     sqlSession.close();
     * }
     * }
     *
     * @param entityClass 实体
     * @param <T>         实体类型
     * @return Mapper
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseMapper<T> getMapper(Class<T> entityClass, SqlSession sqlSession) {
        Optional.ofNullable(entityClass).orElseThrow(() -> ExceptionUtils.mpe("entityClass can't be null!"));
        TableInfo tableInfo = Optional.ofNullable(TableInfoHelper.getTableInfo(entityClass)).orElseThrow(() -> ExceptionUtils.mpe("Can not find TableInfo from Class: \"%s\".", entityClass.getName()));
        try {
            Configuration configuration = tableInfo.getConfiguration();
            return (BaseMapper<T>) configuration.getMapper(Class.forName(tableInfo.getCurrentNamespace()), sqlSession);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
