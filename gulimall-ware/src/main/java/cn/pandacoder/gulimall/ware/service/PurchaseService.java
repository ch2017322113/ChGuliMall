package cn.pandacoder.gulimall.ware.service;

import cn.pandacoder.gulimall.ware.vo.MergeVo;
import cn.pandacoder.gulimall.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:38:01
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> purchaseDetailIds);

    void done(PurchaseDoneVo purchaseDoneVo);
}

