package com.rest.kouponify.broker;

import com.rest.kouponify.entity.Order;

import java.io.IOException;
import java.util.List;

/**
 * Created by vijay on 2/15/16.
 */
public interface ShopifyBroker {

    public List<Order> getOrders() throws IOException;

    public Order findOrder(final Long id) throws IOException;

}
