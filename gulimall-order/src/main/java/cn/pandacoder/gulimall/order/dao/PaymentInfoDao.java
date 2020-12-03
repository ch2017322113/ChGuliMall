package cn.pandacoder.gulimall.order.dao;

import cn.pandacoder.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:41:42
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
