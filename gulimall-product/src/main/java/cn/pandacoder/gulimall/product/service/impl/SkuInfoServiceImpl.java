package cn.pandacoder.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.Query;

import cn.pandacoder.gulimall.product.dao.SkuInfoDao;
import cn.pandacoder.gulimall.product.entity.SkuInfoEntity;
import cn.pandacoder.gulimall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        /**
         *     key: '华为',//检索关键字
         *     catelogId: 0,
         *     brandId: 0,
         *     min: 0,
         *     max: 0
         */
        if (!StringUtils.isEmpty(params.get("key"))) {
            String key = (String) params.get("key");
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        if (!StringUtils.isEmpty(params.get("catelogId")) && !"0".equalsIgnoreCase((String) params.get("catelogId"))) {
            String catalogId = (String) params.get("catelogId");
            wrapper.eq("catalog_id", catalogId);
        }
        if (!StringUtils.isEmpty(params.get("brandId")) && !"0".equalsIgnoreCase((String) params.get("brandId"))) {
            String brandId = (String) params.get("brandId");
            wrapper.eq("brand_id", brandId);
        }
        if (!StringUtils.isEmpty(params.get("min"))) {
            String min = (String) params.get("min");
            try {
                BigDecimal minBig = new BigDecimal(min);
                wrapper.ge("price", min);
            } catch (Exception e) {

            }
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal maxBig = new BigDecimal(max);
                if (maxBig.compareTo(BigDecimal.ZERO) > 0) {
                    wrapper.le("price", max);
                }
            } catch (Exception e) {

            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        //根据SpuId 查出所有sku信息

        List<SkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntities;
    }

}