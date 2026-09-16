package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.Location;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 库位视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Location.class)
public class LocationVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库位ID
     */
    @ExcelProperty(value = "库位ID")
    private Long id;

    /**
     * 库位编码
     */
    @ExcelProperty(value = "库位编码")
    private String locationCode;

    /**
     * 库位名称
     */
    @ExcelProperty(value = "库位名称")
    private String locationName;

    /**
     * 所属库区
     */
    @ExcelProperty(value = "所属库区")
    private Long zoneId;

    /**
     * 所属仓库
     */
    @ExcelProperty(value = "所属仓库")
    private Long warehouseId;

    /**
     * 库位类型
     */
    @ExcelProperty(value = "库位类型")
    private Integer locationType;

    /**
     * 承重(kg)
     */
    @ExcelProperty(value = "承重")
    private BigDecimal maxWeight;

    /**
     * 长(cm)
     */
    @ExcelProperty(value = "长")
    private BigDecimal length;

    /**
     * 宽(cm)
     */
    @ExcelProperty(value = "宽")
    private BigDecimal width;

    /**
     * 高(cm)
     */
    @ExcelProperty(value = "高")
    private BigDecimal height;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Long orderNum;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 库区名称
     */
    private String zoneName;

}
