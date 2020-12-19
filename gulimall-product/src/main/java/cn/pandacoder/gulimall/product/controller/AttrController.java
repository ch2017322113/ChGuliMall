package cn.pandacoder.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.pandacoder.gulimall.product.entity.ProductAttrValueEntity;
import cn.pandacoder.gulimall.product.service.ProductAttrValueService;
import cn.pandacoder.gulimall.product.vo.AttrRespVo;
import cn.pandacoder.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.pandacoder.gulimall.product.entity.AttrEntity;
import cn.pandacoder.gulimall.product.service.AttrService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.R;


/**
 * 商品属性
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 23:25:43
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * @param spuId
     * @return
     */
    //product/attr/base/listforspu/{spuId}
    @GetMapping("/base/listforspu/{spuId}")
    public R listForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.getSpuInfo(spuId);
        return R.ok().put("data", list);
    }

    ///product/attr/update/{spuId}
    @PostMapping("/update/{spuId}")
    public R updateSpu(@PathVariable("spuId") Long spuId,
                       @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
//        attrService.updateById(attr);
        attrService.updateByAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    @RequestMapping("/{attrType}/list/{catId}")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable("catId") Long catId,
                      @PathVariable("attrType") String type) {


        PageUtils page = attrService.queryBaseQuery(params, catId, type);

        return R.ok().put("page", page);
    }

}
