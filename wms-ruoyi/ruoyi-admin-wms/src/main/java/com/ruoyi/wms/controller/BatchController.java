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
import com.ruoyi.wms.domain.bo.BatchBo;
import com.ruoyi.wms.domain.vo.BatchVo;
import com.ruoyi.wms.service.BatchService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 批次
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/batch")
public class BatchController extends BaseController {

    private final BatchService batchService;

    /**
     * 查询批次列表
     */
    @SaCheckPermission("wms:batch:list")
    @GetMapping("/list")
    public TableDataInfo<BatchVo> list(BatchBo bo, PageQuery pageQuery) {
        return batchService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询批次列表(不分页)
     */
    @SaCheckPermission("wms:batch:list")
    @GetMapping("/listNoPage")
    public R<List<BatchVo>> listNoPage(BatchBo bo) {
        return R.ok(batchService.queryList(bo));
    }

    /**
     * 获取批次详细信息
     */
    @SaCheckPermission("wms:batch:list")
    @GetMapping("/{id}")
    public R<BatchVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(batchService.queryById(id));
    }

    /**
     * 新增批次
     */
    @SaCheckPermission("wms:batch:edit")
    @Log(title = "批次", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BatchBo bo) {
        batchService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改批次
     */
    @SaCheckPermission("wms:batch:edit")
    @Log(title = "批次", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BatchBo bo) {
        batchService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除批次
     */
    @SaCheckPermission("wms:batch:edit")
    @Log(title = "批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        batchService.deleteById(id);
        return R.ok();
    }

}
