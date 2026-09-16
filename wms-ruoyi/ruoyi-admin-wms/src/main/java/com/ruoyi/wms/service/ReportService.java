package com.ruoyi.wms.service;

import com.ruoyi.wms.domain.vo.CheckReportVo;
import com.ruoyi.wms.domain.vo.FlowReportVo;
import com.ruoyi.wms.domain.vo.FlowTrendVo;
import com.ruoyi.wms.domain.vo.TurnoverReportVo;
import com.ruoyi.wms.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportMapper reportMapper;

    /**
     * 库存周转分析
     */
    public List<TurnoverReportVo> queryTurnover(Long warehouseId, String itemName, LocalDateTime startTime, LocalDateTime endTime) {
        List<TurnoverReportVo> list = reportMapper.queryTurnover(warehouseId, itemName, startTime, endTime);
        for (TurnoverReportVo vo : list) {
            // 库存金额 = 库存数量 * 成本价
            if (vo.getStockQuantity() != null && vo.getCostPrice() != null) {
                vo.setStockAmount(vo.getStockQuantity().multiply(vo.getCostPrice()).setScale(2, RoundingMode.HALF_UP));
            }
            // 统计天数
            long days = calcDays(startTime, endTime);
            if (days <= 0) {
                days = 30;
            }
            // 日均出库量
            if (vo.getOutQuantity() != null) {
                vo.setAvgDailyOut(vo.getOutQuantity().divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP));
            }
            // 周转率 = 期间出库量 / 当前库存(库存为0时记0)
            if (vo.getOutQuantity() != null && vo.getStockQuantity() != null && vo.getStockQuantity().signum() > 0) {
                vo.setTurnoverRate(vo.getOutQuantity().divide(vo.getStockQuantity(), 2, RoundingMode.HALF_UP));
            } else {
                vo.setTurnoverRate(BigDecimal.ZERO);
            }
            // 周转天数 = 当前库存 / 日均出库(日均出库为0时,周转天数记为库存可支撑天数无法计算,置空或0)
            if (vo.getAvgDailyOut() != null && vo.getAvgDailyOut().signum() > 0 && vo.getStockQuantity() != null) {
                vo.setTurnoverDays(vo.getStockQuantity().divide(vo.getAvgDailyOut(), 1, RoundingMode.HALF_UP));
            } else {
                vo.setTurnoverDays(BigDecimal.ZERO);
            }
        }
        return list;
    }

    /**
     * 出入库统计报表
     */
    public List<FlowReportVo> queryFlow(Long warehouseId, String itemName, LocalDateTime startTime, LocalDateTime endTime) {
        List<FlowReportVo> list = reportMapper.queryFlow(warehouseId, itemName, startTime, endTime);
        for (FlowReportVo vo : list) {
            if (vo.getCostPrice() != null) {
                if (vo.getInQuantity() != null) {
                    vo.setInAmount(vo.getInQuantity().multiply(vo.getCostPrice()).setScale(2, RoundingMode.HALF_UP));
                }
                if (vo.getOutQuantity() != null) {
                    vo.setOutAmount(vo.getOutQuantity().multiply(vo.getCostPrice()).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        return list;
    }

    /**
     * 出入库月度趋势
     */
    public List<FlowTrendVo> queryFlowTrend(Long warehouseId, LocalDateTime startTime, LocalDateTime endTime) {
        return reportMapper.queryFlowTrend(warehouseId, startTime, endTime);
    }

    /**
     * 盘点差异报表
     */
    public List<CheckReportVo> queryCheckDiff(Long warehouseId, String itemName, LocalDateTime startTime, LocalDateTime endTime) {
        List<CheckReportVo> list = reportMapper.queryCheckDiff(warehouseId, itemName, startTime, endTime);
        for (CheckReportVo vo : list) {
            if (vo.getDiffQuantity() != null && vo.getCostPrice() != null) {
                vo.setDiffAmount(vo.getDiffQuantity().multiply(vo.getCostPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return list;
    }

    private long calcDays(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate());
            return Math.max(days, 1);
        }
        return 30;
    }
}
