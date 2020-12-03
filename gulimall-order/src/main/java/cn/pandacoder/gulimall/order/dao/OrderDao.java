package cn.pandacoder.gulimall.order.dao;

import cn.pandacoder.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:41:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
