package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出入库月度趋势视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
public class FlowTrendVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 月份 yyyy-MM */
    private String month;

    /** 入库数量 */
    private BigDecimal inQuantity;

    /** 出库数量 */
    private BigDecimal outQuantity;

    /** 入库金额 */
    private BigDecimal inAmount;

    /** 出库金额 */
    private BigDecimal outAmount;
}
