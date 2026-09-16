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
import com.ruoyi.wms.domain.bo.ZoneBo;
import com.ruoyi.wms.domain.vo.ZoneVo;
import com.ruoyi.wms.service.ZoneService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库区
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/zone")
public class ZoneController extends BaseController {

    private final ZoneService zoneService;

    /**
     * 查询库区列表
     */
    @SaCheckPermission("wms:zone:list")
    @GetMapping("/list")
    public TableDataInfo<ZoneVo> list(ZoneBo bo, PageQuery pageQuery) {
        return zoneService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询库区列表(不分页)
     */
    @SaCheckPermission("wms:zone:list")
    @GetMapping("/listNoPage")
    public R<List<ZoneVo>> listNoPage(ZoneBo bo) {
        return R.ok(zoneService.queryList(bo));
    }

    /**
     * 获取库区详细信息
     */
    @SaCheckPermission("wms:zone:list")
    @GetMapping("/{id}")
    public R<ZoneVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(zoneService.queryById(id));
    }

    /**
     * 新增库区
     */
    @SaCheckPermission("wms:zone:edit")
    @Log(title = "库区", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ZoneBo bo) {
        zoneService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改库区
     */
    @SaCheckPermission("wms:zone:edit")
    @Log(title = "库区", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ZoneBo bo) {
        zoneService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除库区
     */
    @SaCheckPermission("wms:zone:edit")
    @Log(title = "库区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        zoneService.deleteById(id);
        return R.ok();
    }

}
