
package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * SQL 默认注入器
 *
 * @author hubin
 * @since 2018-04-10
 */
public class DefaultSqlInjector extends AbstractSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        if (tableInfo.havePK()) {
            return Stream.of(
                new InsertBatch(),
                new Insert(),
                new Delete(),
                new DeleteByMap(),
                new DeleteById(),
                new DeleteBatchByIds(),
                new Update(),
                new UpdateById(),
                new SelectById(),
                new SelectBatchByIds(),
                new SelectByMap(),
                new SelectCount(),
                new SelectMaps(),
                new SelectMapsPage(),
                new SelectObjs(),
                new SelectList(),
                new SelectPage()
            ).collect(toList());
        } else {
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                tableInfo.getEntityType()));
            return Stream.of(
                new InsertBatch(),
                new Insert(),
                new Delete(),
                new DeleteByMap(),
                new Update(),
                new SelectByMap(),
                new SelectCount(),
                new SelectMaps(),
                new SelectMapsPage(),
                new SelectObjs(),
                new SelectList(),
                new SelectPage()
            ).collect(toList());
        }
    }
}
