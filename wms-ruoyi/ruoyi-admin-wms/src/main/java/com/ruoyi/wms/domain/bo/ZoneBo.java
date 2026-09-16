package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.Zone;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库区业务对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Zone.class, reverseConvertGenerate = false)
public class ZoneBo extends BaseEntity {

    /**
     * 库区ID
     */
    @NotNull(message = "库区ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 库区编码
     */
    private String zoneCode;

    /**
     * 库区名称
     */
    @NotBlank(message = "库区名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String zoneName;

    /**
     * 所属仓库
     */
    @NotNull(message = "所属仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long warehouseId;

    /**
     * 库区类型
     */
    private Integer zoneType;

    /**
     * 容量(库位数)
     */
    private Integer capacity;

    /**
     * 显示顺序
     */
    private Long orderNum;

    /**
     * 备注
     */
    private String remark;

}
