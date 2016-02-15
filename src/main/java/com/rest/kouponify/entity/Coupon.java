package com.rest.kouponify.entity;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.rest.kouponify.common.PersistableEntity;

import java.util.Date;

/**
 * Created by vijay on 2/15/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("coupon")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coupon extends PersistableEntity {

    private static final long serialVersionUID = -1284654508625552327L;

    private Long id;

    private String code;

    private Double value;

    private Date startAt;

    private Date endAt;

    private Double minimumOrderAmount;

    private Double usageLimit;

    private Integer appliesToId;

    private Boolean appliesOnce;

    private String discountType;

    private String appliesToResource;

    private Boolean active;

}
