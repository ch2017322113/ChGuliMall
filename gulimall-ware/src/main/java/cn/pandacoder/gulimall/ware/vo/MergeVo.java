package cn.pandacoder.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    /**
     * {purchaseId: 1, items: [1, 2]}
     * items: [1, 2]
     */
    private Long purchaseId;
    private List<Long> items;
}
