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
 * 发货计划单对象 wms_ship_plan
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ship_plan")
public class ShipPlan extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id")
    private Long id;

    /** 计划单号 */
    private String planNo;

    /** 状态: 0待装车 1已装车 2已取消 */
    private String status;

    /** 客户ID(往来单位) */
    private Long customerId;

    /** 发货仓库ID */
    private Long warehouseId;

    /** 总数量 */
    private BigDecimal totalQuantity;

    /** SKU种类数 */
    private Integer totalSku;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 期望发货日期 */
    private LocalDate expectDate;

    /** 收货人 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 备注 */
    private String remark;
}
