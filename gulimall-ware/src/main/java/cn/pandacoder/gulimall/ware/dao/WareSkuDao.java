package cn.pandacoder.gulimall.ware.dao;

import cn.pandacoder.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:38:01
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    long getSkuStock(Long skuId);

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
