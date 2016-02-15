package com.rest.kouponify.resource;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.Setter;

import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.kouponify.common.AuthenticationException;
import com.rest.kouponify.entity.Token;
import com.rest.kouponify.entity.User;
import com.rest.kouponify.service.AbstractService;
import com.rest.kouponify.service.UserService;
import com.wordnik.swagger.annotations.Api;

/**
 * Created by vijay on 2/15/16.
 */

@Path("users")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
@Resource
@Api(value = "/users", description = "Operations for Users.")
public class UserResource extends BaseResource<User> {

    @Autowired
    @Getter
    @Setter
    private UserService service;

    @Override
    protected AbstractService<User> getService() {
        return service;
    }

    /**
     *
     * Authenticates a user and creates an authentication token.
     * @param user
     * @return
     */
    @POST
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Token authenticate(final User user) {
        final User userDB = service.getUserByName(user.getUsername());
        if (userDB == null || !userDB.getPassword().equals(user.getPassword())) {
            throw new AuthenticationException("Access Denied", MediaType.APPLICATION_JSON_TYPE);
        }
        final Token token = new Token(Base64.encodeAsString(user.getUsername() + ":" + user.getPassword()));
        return token;
    }

}
