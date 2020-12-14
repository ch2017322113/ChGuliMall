package cn.pandacoder.gulimall.product.dao;

import cn.pandacoder.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 22:37:51
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCascadeCategory(@Param("catId") Long catId, @Param("name") String name);
}
