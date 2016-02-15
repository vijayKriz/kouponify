package com.rest.kouponify.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rest.kouponify.common.GenericDaoImpl;
import com.rest.kouponify.entity.Coupon;

@Component("couponDao")
public class CouponDao extends GenericDaoImpl<Coupon, Long> {

    public CouponDao() {
        super("kouponify.coupon", "coupon_id");
    }

    @Override
    public Coupon rowMapper(final ResultSet rs) throws SQLException {
        final Coupon coupon = new Coupon();
        coupon.setId(rs.getLong("coupon_id"));
        coupon.setCode(rs.getString("code"));
        coupon.setValue(rs.getDouble("value"));
        coupon.setEndAt(rs.getLong("starts_at") > 0 ?new Date(rs.getLong("ends_at")):null);
        coupon.setStartAt(rs.getLong("starts_at") > 0 ?new Date(rs.getLong("starts_at")):null);
        coupon.setDiscountType(rs.getString("discount_type"));
        coupon.setCreatedBy(rs.getLong("created_by"));
        coupon.setUpdatedBy(rs.getLong("updated_by"));
        coupon.setCreatedAt(rs.getLong("created_at"));
        coupon.setUpdatedAt(rs.getLong("updated_at"));
        coupon.setActive(rs.getBoolean("active"));
        coupon.setMinimumOrderAmount(rs.getDouble("minimum_order_amount"));
        coupon.setAppliesToResource(rs.getString("applies_to_resource"));
        return coupon;
    }

    @Override
    public Map<String, Object> rowUnmapper(final Coupon coupon) {
        final Map<String, Object> mapping = new LinkedHashMap<String, Object>();
        mapping.put("coupon_id", coupon.getId());
        mapping.put("code", coupon.getCode());
        mapping.put("value", coupon.getValue());
        mapping.put("ends_at", coupon.getEndAt().getTime());
        mapping.put("starts_at", coupon.getStartAt().getTime());
        mapping.put("discount_type", coupon.getDiscountType());
        mapping.put("applies_to_resource", coupon.getAppliesToResource());
        mapping.put("minimum_order_amount", coupon.getMinimumOrderAmount());
        mapping.put("created_by", coupon.getCreatedBy());
        mapping.put("updated_by", coupon.getUpdatedBy());
        mapping.put("created_at", coupon.getCreatedAt());
        mapping.put("updated_at", coupon.getUpdatedAt());
        setIfPresent(mapping, coupon.getActive(), "active");
        return mapping;
    }

}
