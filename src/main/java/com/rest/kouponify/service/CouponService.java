package com.rest.kouponify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rest.kouponify.common.GenericDaoImpl;
import com.rest.kouponify.dao.CouponDao;
import com.rest.kouponify.entity.Coupon;
import com.rest.kouponify.entity.User;
/**
 *
 * @author vijay | Coupon service to generate coupon
 */
@Service
public class CouponService extends AbstractService<Coupon> {

    private final String DEFAULT_SHOP = "KOUPON";

    @Override
    protected GenericDaoImpl<Coupon, Long> getDao() {
        return dao;
    }

    @Autowired
    @Qualifier("couponDao")
    CouponDao dao;

    @Override
    public Coupon create(final Coupon coupon) {
        final User user = SecurityContextHolder.getContext() == null ? null : (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        coupon.setCode(generateCoupon(user.getShop() == null ? DEFAULT_SHOP : user.getShop().toUpperCase(),
                coupon.getDiscountType(),
                coupon.getValue(), coupon.getMinimumOrderAmount(), coupon.getAppliesToResource()));
        return super.create(coupon);
    }

    /**
     *
     * @param shop
     * @param discountType
     * @param discountValue
     * @param minimumOrderAmount
     * @param appliedResource
     * @return
     */
    private String generateCoupon(final String shop, final String discountType, final Double value,
            final Double minimumOrderAmount, final String resource) {
        final String prefix = shop.length() <= 4 ? shop : shop.substring(0, 3);
        final StringBuffer suffix = new StringBuffer();
        suffix.append(value.intValue());
        if (minimumOrderAmount != null && minimumOrderAmount > 0) {
            suffix.append("OFF" + minimumOrderAmount.intValue());
        }
        if (resource != null) {
            suffix.append("ON" + resource.toUpperCase());
        }
        return prefix + suffix.toString();
    }

}
