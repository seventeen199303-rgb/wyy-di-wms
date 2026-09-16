package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.Location;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库位业务对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Location.class, reverseConvertGenerate = false)
public class LocationBo extends BaseEntity {

    /**
     * 库位ID
     */
    @NotNull(message = "库位ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 库位编码
     */
    @NotBlank(message = "库位编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String locationCode;

    /**
     * 库位名称
     */
    private String locationName;

    /**
     * 所属库区
     */
    @NotNull(message = "所属库区不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long zoneId;

    /**
     * 所属仓库
     */
    @NotNull(message = "所属仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long warehouseId;

    /**
     * 库位类型
     */
    private Integer locationType;

    /**
     * 承重(kg)
     */
    private BigDecimal maxWeight;

    /**
     * 长(cm)
     */
    private BigDecimal length;

    /**
     * 宽(cm)
     */
    private BigDecimal width;

    /**
     * 高(cm)
     */
    private BigDecimal height;

    /**
     * 状态
     */
    private String status;

    /**
     * 显示顺序
     */
    private Long orderNum;

    /**
     * 备注
     */
    private String remark;

}
