package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 批次
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_batch")
public class Batch extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 规格ID
     */
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
     * 状态(0正常 1已过期 2已用完)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
