package com.rest.kouponify.common;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;

/**
 * Created by vijay on 2/15/16.
 * Implementation of {@link PagingAndSortingRepository} using {@link JdbcTemplate}
 */

public abstract class GenericDaoImpl<T extends PersistableEntity, ID extends Serializable>
implements
PagingAndSortingRepository<T, ID>,
InitializingBean,
BeanFactoryAware {


    private final TableDescription table;

    private final RowMapper<T> rowMapper;
    private final RowUnmapper<T> rowUnmapper;

    protected SqlGenerator sqlGenerator = new SqlGenerator();
    private BeanFactory beanFactory;
    protected JdbcOperations jdbcOperations;

    public GenericDaoImpl(final TableDescription table) {
        Assert.notNull(table);

        this.rowUnmapper = entity -> {
            final Map<String, Object> unmapper = rowUnmapper(entity);
            return unmapper;
        };
        this.rowMapper = (rs, rowNum) -> {

            final T mapper = rowMapper(rs);
            return mapper;
        };
        this.sqlGenerator = new SqlGenerator();
        this.table = table;
    }

    protected void rowUnmapperForSystemColumns(final Map<String, Object> unmapper, final T entity) {
        unmapper.put("created_by", entity.getCreatedBy());
        unmapper.put("updated_by", entity.getUpdatedBy());
        unmapper.put("created_at", entity.getCreatedAt());
        unmapper.put("updated_at", entity.getUpdatedAt());
    }

    protected void rowMapperForSystemColumns(final T entity, final ResultSet rs) throws SQLException {
        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setUpdatedAt(rs.getLong("updated_at"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setUpdatedBy(rs.getLong("updated_by"));
    }

    public GenericDaoImpl(final String tabeName, final String idColumn) {
        this(new TableDescription(tabeName, idColumn));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        obtainJdbcTemplate();
    }

    public void setSqlGenerator(final SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }

    public void setJdbcOperations(final JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    protected JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    protected TableDescription getTable() {
        return table;
    }

    private void obtainJdbcTemplate() {
        try {
            jdbcOperations = beanFactory.getBean(JdbcOperations.class);
        } catch (final NoSuchBeanDefinitionException e) {
            final DataSource dataSource = beanFactory.getBean(DataSource.class);
            jdbcOperations = new JdbcTemplate(dataSource);
        }
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public long count() {
        return jdbcOperations.queryForObject(sqlGenerator.count(table), Long.class);
    }

    @Override
    public void delete(final ID id) {
        jdbcOperations.update(sqlGenerator.deleteById(table), idToObjectArray(id));
    }

    @Override
    public void delete(final T entity) {
        jdbcOperations.update(sqlGenerator.deleteById(table), entity.getId());
    }

    @Override
    public void delete(final Iterable<? extends T> entities) {
        for (final T t : entities) {
            delete(t);
        }
    }

    @Override
    public void deleteAll() {
        jdbcOperations.update(sqlGenerator.deleteAll(table));
    }

    @Override
    public boolean exists(final ID id) {
        return jdbcOperations.queryForObject(sqlGenerator.countById(table), Integer.class, idToObjectArray(id)) > 0;
    }

    @Override
    public List<T> findAll() {
        return jdbcOperations.query(sqlGenerator.selectAll(table), rowMapper);
    }

    @Override
    public T findOne(final ID id) {
        final Object[] idColumns = idToObjectArray(id);
        final List<T> entityOrEmpty = jdbcOperations.query(sqlGenerator.selectById(table), idColumns, rowMapper);
        return entityOrEmpty.isEmpty() ? null : entityOrEmpty.get(0);
    }

    public List<T> search(final String query, final Object[] args) {
        return jdbcOperations.query(query, args, rowMapper);
    }

    public List<T> search(final String query) {
        return jdbcOperations.query(query, rowMapper);
    }

    private static <ID> Object[] idToObjectArray(final ID id) {
        if (id instanceof Object[]) {
            return (Object[]) id;
        }
        return new Object[]{id};
    }

    private static <ID> List<Object> idToObjectList(final ID id) {
        if (id instanceof Object[]) {
            return Arrays.asList((Object[]) id);
        }
        return Collections.<Object> singletonList(id);
    }

    @Override
    public <S extends T> S save(final S entity) {

        if (entity.isNew()) {
            return create(entity);
        }
        return update(entity);
    }

    protected <S extends T> S update(final S entity) {
        // LOGGER.info("Performing update : " + ServiceHelper.toFormattedJson(entity));
        final Map<String, Object> columns = preUpdate(entity, columns(entity));
        final List<Object> idValues = removeIdColumns(columns);
        final String updateQuery = sqlGenerator.updateById(table, new ArrayList<String>(columns.keySet()));
        for (int i = 0; i < table.getIdColumns().size(); ++i) {
            columns.put(table.getIdColumns().get(i), idValues.get(i));
        }
        final Object[] queryParams = columns.values().toArray();
        jdbcOperations.update(updateQuery, queryParams);
        return postUpdate(entity);
    }

    protected Map<String, Object> preUpdate(final T entity, final Map<String, Object> columns) {
        return columns;
    }

    protected <S extends T> S create(final S entity) {
        // LOGGER.info("Performing save : " + ServiceHelper.toFormattedJson(entity));
        final Map<String, Object> columns = preCreate(columns(entity), entity);
        if (entity.getId() == null) {
            return createWithAutoGeneratedKey(entity, columns);
        }
        return createWithManuallyAssignedKey(entity, columns);
    }

    private <S extends T> S createWithManuallyAssignedKey(final S entity, final Map<String, Object> columns) {
        final String createQuery = sqlGenerator.create(table, columns);
        final Object[] queryParams = columns.values().toArray();
        jdbcOperations.update(createQuery, queryParams);
        return postCreate(entity, entity.getId()); //passing ID column instead of null to avoid null pointer exception.
    }

    private <S extends T> S createWithAutoGeneratedKey(final S entity, final Map<String, Object> columns) {
        removeIdColumns(columns);
        final String createQuery = sqlGenerator.create(table, columns);
        final Object[] queryParams = columns.values().toArray();
        final GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update(con -> {
            final String idColumnName = table.getIdColumns().get(0);
            final PreparedStatement ps = con.prepareStatement(createQuery, new String[]{idColumnName});
            for (int i = 0; i < queryParams.length; ++i) {
                ps.setObject(i + 1, queryParams[i]);
            }
            return ps;
        }, key);
        return postCreate(entity, key.getKey());
    }

    private List<Object> removeIdColumns(final Map<String, Object> columns) {
        final List<Object> idColumnsValues = new ArrayList<Object>(columns.size());
        for (final String idColumn : table.getIdColumns()) {
            idColumnsValues.add(columns.remove(idColumn));
        }
        return idColumnsValues;
    }

    protected Map<String, Object> preCreate(final Map<String, Object> columns, final T entity) {
        return columns;
    }

    private Map<String, Object> columns(final T entity) {
        return new LinkedHashMap<String, Object>(rowUnmapper.mapColumns(entity));
    }

    protected <S extends T> S postUpdate(final S entity) {
        return entity;
    }

    /**
     * General purpose hook method that is called every time {@link #create} is called with a new entity.
     * <p/>
     * OVerride this method e.g. if you want to fetch auto-generated key from database
     *
     *
     * @param entity
     *            Entity that was passed to {@link #create}
     * @param generatedId
     *            ID generated during INSERT or NULL if not available/not generated. todo: Type should be ID, not Number
     * @return Either the same object as an argument or completely different one
     */
    @SuppressWarnings("unchecked")
    protected <S extends T> S postCreate(final S entity, final Number generatedId) {
        entity.setId(generatedId.longValue());
        return (S) entity.withPersisted(true);
    }

    @Override
    public <S extends T> Iterable<S> save(final Iterable<S> entities) {
        // TODO (optimization): Should make this batch
        final List<S> ret = new ArrayList<S>();
        for (final S s : entities) {
            ret.add(save(s));
        }
        return ret;
    }

    @Override
    public List<T> findAll(final Iterable<ID> ids) {
        final List<ID> idsList = toList(ids);
        if (idsList.isEmpty()) {
            return Collections.emptyList();
        }
        final Object[] idColumnValues = flatten(idsList);
        return jdbcOperations.query(sqlGenerator.selectByIds(table, idsList.size()), rowMapper, idColumnValues);
    }

    private static <T> List<T> toList(final Iterable<T> iterable) {
        final List<T> result = new ArrayList<T>();
        for (final T item : iterable) {
            result.add(item);
        }
        return result;
    }

    public List<T> findByCustomColumnId(final Iterable<ID> ids, final String columnName) {
        final List<ID> idsList = toList(ids);
        if (idsList.isEmpty()) {
            return Collections.emptyList();
        }
        final Object[] idColumnValues = flatten(idsList);
        return jdbcOperations.query(sqlGenerator.selectByCustomColumnIds(table, idsList.size(), columnName), rowMapper,
                idColumnValues);
    }

    public List<T> findAllByCustomQuery(final String query) {
        return jdbcOperations.query(sqlGenerator.selectByCustomQuery(table, query), rowMapper);
    }

    public List<T> findAllByCustomQuery(final List<String> columns, final Object... values) {
        validate(columns, values);
        return jdbcOperations.query(sqlGenerator.selectByCustomQuery(table, columns), rowMapper, values);
    }

    private void validate(final List<String> columns, final Object... values) {
        if (columns.size() != values.length) {
            throw new IllegalArgumentException("Columns and values should be of same count");
        }
    }

    public List<T> findAllByCustomQuery(final String column, final Object value) {
        return findAllByCustomQuery(asList(column), value);
    }

    public Page<T> findAllByCustomQuery(final String queries, final Pageable page) {
        final String query = sqlGenerator.selectByCustomQuery(table, queries, page);
        return new PageImpl<T>(jdbcOperations.query(query, rowMapper), page, count());
    }

    public Page<T> findAllByCustomQuery(final Pageable page, final List<String> columns, final Object... values) {
        final String query = sqlGenerator.selectByCustomQuery(table, columns, page);
        return new PageImpl<T>(jdbcOperations.query(query, rowMapper, values), page, count());
    }

    public T findObjByCustomQuery(final String query) {
        final List<T> entityOrEmpty = jdbcOperations.query(sqlGenerator.selectByCustomQuery(table, query), rowMapper);
        return entityOrEmpty.isEmpty() ? null : entityOrEmpty.get(0);
    }

    public T findObjByCustomQuery(final String column, final Object value) {
        return findObjByCustomQuery(asList(column), value);
    }

    public T findObjByCustomQuery(final List<String> columns, final Object... values) {
        validate(columns, values);
        final List<T> entityOrEmpty = jdbcOperations.query(sqlGenerator.selectByCustomQuery(table, columns), rowMapper,
                values);
        return entityOrEmpty.isEmpty() ? null : entityOrEmpty.get(0);
    }

    public int countByCustomQuery(final String query) {
        return jdbcOperations.queryForObject(sqlGenerator.countByCustomQuery(table, query), Integer.class);
    }

    public int countByCustomQuery(final List<String> columns, final Object... values) {
        validate(columns, values);
        return jdbcOperations.queryForObject(sqlGenerator.countByCustomQuery(table, columns), Integer.class, values);
    }

    public int sumColumnByCustomQuery(final String column, final List<String> whereColumns, final Object... values) {
        validate(whereColumns, values);
        return jdbcOperations.queryForObject(sqlGenerator.sumByCustomQuery(table, column, whereColumns), Integer.class, values);
    }

    private static <ID> Object[] flatten(final List<ID> ids) {
        final List<Object> result = new ArrayList<Object>();
        for (final ID id : ids) {
            result.addAll(idToObjectList(id));
        }
        return result.toArray();
    }

    @Override
    public List<T> findAll(final Sort sort) {
        return jdbcOperations.query(sqlGenerator.selectAll(table, sort), rowMapper);
    }

    @Override
    public Page<T> findAll(final Pageable page) {
        final String query = sqlGenerator.selectAll(table, page);
        return new PageImpl<T>(jdbcOperations.query(query, rowMapper), page, count());
    }

    protected void runCustomUpdateQuery(final String query, final Object... params) {
        jdbcOperations.update(query, params);
    }

    protected void setIfPresent(final Map<String, Object> mapping, final Boolean value, final String key) {
        if (value != null) {
            mapping.put(key, value.booleanValue() ? 1 : 0);
        }
    }

    protected void setIfPresent(final Map<String, Object> mapping, final String value, final String key) {
        if (value != null) {
            mapping.put(key, value);
        }
    }

    protected void setIfPresent(final Map<String, Object> mapping, final Long value, final String key) {
        if (value != null) {
            mapping.put(key, value);
        }
    }

    protected void setIfPresent(final Map<String, Object> mapping, final Double value, final String key) {
        if (value != null) {
            mapping.put(key, value);
        }
    }

    protected void setIfPresent(final Map<String, Object> mapping, final Integer value, final String key) {
        if (value != null) {
            mapping.put(key, value);
        }
    }

    protected Long getLong(final String value) {
        return value == null ? 0 : Long.valueOf(value);
    }

    protected Double getDouble(final String value) {
        return value == null ? 0.0 : Double.valueOf(value);
    }

    protected Integer getInteger(final String value) {
        return value == null ? 0 : Integer.valueOf(value);
    }

    public abstract T rowMapper(final ResultSet rs) throws SQLException;

    public abstract Map<String, Object> rowUnmapper(final T entity);

}
