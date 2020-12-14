package cn.pandacoder.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 22:37:51
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeByMenuIds(List<Long> asList);

    /**
     * 找到
     * @param catelogId
     * @return
     */
    Long[] findCategoryPath(Long catelogId);

    void updateDetails(CategoryEntity category);

    void updateCascade(CategoryEntity category);
}

