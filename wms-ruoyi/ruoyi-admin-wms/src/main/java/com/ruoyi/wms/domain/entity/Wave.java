package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_wave")
public class Wave extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    /** 波次号 */
    private String waveNo;

    /** 所属仓库 */
    private Long warehouseId;

    /** 状态(0待拣货 1拣货中 2已完成 3已取消) */
    private String status;

    /** 总数量 */
    private BigDecimal totalQuantity;

    /** SKU种类数 */
    private Integer totalSku;

    /** 备注 */
    private String remark;

}
