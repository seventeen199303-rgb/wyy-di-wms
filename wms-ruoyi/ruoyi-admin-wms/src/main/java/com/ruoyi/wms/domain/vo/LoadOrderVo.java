package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.LoadOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 装车单视图对象 wms_load_order
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = LoadOrder.class)
public class LoadOrderVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "装车单ID")
    private Long id;

    @ExcelProperty(value = "装车单号")
    private String loadNo;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "车牌号")
    private String vehicleNo;

    @ExcelProperty(value = "司机姓名")
    private String driverName;

    @ExcelProperty(value = "司机电话")
    private String driverPhone;

    @ExcelProperty(value = "总数量")
    private BigDecimal totalQuantity;

    @ExcelProperty(value = "计划单数量")
    private Integer totalPlan;

    @ExcelProperty(value = "发车时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;

    @ExcelProperty(value = "签收时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signTime;

    @ExcelProperty(value = "签收人")
    private String signName;

    @ExcelProperty(value = "备注")
    private String remark;

    /** 关联的发货计划单号列表 */
    private List<String> shipPlanNos;
}
