package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 序列号
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_serial")
public class SerialNumber extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 序列号ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 所属批次
     */
    private Long batchId;

    /**
     * 批次号(冗余)
     */
    private String batchNo;

    /**
     * 规格ID
     */
    private Long skuId;

    /**
     * 状态(0在库 1已出库 2报废)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
