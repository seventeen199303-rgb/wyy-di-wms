package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.MovementOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 移库单视图对象 wms_movement_order
 *
 * @author zcc
 * @date 2024-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = MovementOrder.class)
public class MovementOrderVo extends BaseOrderVo<MovementOrderDetailVo>{

    /**
     * 源仓库
     */
    @ExcelProperty(value = "源仓库")
    private Long sourceWarehouseId;

    /**
     * 目标仓库
     */
    @ExcelProperty(value = "目标仓库")
    private Long targetWarehouseId;

    /**
     * 目标库区名称(聚合,逗号分隔)
     */
    private String targetZoneNames;

    /**
     * 目标库区编号(聚合,逗号分隔)
     */
    private String targetZoneCodes;

    /**
     * 目标库位名称(聚合,逗号分隔)
     */
    private String targetLocationNames;

    /**
     * 目标库位编码(聚合,逗号分隔)
     */
    private String targetLocationCodes;
}
