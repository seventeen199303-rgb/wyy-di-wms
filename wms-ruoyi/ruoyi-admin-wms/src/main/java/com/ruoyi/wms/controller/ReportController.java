package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.wms.domain.vo.CheckReportVo;
import com.ruoyi.wms.domain.vo.FlowReportVo;
import com.ruoyi.wms.domain.vo.FlowTrendVo;
import com.ruoyi.wms.domain.vo.TurnoverReportVo;
import com.ruoyi.wms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表中心
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/report")
public class ReportController {

    private final ReportService reportService;

    /**
     * 库存周转分析
     */
    @SaCheckPermission("wms:report:turnover")
    @GetMapping("/turnover")
    public R<List<TurnoverReportVo>> turnover(Long warehouseId, String itemName,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(reportService.queryTurnover(warehouseId, itemName, startTime, endTime));
    }

    /**
     * 出入库统计报表
     */
    @SaCheckPermission("wms:report:flow")
    @GetMapping("/flow")
    public R<List<FlowReportVo>> flow(Long warehouseId, String itemName,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(reportService.queryFlow(warehouseId, itemName, startTime, endTime));
    }

    /**
     * 出入库月度趋势
     */
    @SaCheckPermission("wms:report:flow")
    @GetMapping("/flow/trend")
    public R<List<FlowTrendVo>> flowTrend(Long warehouseId,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(reportService.queryFlowTrend(warehouseId, startTime, endTime));
    }

    /**
     * 盘点差异报表
     */
    @SaCheckPermission("wms:report:check")
    @GetMapping("/check")
    public R<List<CheckReportVo>> check(Long warehouseId, String itemName,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(reportService.queryCheckDiff(warehouseId, itemName, startTime, endTime));
    }
}
