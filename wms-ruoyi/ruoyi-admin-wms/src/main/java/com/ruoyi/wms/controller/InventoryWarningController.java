package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import com.ruoyi.wms.service.InventoryWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存预警
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/warning")
public class InventoryWarningController extends BaseController {

    private final InventoryWarningService inventoryWarningService;

    /**
     * 安全库存预警列表
     */
    @SaCheckPermission("wms:warning:safety")
    @GetMapping("/safetyStock/list")
    public TableDataInfo<InventoryWarningVo> safetyStock(Long warehouseId, Long zoneId, String itemName, PageQuery pageQuery) {
        return inventoryWarningService.querySafetyStockWarning(warehouseId, zoneId, itemName, pageQuery);
    }

    /**
     * 超储预警列表
     */
    @SaCheckPermission("wms:warning:over")
    @GetMapping("/overStock/list")
    public TableDataInfo<InventoryWarningVo> overStock(Long warehouseId, Long zoneId, String itemName, PageQuery pageQuery) {
        return inventoryWarningService.queryOverStockWarning(warehouseId, zoneId, itemName, pageQuery);
    }

    /**
     * 呆滞料列表
     */
    @SaCheckPermission("wms:warning:slow")
    @GetMapping("/slowMoving/list")
    public TableDataInfo<InventoryWarningVo> slowMoving(Long warehouseId, Long zoneId, Integer slowDays, PageQuery pageQuery) {
        return inventoryWarningService.querySlowMoving(warehouseId, zoneId, slowDays, pageQuery);
    }
}
