package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.Zone;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;

/**
 * 库区视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Zone.class)
public class ZoneVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库区ID
     */
    @ExcelProperty(value = "库区ID")
    private Long id;

    /**
     * 库区编码
     */
    @ExcelProperty(value = "库区编码")
    private String zoneCode;

    /**
     * 库区名称
     */
    @ExcelProperty(value = "库区名称")
    private String zoneName;

    /**
     * 所属仓库
     */
    @ExcelProperty(value = "所属仓库")
    private Long warehouseId;

    /**
     * 库区类型
     */
    @ExcelProperty(value = "库区类型")
    private Integer zoneType;

    /**
     * 容量(库位数)
     */
    @ExcelProperty(value = "容量")
    private Integer capacity;

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

}
