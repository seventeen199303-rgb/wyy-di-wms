package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出入库统计报表视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
public class FlowReportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 规格ID */
    private Long skuId;

    /** 商品ID */
    private Long itemId;

    /** 商品编码 */
    private String itemCode;

    /** 商品名称 */
    private String itemName;

    /** 规格编码 */
    private String skuCode;

    /** 规格名称 */
    private String skuName;

    /** 单位 */
    private String unit;

    /** 期初库存 */
    private BigDecimal beginQuantity;

    /** 期间入库数量 */
    private BigDecimal inQuantity;

    /** 期间出库数量 */
    private BigDecimal outQuantity;

    /** 期末库存(结存) */
    private BigDecimal endQuantity;

    /** 期间盘盈盘亏(盘点调整,正数盘盈负数盘亏) */
    private BigDecimal checkQuantity;

    /** 成本单价 */
    private BigDecimal costPrice;

    /** 入库金额 */
    private BigDecimal inAmount;

    /** 出库金额 */
    private BigDecimal outAmount;
}
