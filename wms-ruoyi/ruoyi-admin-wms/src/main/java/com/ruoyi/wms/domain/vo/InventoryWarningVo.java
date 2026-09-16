package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存预警视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
public class InventoryWarningVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 库存ID
     */
    private Long inventoryId;

    /**
     * 规格ID
     */
    private Long skuId;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 规格编码
     */
    private String skuCode;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 单位
     */
    private String unit;

    /**
     * 所属仓库
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 所属库区
     */
    private Long zoneId;

    /**
     * 库区名称
     */
    private String zoneName;

    /**
     * 所属库位
     */
    private Long locationId;

    /**
     * 库位编码
     */
    private String locationCode;

    /**
     * 当前库存
     */
    private BigDecimal quantity;

    /**
     * 安全库存下限
     */
    private BigDecimal safetyStock;

    /**
     * 最大库存上限
     */
    private BigDecimal maxStock;

    /**
     * 预警差额(安全预警: 安全库存-当前库存, 超储: 当前库存-最大库存)
     */
    private BigDecimal difference;

    /**
     * 最后出入库时间
     */
    private String lastFlowTime;

    /**
     * 呆滞天数
     */
    private Long slowDays;
}
