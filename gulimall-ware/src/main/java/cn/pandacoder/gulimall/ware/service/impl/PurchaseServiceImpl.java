package cn.pandacoder.gulimall.ware.service.impl;

import cn.pandacoder.common.constant.WareConstant;
import cn.pandacoder.gulimall.ware.entity.PurchaseDetailEntity;
import cn.pandacoder.gulimall.ware.service.PurchaseDetailService;
import cn.pandacoder.gulimall.ware.service.WareSkuService;
import cn.pandacoder.gulimall.ware.vo.MergeVo;
import cn.pandacoder.gulimall.ware.vo.PurchaseDoneVo;
import cn.pandacoder.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.common.utils.Query;

import cn.pandacoder.gulimall.ware.dao.PurchaseDao;
import cn.pandacoder.gulimall.ware.entity.PurchaseEntity;
import cn.pandacoder.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());

            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();

        }

        //获得所有采购需求的id
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        PurchaseEntity purchaseEntity = this.getById(finalPurchaseId);

        //如果当前采购单的状态为指派或新建状态才能合并
        if (purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
                || purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())) {
            //获取所有采购需求项
            List<PurchaseDetailEntity> purchaseDetailEntities1 = (List<PurchaseDetailEntity>) purchaseDetailService.listByIds(items);

            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailEntities1.stream().filter(purchaseDetail -> {
                if (purchaseDetail.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode())) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());

            List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
                //更新采购需求的里面的状态信息
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            if (collect != null && collect.size() > 0) {
                //批量更新采购单
                purchaseDetailService.updateBatchById(collect);
                //更新采购单的时间状态
                PurchaseEntity thePurchaseEntity = new PurchaseEntity();
                thePurchaseEntity.setId(finalPurchaseId);
                thePurchaseEntity.setUpdateTime(new Date());
                this.updateById(thePurchaseEntity);
            }

        }

    }

    @Transactional
    @Override
    public void received(List<Long> purchaseDetailIds) {
        //1、确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> purchaseEntities = purchaseDetailIds.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            if (item.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) ||
                    item.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //2、改变采购单的状态
        this.updateBatchById(purchaseEntities);

        //3、改变采购需求的状态，遍历采购单-item
        purchaseEntities.forEach(item -> {
            //根据采购单获取所有采购需求
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(item.getId());

            if (purchaseDetailEntities != null && purchaseDetailEntities.size() > 0) {
                //遍历更新采购需求-theItem
                List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(theItem -> {
                    //更新采购需求状态为正在购买
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(theItem.getId());
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());

                purchaseDetailService.updateBatchById(collect);
            }

        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {

        Long id = purchaseDoneVo.getId();

        //记录是否所有采购需求都成功
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        //收集所有采购需求状态，并批量更新
        ArrayList<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            Integer status = item.getStatus();
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (status.equals(WareConstant.PurchaseDetailStatusEnum.ERROR.getCode())) {
                flag = false;
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ERROR.getCode());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                //入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                //给哪个sku增库存，仓库id，增几个
                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            purchaseDetailEntities.add(purchaseDetailEntity);
        }
        //批量更新
        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        //更新采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISHED.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        //
    }

}