package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.ShipPlan;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 发货计划单业务对象 wms_ship_plan
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ShipPlan.class, reverseConvertGenerate = false)
public class ShipPlanBo extends BaseEntity {

    @NotNull(message = "计划单ID不能为空", groups = { EditGroup.class })
    private Long id;

    @NotBlank(message = "计划单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String planNo;

    private String status;

    @NotNull(message = "客户不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long customerId;

    private Long warehouseId;

    private BigDecimal totalQuantity;

    private Integer totalSku;

    private BigDecimal totalAmount;

    private LocalDate expectDate;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    /** 关联的出库单ID列表 */
    private List<Long> shipmentOrderIds;
}
