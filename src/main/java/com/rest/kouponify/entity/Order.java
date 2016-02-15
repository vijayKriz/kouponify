package com.rest.kouponify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

/**
 * Created by vijay on 2/15/16.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("order")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String billing_address;
    private Long id;
    private String email;
    private String closed_at;
    private String created_at;
    private String updated_at;
    private Integer number;
    private String note;
    private String token;
    private String gateway;
    private Boolean test;
    private Double total_price;
    private Double subtotal_price;
    private Double total_weight;
    private Double total_tax;
    private String taxes_included;
    private String currency;
    private String financial_status;
    private String confirmed;
    private String total_discounts;
    private String total_line_items_price;
    private String cart_token;
    private String buyer_accepts_marketing;
    private String name;
    private String referring_site;
    private String landing_site;
    private String cancelled_at;
    private String cancel_reason;
    private Double total_price_usd;
    private String checkout_token;
    private String reference;
    private Long user_id;
    private Long location_id;
    private String source_identifier;
    private String source_url;
    private String processed_at;
    private String device_id;
    private String browser_ip;
    private String landing_site_ref;
    private Integer order_number;
    private Coupon[] discount_codes;
    private String[] payment_gateway_names;
    private String processing_method;
    private Long checkout_id;
    private String source_name;
    private String fulfillment_status;
    private String tags;
    private String contact_email;
    private LineItem[] line_items;
}
