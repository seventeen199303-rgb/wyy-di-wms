package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.SerialNumber;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;

/**
 * 序列号视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SerialNumber.class)
public class SerialNumberVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 序列号ID
     */
    @ExcelProperty(value = "序列号ID")
    private Long id;

    /**
     * 序列号
     */
    @ExcelProperty(value = "序列号")
    private String serialNo;

    /**
     * 所属批次
     */
    @ExcelProperty(value = "所属批次")
    private Long batchId;

    /**
     * 批次号(冗余)
     */
    @ExcelProperty(value = "批次号")
    private String batchNo;

    /**
     * 规格ID
     */
    @ExcelProperty(value = "规格ID")
    private Long skuId;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 商品名称
     */
    private String itemName;

}
