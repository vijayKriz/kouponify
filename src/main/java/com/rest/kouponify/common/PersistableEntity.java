package com.rest.kouponify.common;

import javax.xml.bind.annotation.XmlTransient;

import org.springframework.data.domain.Persistable;

/**
 * Created by vijay on 2/15/16.
 */
public abstract class PersistableEntity implements Persistable<Long> {

    private static final long serialVersionUID = -1099053605082870566L;

    private Long id;
    private Long createdAt;
    private Long createdBy;
    private Long updatedBy;
    private Long updatedAt = System.currentTimeMillis();

    @XmlTransient
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final Long createdBy) {
        this.createdBy = createdBy;
    }

    @XmlTransient
    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @XmlTransient
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Long createdAt) {
        this.createdAt = createdAt;
    }

    @XmlTransient
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    private transient boolean persisted;

    @Override
    public Long getId() {
        return id;
    }

    @XmlTransient
    @Override
    public boolean isNew() {
        return persisted;
    }

    public PersistableEntity withPersisted(final boolean persisted) {
        this.persisted = persisted;
        return this;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
