package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.Batch;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 批次视图对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Batch.class)
public class BatchVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    @ExcelProperty(value = "批次ID")
    private Long id;

    /**
     * 批次号
     */
    @ExcelProperty(value = "批次号")
    private String batchNo;

    /**
     * 规格ID
     */
    @ExcelProperty(value = "规格ID")
    private Long skuId;

    /**
     * 批次数量
     */
    @ExcelProperty(value = "批次数量")
    private BigDecimal quantity;

    /**
     * 生产日期
     */
    @ExcelProperty(value = "生产日期")
    private LocalDate productionDate;

    /**
     * 有效期至
     */
    @ExcelProperty(value = "有效期至")
    private LocalDate expiryDate;

    /**
     * 供应商/来源
     */
    @ExcelProperty(value = "供应商/来源")
    private String supplier;

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

    /**
     * 商品单位
     */
    private String unit;

}
