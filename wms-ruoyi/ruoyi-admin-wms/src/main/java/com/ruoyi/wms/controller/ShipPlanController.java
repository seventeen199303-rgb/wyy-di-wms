package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.ShipPlanBo;
import com.ruoyi.wms.domain.entity.ShipPlanDetail;
import com.ruoyi.wms.domain.vo.ShipPlanVo;
import com.ruoyi.wms.service.ShipPlanService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发货计划
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/shipPlan")
public class ShipPlanController extends BaseController {

    private final ShipPlanService shipPlanService;

    @SaCheckPermission("les:shipPlan:list")
    @GetMapping("/list")
    public TableDataInfo<ShipPlanVo> list(ShipPlanBo bo, PageQuery pageQuery) {
        return shipPlanService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("les:shipPlan:list")
    @GetMapping("/listNoPage")
    public R<List<ShipPlanVo>> listNoPage(ShipPlanBo bo) {
        return R.ok(shipPlanService.queryList(bo));
    }

    @SaCheckPermission("les:shipPlan:list")
    @GetMapping("/{id}")
    public R<ShipPlanVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(shipPlanService.queryById(id));
    }

    @SaCheckPermission("les:shipPlan:list")
    @GetMapping("/{id}/details")
    public R<List<ShipPlanDetail>> details(@PathVariable Long id) {
        return R.ok(shipPlanService.queryDetailsByPlanId(id));
    }

    @SaCheckPermission("les:shipPlan:edit")
    @Log(title = "发货计划", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShipPlanBo bo) {
        shipPlanService.insertByBo(bo);
        return R.ok();
    }

    @SaCheckPermission("les:shipPlan:edit")
    @Log(title = "发货计划", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShipPlanBo bo) {
        shipPlanService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 取消发货计划
     */
    @SaCheckPermission("les:shipPlan:edit")
    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        shipPlanService.cancel(id);
        return R.ok();
    }

    @SaCheckPermission("les:shipPlan:edit")
    @Log(title = "发货计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        shipPlanService.deleteById(id);
        return R.ok();
    }
}
