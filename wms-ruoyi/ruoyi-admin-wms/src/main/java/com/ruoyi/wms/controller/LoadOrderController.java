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
import com.ruoyi.wms.domain.bo.LoadOrderBo;
import com.ruoyi.wms.domain.entity.LoadOrderDetail;
import com.ruoyi.wms.domain.vo.LoadOrderVo;
import com.ruoyi.wms.service.LoadOrderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装车单
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/loadOrder")
public class LoadOrderController extends BaseController {

    private final LoadOrderService loadOrderService;

    @SaCheckPermission("les:loading:list")
    @GetMapping("/list")
    public TableDataInfo<LoadOrderVo> list(LoadOrderBo bo, PageQuery pageQuery) {
        return loadOrderService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("les:loading:list")
    @GetMapping("/listNoPage")
    public R<List<LoadOrderVo>> listNoPage(LoadOrderBo bo) {
        return R.ok(loadOrderService.queryList(bo));
    }

    @SaCheckPermission("les:loading:list")
    @GetMapping("/{id}")
    public R<LoadOrderVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(loadOrderService.queryById(id));
    }

    @SaCheckPermission("les:loading:list")
    @GetMapping("/{id}/details")
    public R<List<LoadOrderDetail>> details(@PathVariable Long id) {
        return R.ok(loadOrderService.queryDetailsByLoadOrderId(id));
    }

    @SaCheckPermission("les:loading:edit")
    @Log(title = "装车单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LoadOrderBo bo) {
        loadOrderService.insertByBo(bo);
        return R.ok();
    }

    @SaCheckPermission("les:loading:edit")
    @Log(title = "装车单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LoadOrderBo bo) {
        loadOrderService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 发车
     */
    @SaCheckPermission("les:delivery:edit")
    @PostMapping("/{id}/depart")
    public R<Void> depart(@PathVariable Long id) {
        loadOrderService.depart(id);
        return R.ok();
    }

    /**
     * 签收
     */
    @SaCheckPermission("les:delivery:edit")
    @PostMapping("/{id}/sign")
    public R<Void> sign(@PathVariable Long id, String signName) {
        loadOrderService.sign(id, signName);
        return R.ok();
    }

    /**
     * 标记异常
     */
    @SaCheckPermission("les:delivery:edit")
    @PostMapping("/{id}/abnormal")
    public R<Void> abnormal(@PathVariable Long id, String remark) {
        loadOrderService.abnormal(id, remark);
        return R.ok();
    }

    /**
     * 异常恢复
     */
    @SaCheckPermission("les:delivery:edit")
    @PostMapping("/{id}/recover")
    public R<Void> recover(@PathVariable Long id) {
        loadOrderService.recover(id);
        return R.ok();
    }

    @SaCheckPermission("les:loading:edit")
    @Log(title = "装车单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        loadOrderService.deleteById(id);
        return R.ok();
    }
}
