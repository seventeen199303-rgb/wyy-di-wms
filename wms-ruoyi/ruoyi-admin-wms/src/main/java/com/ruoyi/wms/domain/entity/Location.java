package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 库位
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_location")
public class Location extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库位ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 库位编码
     */
    private String locationCode;

    /**
     * 库位名称
     */
    private String locationName;

    /**
     * 所属库区
     */
    private Long zoneId;

    /**
     * 所属仓库
     */
    private Long warehouseId;

    /**
     * 库位类型(1货架位 2地堆位 3托盘位)
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
     * 状态(0空闲 1占用 2禁用)
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
