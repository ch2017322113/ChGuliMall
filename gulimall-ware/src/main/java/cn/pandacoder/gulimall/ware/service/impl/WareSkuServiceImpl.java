package cn.pandacoder.gulimall.ware.service.impl;


import cn.pandacoder.gulimall.ware.vo.SkuHasStockVo;
import cn.pandacoder.common.utils.R;
import cn.pandacoder.gulimall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.Query;

import cn.pandacoder.gulimall.ware.dao.WareSkuDao;
import cn.pandacoder.gulimall.ware.entity.WareSkuEntity;
import cn.pandacoder.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         *    wareId: 123,//仓库id
         *    skuId: 123//商品id
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(wareId) && !"0".equalsIgnoreCase(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        if (!StringUtils.isEmpty(skuId) && !"0".equalsIgnoreCase(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            long count = baseMapper.getSkuStock(skuId);
            //查询当前sku总库存量
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return collect;
    }
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> sku_id = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if (sku_id.size() == 0 || sku_id == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询获取商品名字 无需回滚
            try {
                R info = productFeignService.info(skuId);

                if (info.getCode() == 0) {
                    Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                wareSkuEntity.setSkuName("to_be_query");
            }


            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

}