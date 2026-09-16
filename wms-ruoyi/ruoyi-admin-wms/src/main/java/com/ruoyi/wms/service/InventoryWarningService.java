package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import com.ruoyi.wms.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 库存预警Service
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class InventoryWarningService {

    private final InventoryMapper inventoryMapper;

    /**
     * 安全库存预警查询
     */
    public TableDataInfo<InventoryWarningVo> querySafetyStockWarning(Long warehouseId, Long zoneId, String itemName, PageQuery pageQuery) {
        Page<InventoryWarningVo> result = inventoryMapper.querySafetyStockWarning(pageQuery.build(), warehouseId, zoneId, itemName);
        fillDifferenceForSafety(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 超储预警查询
     */
    public TableDataInfo<InventoryWarningVo> queryOverStockWarning(Long warehouseId, Long zoneId, String itemName, PageQuery pageQuery) {
        Page<InventoryWarningVo> result = inventoryMapper.queryOverStockWarning(pageQuery.build(), warehouseId, zoneId, itemName);
        fillDifferenceForOver(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 呆滞料查询
     */
    public TableDataInfo<InventoryWarningVo> querySlowMoving(Long warehouseId, Long zoneId, Integer slowDays, PageQuery pageQuery) {
        if (slowDays == null) {
            slowDays = 30;
        }
        Page<InventoryWarningVo> result = inventoryMapper.querySlowMoving(pageQuery.build(), warehouseId, zoneId, slowDays);
        return TableDataInfo.build(result);
    }

    /**
     * 计算安全预警差额(安全库存-当前库存)
     */
    private void fillDifferenceForSafety(List<InventoryWarningVo> list) {
        list.forEach(vo -> {
            if (vo.getSafetyStock() != null && vo.getQuantity() != null) {
                vo.setDifference(vo.getSafetyStock().subtract(vo.getQuantity()));
            }
        });
    }

    /**
     * 计算超储差额(当前库存-最大库存)
     */
    private void fillDifferenceForOver(List<InventoryWarningVo> list) {
        list.forEach(vo -> {
            if (vo.getMaxStock() != null && vo.getQuantity() != null) {
                vo.setDifference(vo.getQuantity().subtract(vo.getMaxStock()));
            }
        });
    }
}
