package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 拣货任务视图对象
 */
@Data
public class PickingTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 出库单号 */
    private String shipmentOrderNo;

    /** 商品名称 */
    private String itemName;

    /** 规格名称 */
    private String skuName;

    /** SKU ID */
    private Long skuId;

    /** 库位ID */
    private Long locationId;

    /** 库位编码 */
    private String locationCode;

    /** 库区名称 */
    private String zoneName;

    /** 拣货数量 */
    private BigDecimal quantity;

    /** 拣货状态(0待拣 1已拣) */
    private String status;

}
