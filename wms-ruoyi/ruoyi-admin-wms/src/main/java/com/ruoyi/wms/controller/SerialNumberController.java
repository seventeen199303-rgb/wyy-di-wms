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
import com.ruoyi.wms.domain.bo.SerialNumberBo;
import com.ruoyi.wms.domain.vo.SerialNumberVo;
import com.ruoyi.wms.service.SerialNumberService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/serial")
public class SerialNumberController extends BaseController {

    private final SerialNumberService serialService;

    @SaCheckPermission("wms:serial:list")
    @GetMapping("/list")
    public TableDataInfo<SerialNumberVo> list(SerialNumberBo bo, PageQuery pageQuery) {
        return serialService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("wms:serial:list")
    @GetMapping("/listNoPage")
    public R<List<SerialNumberVo>> listNoPage(SerialNumberBo bo) {
        return R.ok(serialService.queryList(bo));
    }

    @SaCheckPermission("wms:serial:list")
    @GetMapping("/{id}")
    public R<SerialNumberVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(serialService.queryById(id));
    }

    @SaCheckPermission("wms:serial:edit")
    @Log(title = "序列号", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SerialNumberBo bo) {
        serialService.insertByBo(bo);
        return R.ok();
    }

    @SaCheckPermission("wms:serial:edit")
    @Log(title = "序列号", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SerialNumberBo bo) {
        serialService.updateByBo(bo);
        return R.ok();
    }

    @SaCheckPermission("wms:serial:edit")
    @Log(title = "序列号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        serialService.deleteById(id);
        return R.ok();
    }

}
