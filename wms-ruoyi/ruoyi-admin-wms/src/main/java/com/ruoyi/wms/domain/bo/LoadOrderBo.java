package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.LoadOrder;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 装车单业务对象 wms_load_order
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = LoadOrder.class, reverseConvertGenerate = false)
public class LoadOrderBo extends BaseEntity {

    @NotNull(message = "装车单ID不能为空", groups = { EditGroup.class })
    private Long id;

    @NotBlank(message = "装车单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String loadNo;

    private String status;

    private String vehicleNo;

    private String driverName;

    private String driverPhone;

    private BigDecimal totalQuantity;

    private Integer totalPlan;

    private LocalDateTime departTime;

    private LocalDateTime signTime;

    private String signName;

    private String remark;

    /** 关联的发货计划ID列表(一车多计划) */
    private List<Long> shipPlanIds;
}
