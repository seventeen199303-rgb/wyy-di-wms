package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.Wave;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Wave.class)
public class WaveVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "波次ID")
    private Long id;

    @ExcelProperty(value = "波次号")
    private String waveNo;

    @ExcelProperty(value = "所属仓库")
    private Long warehouseId;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "总数量")
    private BigDecimal totalQuantity;

    @ExcelProperty(value = "SKU种类数")
    private Integer totalSku;

    @ExcelProperty(value = "备注")
    private String remark;

    /** 仓库名称 */
    private String warehouseName;

    /** 关联的出库单号列表 */
    private List<String> shipmentOrderNos;

    /** 关联的出库单数量 */
    private Integer shipmentCount;

}
