package com.rest.kouponify;

import org.glassfish.jersey.server.ResourceConfig;

import com.rest.kouponify.common.AuthenticationFilter;
import com.rest.kouponify.filter.CORSResponseFilter;
import com.rest.kouponify.filter.LoggingResponseFilter;

/**
 * Created by vijay on 2/15/16.
 * Registers the components to be used by the JAX-RS application
 */
public class RestApplication extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public RestApplication() {
        packages("com.rest.kouponify");
        register(LoggingResponseFilter.class);
        register(CORSResponseFilter.class);
        register(AuthenticationFilter.class);
    }
}
