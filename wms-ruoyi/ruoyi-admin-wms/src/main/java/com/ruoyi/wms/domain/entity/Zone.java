package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 库区
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_zone")
public class Zone extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库区ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 库区编码
     */
    private String zoneCode;

    /**
     * 库区名称
     */
    private String zoneName;

    /**
     * 所属仓库
     */
    private Long warehouseId;

    /**
     * 库区类型(1存储区 2拣货区 3收货区 4发货区 5退货区 6质检区)
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
