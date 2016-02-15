package com.rest.kouponify.resource;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.rest.kouponify.broker.ShopifyBrokerImpl;
import com.rest.kouponify.entity.Order;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;

import com.rest.kouponify.entity.Coupon;
import com.rest.kouponify.service.AbstractService;
import com.rest.kouponify.service.CouponService;
import com.wordnik.swagger.annotations.Api;

import java.util.List;

/**
 * Created by vijay on 2/15/16.
 */

@Path("coupons")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
@Resource
@Api(value = "/coupons", description = "Operations for coupons.")
public class CouponResource extends BaseResource<Coupon> {

    @Autowired
    @Getter
    @Setter
    private CouponService service;

    @Override
    protected AbstractService<Coupon> getService() {
        return service;
    }

    @Path("/orders")
    @GET
    @RolesAllowed("admin")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public List<Order> getAllOrders() throws Exception {
        return new ShopifyBrokerImpl().getOrders();
    }

}
