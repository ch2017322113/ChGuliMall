package cn.pandacoder.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 12:38:01
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

