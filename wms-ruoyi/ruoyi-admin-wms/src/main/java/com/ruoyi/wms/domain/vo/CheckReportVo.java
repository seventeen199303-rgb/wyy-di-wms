package com.ruoyi.wms.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盘点差异报表视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
public class CheckReportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 盘点单ID */
    private Long checkOrderId;

    /** 盘点单号 */
    private String orderNo;

    /** 盘点单状态 */
    private Integer orderStatus;

    /** 明细ID */
    private Long detailId;

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

    /** 仓库ID */
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;

    /** 库区名称 */
    private String zoneName;

    /** 库位编码 */
    private String locationCode;

    /** 账面数量 */
    private BigDecimal quantity;

    /** 实盘数量 */
    private BigDecimal checkQuantity;

    /** 盘盈盘亏数量(实盘-账面) */
    private BigDecimal diffQuantity;

    /** 成本单价 */
    private BigDecimal costPrice;

    /** 差异金额 */
    private BigDecimal diffAmount;

    /** 盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
