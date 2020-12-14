package cn.pandacoder.gulimall.coupon;

import cn.pandacoder.gulimall.coupon.entity.CouponEntity;
import cn.pandacoder.gulimall.coupon.service.CouponService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallCouponApplicationTests {

    @Autowired
    CouponService couponService;

    @Test
    void contextLoads() {

        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("优惠券");
        couponService.save(couponEntity);
    }

}
