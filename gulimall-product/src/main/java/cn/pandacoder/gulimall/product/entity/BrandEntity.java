package cn.pandacoder.gulimall.product.entity;

import cn.pandacoder.common.valid.AddGroup;
import cn.pandacoder.common.valid.ListValue;
import cn.pandacoder.common.valid.UpdateGroup;
import cn.pandacoder.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-02 22:37:51
 */

@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "修改必须指定品牌", groups = {UpdateGroup.class})
    @Null(message = "新增不可指定id", groups = {AddGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */

    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 品牌logo地址
     */
    @NotBlank(message = "logo地址不能为空", groups = {AddGroup.class})
    @URL(message = "logo地址必须为合法的URL", groups = {AddGroup.class, UpdateGroup.class})
    private String logo;

    /**
     * 介绍
     */
    private String descript;

    /**
     * 显示状态[0-不显示；1-显示]
     */
//    @NotNull(message = "显示状态不能为空", groups = {AddGroup.class})
    @ListValue(vals = {1, 0}, groups = {AddGroup.class,UpdateStatusGroup.class})
    private Integer showStatus;

    /**
     * 检索首字母
     */
    @NotBlank(message = "检索首字母不能为空", groups = {AddGroup.class})
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是a-z或A-Z", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;

    /**
     * 排序
     */
    @NotNull(message = "排序数不能为空", groups = {AddGroup.class})
    @Min(value = 0, message = "排序数必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;

}
