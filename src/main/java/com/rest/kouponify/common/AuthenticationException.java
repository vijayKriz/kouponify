package com.rest.kouponify.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *Created by vijay on 2/15/16.
 */
public class AuthenticationException extends WebApplicationException {

    private static final long serialVersionUID = -2435557348972452246L;

    public AuthenticationException(final Object payload, final MediaType mediaType) {
        super(Response.status(Status.UNAUTHORIZED).entity(payload).type(mediaType.toString()).build());
    }
}