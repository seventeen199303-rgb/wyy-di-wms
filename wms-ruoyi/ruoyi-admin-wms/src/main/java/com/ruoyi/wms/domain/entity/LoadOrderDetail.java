package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 装车单明细对象 wms_load_order_detail
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_load_order_detail")
public class LoadOrderDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id")
    private Long id;

    /** 装车单ID */
    private Long loadOrderId;

    /** 发货计划ID */
    private Long shipPlanId;

    /** 发货计划单号 */
    private String shipPlanNo;
}
