package com.ruoyi.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.bo.InventoryBo;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.vo.InventoryVo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import org.apache.ibatis.annotations.Param;

/**
 * 库存Mapper接口
 *
 * @author zcc
 * @date 2024-07-19
 */
public interface InventoryMapper extends BaseMapperPlus<Inventory, InventoryVo> {

    Page<InventoryVo> queryItemBoardList(Page<InventoryVo> page, @Param("bo") InventoryBo bo);
    Page<InventoryVo> queryWarehouseBoardList(Page<InventoryVo> page, @Param("bo") InventoryBo bo);

    /**
     * 安全库存预警查询
     */
    Page<InventoryWarningVo> querySafetyStockWarning(Page<InventoryWarningVo> page, @Param("warehouseId") Long warehouseId, @Param("zoneId") Long zoneId, @Param("itemName") String itemName);

    /**
     * 超储预警查询
     */
    Page<InventoryWarningVo> queryOverStockWarning(Page<InventoryWarningVo> page, @Param("warehouseId") Long warehouseId, @Param("zoneId") Long zoneId, @Param("itemName") String itemName);

    /**
     * 呆滞料查询
     */
    Page<InventoryWarningVo> querySlowMoving(Page<InventoryWarningVo> page, @Param("warehouseId") Long warehouseId, @Param("zoneId") Long zoneId, @Param("slowDays") Integer slowDays);

}
