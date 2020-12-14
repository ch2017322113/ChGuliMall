package cn.pandacoder.gulimall.product;

import cn.pandacoder.gulimall.product.entity.BrandEntity;
import cn.pandacoder.gulimall.product.service.BrandService;

import cn.pandacoder.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    void testGetParentPath(){
        Long[] categoryPath = categoryService.findCategoryPath(225L);
        log.info("完整路径：{}", Arrays.toString(categoryPath));
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功...");
    }

    @Test
    void update() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("华为手机");
        brandService.updateById(brandEntity);
        System.out.println("更新成功...");
    }

    @Test
    void getList() {
        List<BrandEntity> brand_id = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        brand_id.forEach(System.out::println);
    }


}
