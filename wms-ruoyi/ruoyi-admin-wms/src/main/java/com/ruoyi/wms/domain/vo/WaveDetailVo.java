package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.WaveDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = WaveDetail.class)
public class WaveDetailVo extends BaseVo {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long waveId;

    private Long shipmentOrderId;

    private String shipmentOrderNo;

}
