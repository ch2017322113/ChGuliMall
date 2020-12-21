package cn.pandacoder.gulimall.product.service;

import cn.pandacoder.gulimall.product.vo.AttrGroupRelationVo;
import cn.pandacoder.gulimall.product.vo.AttrRespVo;
import cn.pandacoder.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 22:37:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveAttr(AttrVo attr);


    PageUtils queryBaseQuery(Map<String, Object> params, Long catId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateByAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void delRelation(AttrGroupRelationVo[] relationVos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

