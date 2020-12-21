package cn.pandacoder.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.pandacoder.gulimall.ware.vo.MergeVo;
import cn.pandacoder.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.pandacoder.gulimall.ware.entity.PurchaseEntity;
import cn.pandacoder.gulimall.ware.service.PurchaseService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.R;


/**
 * 采购信息
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:38:01
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/done")
    public R finished(@RequestBody PurchaseDoneVo purchaseDoneVo) {
        purchaseService.done(purchaseDoneVo);

        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }

    ///ware/purchase/merge
    @RequestMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);

        return R.ok();
    }

    /**
     * 领取采购单
     * /ware/purchase/received
     */

    @PostMapping("/received")
    public R received(@RequestBody List<Long> purchaseDetailIds) {
        purchaseService.received(purchaseDetailIds);

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
