package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.WaveDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = WaveDetail.class, reverseConvertGenerate = false)
public class WaveDetailBo extends BaseEntity {

    private Long id;

    private Long waveId;

    private Long shipmentOrderId;

    private String shipmentOrderNo;

}
