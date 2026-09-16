package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存周转分析视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
public class TurnoverReportVo implements Serializable {

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

    /** 当前库存数量 */
    private BigDecimal stockQuantity;

    /** 期间出库数量 */
    private BigDecimal outQuantity;

    /** 期间入库数量 */
    private BigDecimal inQuantity;

    /** 成本单价 */
    private BigDecimal costPrice;

    /** 库存金额(成本价) */
    private BigDecimal stockAmount;

    /** 日均出库量 */
    private BigDecimal avgDailyOut;

    /** 库存周转天数(当前库存/日均出库) */
    private BigDecimal turnoverDays;

    /** 周转率(期间出库/当前库存) */
    private BigDecimal turnoverRate;
}
