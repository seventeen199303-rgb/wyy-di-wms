package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.Batch;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 批次业务对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Batch.class, reverseConvertGenerate = false)
public class BatchBo extends BaseEntity {

    /**
     * 批次ID
     */
    @NotNull(message = "批次ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 批次号
     */
    @NotBlank(message = "批次号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String batchNo;

    /**
     * 规格ID
     */
    @NotNull(message = "规格ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long skuId;

    /**
     * 批次数量
     */
    private BigDecimal quantity;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 有效期至
     */
    private LocalDate expiryDate;

    /**
     * 供应商/来源
     */
    private String supplier;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品名称(查询用)
     */
    private String itemName;

}
