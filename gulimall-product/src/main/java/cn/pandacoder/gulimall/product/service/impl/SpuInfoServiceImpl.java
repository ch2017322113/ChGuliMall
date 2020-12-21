package cn.pandacoder.gulimall.product.service.impl;

import cn.pandacoder.common.to.SkuReductionTo;
import cn.pandacoder.common.to.SpuBoundTo;
import cn.pandacoder.common.to.es.SkuEsModel;
import cn.pandacoder.common.utils.R;
import cn.pandacoder.gulimall.product.entity.*;
import cn.pandacoder.gulimall.product.feign.CouponFeignService;
import cn.pandacoder.gulimall.product.service.*;
import cn.pandacoder.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.Query;

import cn.pandacoder.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {

        //保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //保存spu图片描述 pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);


        //保存spu图片集 pms_spu_images
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            attrValueEntity.setAttrName(attrEntity.getAttrName());
            attrValueEntity.setAttrValue(attr.getAttrValues());
            attrValueEntity.setQuickShow(attr.getShowDesc());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            return attrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(productAttrValueEntities);

        //保存积分信息 sms_spu_bounds
        Bounds bounds = spuInfo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r1.getCode() != 0) {
            log.error("远程保存spu优惠信息失败");
        }


        //保存当前spu的所有sku信息
        List<Skus> skus = spuInfo.getSkus();
        if (skus != null || skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images img :
                        item.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }

                //sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();
                //sku图片信息 pms_sku_images
                List<Images> images1 = item.getImages();
                List<SkuImagesEntity> skuImagesEntityList = images1.stream().map((img) -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setImgUrl(img.getImgUrl());
                    imagesEntity.setDefaultImg(img.getDefaultImg());
                    imagesEntity.setSkuId(skuId);
                    return imagesEntity;
                }).filter((entity) -> {
                    //返回true被收集
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);
                //TODO 没有图片无需保存

                //sku基本销售属性 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);


                //sku优惠信息 sms_sku_ladder / sms_sku_full_reduction /sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
                    R r2 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (!r2.getCode().equals(0)) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(params.get("key"))) {
            String key = (String) params.get("key");
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);

            });
        }
        if (!StringUtils.isEmpty(params.get("status"))) {
            String status = (String) params.get("status");
            wrapper.eq("publish_status", status);

        }
        if (!StringUtils.isEmpty(params.get("brandId"))) {
            String brandId = (String) params.get("brandId");
            wrapper.eq("brand_id", brandId);
        }
        if (!StringUtils.isEmpty(params.get("catelogId"))) {
            String catalogId = (String) params.get("catelogId");
            wrapper.eq("catalog_id", catalogId);
        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //根据spuId获取所有要上架的商品,收集到upProduct中
        List<SkuEsModel> upProduct = new ArrayList<>();


        //根据SpuId查出所有Spu规格属性的attrId
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        //得到attrId中可检索的Id
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);


        List<SkuEsModel.Attrs> attrsList = productAttrValueEntities.stream().filter((item) -> {
            return idSet.contains(item.getAttrId());
        }).map(attrItem -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attrItem, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        //获取SpuId下的所有Sku信息
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        skuInfoEntities.stream().map((item) -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(item, esModel);
            //skuPrice,skuImg,hasStock,hotScore
            esModel.setSkuPrice(item.getPrice());
            esModel.setSkuImg(item.getSkuDefaultImg());
            //TODO 查询ware是否有库存
//            esModel.setHasStock(false);

            //TODO 热度评分默认为0
            esModel.setHotScore(0L);

            //TODO 查出品牌和分类名
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            esModel.setBrandId(brandEntity.getBrandId());
            esModel.setBrandImg(brandEntity.getLogo());
            esModel.setBrandName(brandEntity.getName());

            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());
            /**
             *     private String brandName;
             *     private String brandImg;
             *     private String catalogName;
             *
             *     private List<Attrs> attrs;
             *
             *     @Data
             *     public static class Attrs{
             *         private Long attrId;
             *         private String attrName;
             *         private String attrValue;
             *     }
             */
            esModel.setAttrs(attrsList);

            return esModel;
        }).collect(Collectors.toList());

        //TODO 发送给es保存数据
    }


}