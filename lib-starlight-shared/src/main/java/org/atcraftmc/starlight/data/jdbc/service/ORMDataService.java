package org.atcraftmc.starlight.data.jdbc.service;

import com.baomidou.mybatisplus.core.batch.BatchSqlSession;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.shared.service.JDBCService;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class ORMDataService extends JDBCDataService {
    private SqlSessionFactory sessionFactory;

    @Override
    public void init(DataSource datasource, JDBCService service) {
        super.init(datasource, service);
        this.sessionFactory = ((JDBCDataSource) datasource).getSessionFactory();
    }

    public <I> CloseableMapper<I> getDataMapper(Class<? extends BaseMapper<I>> dataMapper) {
        return new CloseableMapperImpl<>(this, dataMapper);
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public interface CloseableMapper<T> extends BaseMapper<T>, AutoCloseable {
    }

    private static final class CloseableMapperImpl<T> implements CloseableMapper<T> {
        private final ORMDataService service;
        private final SqlSession session;
        private final BaseMapper<T> mapper;

        public CloseableMapperImpl(ORMDataService service, Class<? extends BaseMapper<T>> mapperType, Function<SqlSessionFactory, SqlSession> opener) {
            this.service = service;
            this.session = opener.apply(service.getSessionFactory());

            var conf = service.sessionFactory.getConfiguration();
            if (!conf.hasMapper(mapperType)) {
                conf.addMapper(mapperType);
            }

            this.mapper = this.session.getMapper(mapperType);
        }

        public CloseableMapperImpl(ORMDataService service, Class<? extends BaseMapper<T>> mapperType) {
            this(service, mapperType, (f) -> f.openSession(true));
        }

        @Override
        public int insert(T entity) {
            return mapper.insert(entity);
        }

        @Override
        public int deleteById(Serializable id) {
            return mapper.deleteById(id);
        }

        @Override
        public int deleteById(Object obj, boolean useFill) {
            return mapper.deleteById(obj, useFill);
        }

        @Override
        public int deleteById(T entity) {
            return mapper.deleteById(entity);
        }

        @Override
        public int deleteByMap(Map<String, Object> columnMap) {
            return mapper.deleteByMap(columnMap);
        }

        @Override
        public int delete(Wrapper<T> queryWrapper) {
            return mapper.delete(queryWrapper);
        }

        @Deprecated
        @Override
        public int deleteBatchIds(Collection<?> idList) {
            return mapper.deleteBatchIds(idList);
        }

        @Override
        public int deleteByIds(Collection<?> idList) {
            return mapper.deleteByIds(idList);
        }

        @Override
        public int deleteByIds(Collection<?> collections, boolean useFill) {
            return mapper.deleteByIds(collections, useFill);
        }

        @Override
        public int updateById(T entity) {
            return mapper.updateById(entity);
        }

        @Override
        public int update(T entity, Wrapper<T> updateWrapper) {
            return mapper.update(entity, updateWrapper);
        }

        @Override
        public int update(Wrapper<T> updateWrapper) {
            return mapper.update(updateWrapper);
        }

        @Override
        public T selectById(Serializable id) {
            return mapper.selectById(id);
        }

        @Override
        public List<T> selectByIds(Collection<? extends Serializable> idList) {
            return mapper.selectByIds(idList);
        }

        @Override
        public List<T> selectBatchIds(Collection<? extends Serializable> idList) {
            return mapper.selectBatchIds(idList);
        }

        @Override
        public void selectByIds(Collection<? extends Serializable> idList, ResultHandler<T> resultHandler) {
            mapper.selectByIds(idList, resultHandler);
        }

        @Deprecated
        @Override
        public void selectBatchIds(Collection<? extends Serializable> idList, ResultHandler<T> resultHandler) {
            mapper.selectBatchIds(idList, resultHandler);
        }

        @Override
        public List<T> selectByMap(Map<String, Object> columnMap) {
            return mapper.selectByMap(columnMap);
        }

        @Override
        public void selectByMap(Map<String, Object> columnMap, ResultHandler<T> resultHandler) {
            mapper.selectByMap(columnMap, resultHandler);
        }

        @Override
        public T selectOne(Wrapper<T> queryWrapper) {
            return mapper.selectOne(queryWrapper);
        }

        @Override
        public T selectOne(Wrapper<T> queryWrapper, boolean throwEx) {
            return mapper.selectOne(queryWrapper, throwEx);
        }

        @Override
        public boolean exists(Wrapper<T> queryWrapper) {
            return mapper.exists(queryWrapper);
        }

        @Override
        public Long selectCount(Wrapper<T> queryWrapper) {
            return mapper.selectCount(queryWrapper);
        }

        @Override
        public List<T> selectList(Wrapper<T> queryWrapper) {
            return mapper.selectList(queryWrapper);
        }

        @Override
        public void selectList(Wrapper<T> queryWrapper, ResultHandler<T> resultHandler) {
            mapper.selectList(queryWrapper, resultHandler);
        }

        @Override
        public List<T> selectList(IPage<T> page, Wrapper<T> queryWrapper) {
            return mapper.selectList(page, queryWrapper);
        }

        @Override
        public void selectList(IPage<T> page, Wrapper<T> queryWrapper, ResultHandler<T> resultHandler) {
            mapper.selectList(page, queryWrapper, resultHandler);
        }

        @Override
        public List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper) {
            return mapper.selectMaps(queryWrapper);
        }

        @Override
        public void selectMaps(Wrapper<T> queryWrapper, ResultHandler<Map<String, Object>> resultHandler) {
            mapper.selectMaps(queryWrapper, resultHandler);
        }

        @Override
        public List<Map<String, Object>> selectMaps(IPage<? extends Map<String, Object>> page, Wrapper<T> queryWrapper) {
            return mapper.selectMaps(page, queryWrapper);
        }

        @Override
        public void selectMaps(IPage<? extends Map<String, Object>> page, Wrapper<T> queryWrapper, ResultHandler<Map<String, Object>> resultHandler) {
            mapper.selectMaps(page, queryWrapper, resultHandler);
        }

        @Override
        public <E> List<E> selectObjs(Wrapper<T> queryWrapper) {
            return mapper.selectObjs(queryWrapper);
        }

        @Override
        public <E> void selectObjs(Wrapper<T> queryWrapper, ResultHandler<E> resultHandler) {
            mapper.selectObjs(queryWrapper, resultHandler);
        }

        @Override
        public <P extends IPage<T>> P selectPage(P page, Wrapper<T> queryWrapper) {
            return mapper.selectPage(page, queryWrapper);
        }

        @Override
        public <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, Wrapper<T> queryWrapper) {
            return mapper.selectMapsPage(page, queryWrapper);
        }

        @Override
        public boolean insertOrUpdate(T entity) {
            return mapper.insertOrUpdate(entity);
        }

        @Override
        public List<BatchResult> insert(Collection<T> entityList) {
            return mapper.insert(entityList);
        }

        @Override
        public List<BatchResult> insert(Collection<T> entityList, int batchSize) {
            return mapper.insert(entityList, batchSize);
        }

        @Override
        public List<BatchResult> updateById(Collection<T> entityList) {
            return mapper.updateById(entityList);
        }

        @Override
        public List<BatchResult> updateById(Collection<T> entityList, int batchSize) {
            return mapper.updateById(entityList, batchSize);
        }

        @Override
        public List<BatchResult> insertOrUpdate(Collection<T> entityList) {
            return mapper.insertOrUpdate(entityList);
        }

        @Override
        public List<BatchResult> insertOrUpdate(Collection<T> entityList, int batchSize) {
            return mapper.insertOrUpdate(entityList, batchSize);
        }

        @Override
        public List<BatchResult> insertOrUpdate(Collection<T> entityList, BiPredicate<BatchSqlSession, T> insertPredicate) {
            return mapper.insertOrUpdate(entityList, insertPredicate);
        }

        @Override
        public List<BatchResult> insertOrUpdate(Collection<T> entityList, BiPredicate<BatchSqlSession, T> insertPredicate, int batchSize) {
            return mapper.insertOrUpdate(entityList, insertPredicate, batchSize);
        }

        @Override
        public void close() {
            this.session.close();
        }

        public ORMDataService getService() {
            return service;
        }
    }
}
