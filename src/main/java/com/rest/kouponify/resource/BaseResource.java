package com.rest.kouponify.resource;

/**
 *
 * Created by vijay on 2/15/16.
 * Base Resource to perform CRUD
 */
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.rest.kouponify.common.PersistableEntity;
import com.rest.kouponify.service.AbstractService;

public abstract class BaseResource<T extends PersistableEntity> {

    abstract protected AbstractService<T> getService();

    @GET
    @Path("{id}")
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public T get(@PathParam("id") final Long id)
            throws Exception {
        return getService().find(id);
    }

    @POST
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public T create(final T tdto)
            throws Exception {
        return getService().create(tdto);
    }

    @PUT
    @Path("{id}")
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public void update(@PathParam("id") final Long id, final T tdto) throws Exception {
        tdto.setId(id);
        getService().update(tdto);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public void delete(@PathParam("id") final Long id)
            throws Exception {
        getService().delete(id);
    }

    @GET
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public List<T> getAll() throws Exception {
        return getService().findall();
    }
}
