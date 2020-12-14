package cn.pandacoder.gulimall.product.service.impl;

import cn.pandacoder.common.constant.ProductConstant;
import cn.pandacoder.gulimall.product.dao.AttrAttrgroupRelationDao;
import cn.pandacoder.gulimall.product.dao.AttrGroupDao;
import cn.pandacoder.gulimall.product.dao.CategoryDao;
import cn.pandacoder.gulimall.product.entity.AttrAttrgroupRelationEntity;
import cn.pandacoder.gulimall.product.entity.AttrGroupEntity;
import cn.pandacoder.gulimall.product.entity.CategoryEntity;
import cn.pandacoder.gulimall.product.service.CategoryService;
import cn.pandacoder.gulimall.product.vo.AttrRespVo;
import cn.pandacoder.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
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

import cn.pandacoder.gulimall.product.dao.AttrDao;
import cn.pandacoder.gulimall.product.entity.AttrEntity;
import cn.pandacoder.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //保存基本数据
        this.save(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_Type_BASE.getCode()) {
            //保存级联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }

    }

    @Override
    public PageUtils queryBaseQuery(Map<String, Object> params, Long catId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_Type_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catId != 0) {
            wrapper.eq("catelog_id", catId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((item) -> {
                item.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVoList = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //设置分类分组
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            return attrRespVo;
        })).collect(Collectors.toList());
        pageUtils.setList(respVoList);
        return pageUtils;
    }

    @Override
    @Transactional
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity byId = this.getById(attrId);
        BeanUtils.copyProperties(byId, attrRespVo);


        if (byId.getAttrType().equals(ProductConstant.AttrEnum.ATTR_Type_BASE.getCode())) {
            //分组信息
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity.getAttrGroupId() != null) {
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


//        attrRespVo.setCatelogPath();
        Long catelogId = byId.getCatelogId();
        Long[] categoryPath = categoryService.findCategoryPath(catelogId);
        attrRespVo.setCatelogPath(categoryPath);


        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }

        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateByAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_Type_BASE.getCode())) {
            Integer attr_id = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            //更新级联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));


            if (attr_id > 0) {
                //更新级联关系
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }


    }


}