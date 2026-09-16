package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.Wave;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Wave.class, reverseConvertGenerate = false)
public class WaveBo extends BaseEntity {

    @NotNull(message = "波次ID不能为空", groups = { EditGroup.class })
    private Long id;

    @NotBlank(message = "波次号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String waveNo;

    @NotNull(message = "所属仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long warehouseId;

    private String status;

    private BigDecimal totalQuantity;

    private Integer totalSku;

    private String remark;

    /** 关联的出库单ID列表 */
    private List<Long> shipmentOrderIds;

}
