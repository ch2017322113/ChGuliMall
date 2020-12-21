package cn.pandacoder.gulimall.ware.service;

import cn.pandacoder.gulimall.ware.vo.SkuHasStockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:38:01
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);
}

