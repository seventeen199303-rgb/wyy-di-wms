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
import com.ruoyi.wms.domain.bo.WaveBo;
import com.ruoyi.wms.domain.entity.WaveDetail;
import com.ruoyi.wms.domain.vo.WaveVo;
import com.ruoyi.wms.service.WaveService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/wave")
public class WaveController extends BaseController {

    private final WaveService waveService;

    @SaCheckPermission("wms:wave:list")
    @GetMapping("/list")
    public TableDataInfo<WaveVo> list(WaveBo bo, PageQuery pageQuery) {
        return waveService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("wms:wave:list")
    @GetMapping("/listNoPage")
    public R<List<WaveVo>> listNoPage(WaveBo bo) {
        return R.ok(waveService.queryList(bo));
    }

    @SaCheckPermission("wms:wave:list")
    @GetMapping("/{id}")
    public R<WaveVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(waveService.queryById(id));
    }

    /**
     * 查询波次关联的出库单
     */
    @SaCheckPermission("wms:wave:list")
    @GetMapping("/{id}/details")
    public R<List<WaveDetail>> details(@PathVariable Long id) {
        return R.ok(waveService.queryDetailsByWaveId(id));
    }

    @SaCheckPermission("wms:wave:edit")
    @Log(title = "波次", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody WaveBo bo) {
        waveService.insertByBo(bo);
        return R.ok();
    }

    @SaCheckPermission("wms:wave:edit")
    @Log(title = "波次", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody WaveBo bo) {
        waveService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 开始拣货
     */
    @SaCheckPermission("wms:wave:edit")
    @PostMapping("/{id}/start")
    public R<Void> start(@PathVariable Long id) {
        waveService.startPicking(id);
        return R.ok();
    }

    /**
     * 完成波次
     */
    @SaCheckPermission("wms:wave:edit")
    @PostMapping("/{id}/complete")
    public R<Void> complete(@PathVariable Long id) {
        waveService.completeWave(id);
        return R.ok();
    }

    /**
     * 取消波次
     */
    @SaCheckPermission("wms:wave:edit")
    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        waveService.cancelWave(id);
        return R.ok();
    }

    @SaCheckPermission("wms:wave:edit")
    @Log(title = "波次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        waveService.deleteById(id);
        return R.ok();
    }

}
