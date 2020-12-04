package cn.pandacoder.gulimall.member.feign;

import cn.pandacoder.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CoupenFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupon();

}
