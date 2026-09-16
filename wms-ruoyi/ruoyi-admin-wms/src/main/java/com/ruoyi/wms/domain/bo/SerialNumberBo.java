package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.SerialNumber;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 序列号业务对象
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SerialNumber.class, reverseConvertGenerate = false)
public class SerialNumberBo extends BaseEntity {

    /**
     * 序列号ID
     */
    @NotNull(message = "序列号ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 序列号
     */
    @NotBlank(message = "序列号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String serialNo;

    /**
     * 所属批次
     */
    private Long batchId;

    /**
     * 批次号(冗余)
     */
    private String batchNo;

    /**
     * 规格ID
     */
    private Long skuId;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
