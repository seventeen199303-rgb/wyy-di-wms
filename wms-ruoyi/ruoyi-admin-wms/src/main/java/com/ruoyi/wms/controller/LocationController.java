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
import com.ruoyi.wms.domain.bo.LocationBo;
import com.ruoyi.wms.domain.vo.LocationVo;
import com.ruoyi.wms.service.LocationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库位
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/location")
public class LocationController extends BaseController {

    private final LocationService locationService;

    /**
     * 查询库位列表
     */
    @SaCheckPermission("wms:location:list")
    @GetMapping("/list")
    public TableDataInfo<LocationVo> list(LocationBo bo, PageQuery pageQuery) {
        return locationService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询库位列表(不分页)
     */
    @SaCheckPermission("wms:location:list")
    @GetMapping("/listNoPage")
    public R<List<LocationVo>> listNoPage(LocationBo bo) {
        return R.ok(locationService.queryList(bo));
    }

    /**
     * 获取库位详细信息
     */
    @SaCheckPermission("wms:location:list")
    @GetMapping("/{id}")
    public R<LocationVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(locationService.queryById(id));
    }

    /**
     * 新增库位
     */
    @SaCheckPermission("wms:location:edit")
    @Log(title = "库位", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LocationBo bo) {
        locationService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改库位
     */
    @SaCheckPermission("wms:location:edit")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LocationBo bo) {
        locationService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除库位
     */
    @SaCheckPermission("wms:location:edit")
    @Log(title = "库位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        locationService.deleteById(id);
        return R.ok();
    }

}
