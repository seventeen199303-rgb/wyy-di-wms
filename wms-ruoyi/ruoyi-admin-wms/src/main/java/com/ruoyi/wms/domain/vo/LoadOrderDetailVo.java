package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.LoadOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = LoadOrderDetail.class)
public class LoadOrderDetailVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long loadOrderId;

    private Long shipPlanId;

    private String shipPlanNo;
}
