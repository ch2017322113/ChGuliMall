package cn.pandacoder.gulimall.product.service.impl;

import cn.pandacoder.gulimall.product.entity.CategoryBrandRelationEntity;
import cn.pandacoder.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.UpdateChainWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.Query;

import cn.pandacoder.gulimall.product.dao.CategoryDao;
import cn.pandacoder.gulimall.product.entity.CategoryEntity;
import cn.pandacoder.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        List<CategoryEntity> collect = categoryEntities.stream().filter(
                categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildrens(menu, categoryEntities));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());


        return collect;
    }

    @Override
    public void removeByMenuIds(List<Long> asList) {
        //TODO 检查当前的菜单是否被别的地方所引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    public void updateDetails(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())){
            this.categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

        }

    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        this.categoryBrandRelationService.updateCascadeCategory(category.getCatId(),category.getName());
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity rootEntity, List<CategoryEntity> allEntities) {
        List<CategoryEntity> collect = allEntities.stream()
                .filter((categoryEntity -> categoryEntity.getParentCid().equals(rootEntity.getCatId())))
                .map((theCategoryEntity) -> {
                    theCategoryEntity.setChildren(getChildrens(theCategoryEntity, allEntities));
                    return theCategoryEntity;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }
}