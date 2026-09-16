package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 装车单对象 wms_load_order
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_load_order")
public class LoadOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id")
    private Long id;

    /** 装车单号 */
    private String loadNo;

    /** 状态: 0待发车 1在途 2已签收 3异常 */
    private String status;

    /** 车牌号 */
    private String vehicleNo;

    /** 司机姓名 */
    private String driverName;

    /** 司机电话 */
    private String driverPhone;

    /** 总数量 */
    private BigDecimal totalQuantity;

    /** 计划单数量 */
    private Integer totalPlan;

    /** 发车时间 */
    private LocalDateTime departTime;

    /** 签收时间 */
    private LocalDateTime signTime;

    /** 签收人 */
    private String signName;

    /** 备注 */
    private String remark;
}
