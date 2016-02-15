package com.rest.kouponify.common;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.rest.kouponify.dao.UserDao;
import com.rest.kouponify.entity.User;

/**
 * Created by vijay on 2/15/16.
 * This filter verify the access permissions for a user based on username and passowrd
 */
@Provider
@Slf4j
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        final Method method = resourceInfo.getResourceMethod();
        log.debug(method + requestContext.getMethod());
        // Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class) && !requestContext.getMethod().equals("OPTIONS")) {
            // Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                throw new AuthenticationException("Access Denied", MediaType.APPLICATION_JSON_TYPE);
            }

            // Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            // Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

            // If no authorization information present; block access
            if (authorization == null || authorization.isEmpty()) {
                throw new AuthenticationException("Access Denied", MediaType.APPLICATION_JSON_TYPE);
            }

            // Get encoded username and password
            final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            // Decode username and password
            final String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

            // Split username and password tokens
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            if (tokenizer.countTokens() < 2) {
                throw new AuthenticationException("Access Denied", MediaType.APPLICATION_JSON_TYPE);
            }
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            if (method.isAnnotationPresent(RolesAllowed.class)) {
                final RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                final Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

                // verify user access
                if (!isUserAllowed(username, password, rolesSet)) {
                    throw new AuthenticationException("Access Denied", MediaType.APPLICATION_JSON_TYPE);
                }
            }
        }
    }

    private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {
        boolean isAllowed = false;
        final User user = userDao.loadUserByUsername(username);
        // For now not considering roles to keep it simple
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            // Verify user role
            if (rolesSet.contains("admin")) {
                isAllowed = true;
                log.debug(user.getShop());
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug(SecurityContextHolder.getContext().toString());
            }
        }
        return isAllowed;
    }
}
