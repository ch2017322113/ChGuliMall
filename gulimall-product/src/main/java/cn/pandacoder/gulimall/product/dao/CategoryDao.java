package cn.pandacoder.gulimall.product.dao;

import cn.pandacoder.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 22:37:51
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
