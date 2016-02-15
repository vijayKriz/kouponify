package com.rest.kouponify.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rest.kouponify.common.GenericDaoImpl;
import com.rest.kouponify.common.PersistableEntity;
import com.rest.kouponify.entity.User;
/**
 *
 * @author vijay
 *
 * @param <T>| Abstract service to perform basic CRUD operations
 */
@Service
public abstract class AbstractService<T extends PersistableEntity> {

    abstract protected GenericDaoImpl<T, Long> getDao();

    /**
     * @param entity
     * @return entity
     */
    public T create(final T entity) {
        final User user = getRequestingUser();
        entity.setCreatedBy(user.getId());
        entity.setUpdatedBy(user.getId());
        entity.setCreatedAt(System.currentTimeMillis());
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.withPersisted(true);
        return getDao().save(entity);
    }

    /**
     *
     * @param entity
     */
    public void update(final T entity) {
        final User user = getRequestingUser();
        entity.setUpdatedBy(user.getId());
        entity.setUpdatedAt(System.currentTimeMillis());
        getDao().save(entity);
    }

    /**
     *
     * @param id
     */
    public void delete(final Long id) {
        getDao().delete(id);
    }

    /**
     *
     * @param id
     * @return entity
     */
    public T find(final Long id) {
        return getDao().findOne(id);
    }

    /**
     *
     * @return entities
     */
    public List<T> findall() {
        final User user = getRequestingUser();
        return getDao().findAllByCustomQuery("created_by=" + user.getId() + " or " + " updated_by =" + user.getId());
    }

    /**
     *
     * @return requestUserContext
     */
    protected User getRequestingUser() {
        return SecurityContextHolder.getContext() == null ? null : (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
