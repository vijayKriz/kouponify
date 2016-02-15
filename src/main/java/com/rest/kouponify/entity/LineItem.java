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
@JsonRootName("lineItem")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {
    private Integer fulfillable_quantity;

    private String fulfillment_service;

    private String fulfillment_status;

    private Integer grams;

    private Long id;

    private Double price;

    private Long product_id;

    private Integer quantity;

    private Boolean requires_shipping;

    private String sku;

    private String title;

    private Long variant_id;

    private String variant_title;

    private String vendor;

    private String name;

    private Boolean gift_card;

    private Boolean taxable;

    private String[] tax_lines;

    private Double total_discount;


}
