package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.vo.PickingTaskVo;
import com.ruoyi.wms.service.PickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/picking")
public class PickingController extends BaseController {

    private final PickingService pickingService;

    /**
     * 查询波次的拣货任务
     */
    @SaCheckPermission("wms:picking:list")
    @GetMapping("/tasks/{waveId}")
    public R<List<PickingTaskVo>> tasks(@PathVariable Long waveId) {
        return R.ok(pickingService.queryPickingTasks(waveId));
    }

}
