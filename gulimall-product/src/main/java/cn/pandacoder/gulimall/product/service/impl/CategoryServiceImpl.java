package cn.pandacoder.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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
                    menu.setChildren(getChildrens(menu,categoryEntities));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());


        return collect;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity rootEntity, List<CategoryEntity> allEntities) {
        List<CategoryEntity> collect = allEntities.stream()
                .filter((categoryEntity -> categoryEntity.getParentCid() == rootEntity.getCatId()))
                .map((theCategoryEntity) -> {
                    theCategoryEntity.setChildren(getChildrens(theCategoryEntity, allEntities));
                    return theCategoryEntity;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }
}