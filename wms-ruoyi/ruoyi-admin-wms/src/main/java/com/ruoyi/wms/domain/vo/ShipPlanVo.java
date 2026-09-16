package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.ShipPlan;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 发货计划单视图对象 wms_ship_plan
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ShipPlan.class)
public class ShipPlanVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "计划单ID")
    private Long id;

    @ExcelProperty(value = "计划单号")
    private String planNo;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "客户ID")
    private Long customerId;

    @ExcelProperty(value = "发货仓库")
    private Long warehouseId;

    @ExcelProperty(value = "总数量")
    private BigDecimal totalQuantity;

    @ExcelProperty(value = "SKU种类数")
    private Integer totalSku;

    @ExcelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ExcelProperty(value = "期望发货日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectDate;

    @ExcelProperty(value = "收货人")
    private String receiverName;

    @ExcelProperty(value = "收货人电话")
    private String receiverPhone;

    @ExcelProperty(value = "收货地址")
    private String receiverAddress;

    @ExcelProperty(value = "备注")
    private String remark;

    /** 客户名称 */
    private String customerName;

    /** 仓库名称 */
    private String warehouseName;

    /** 关联的出库单号列表 */
    private List<String> shipmentOrderNos;

    /** 关联的出库单数量 */
    private Integer shipmentCount;
}
