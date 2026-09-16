package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.vo.CheckReportVo;
import com.ruoyi.wms.domain.vo.FlowReportVo;
import com.ruoyi.wms.domain.vo.FlowTrendVo;
import com.ruoyi.wms.domain.vo.TurnoverReportVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表Mapper接口
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
public interface ReportMapper {

    /**
     * 库存周转分析查询
     */
    List<TurnoverReportVo> queryTurnover(@Param("warehouseId") Long warehouseId,
                                         @Param("itemName") String itemName,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 出入库统计查询
     */
    List<FlowReportVo> queryFlow(@Param("warehouseId") Long warehouseId,
                                 @Param("itemName") String itemName,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 出入库月度趋势查询
     */
    List<FlowTrendVo> queryFlowTrend(@Param("warehouseId") Long warehouseId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 盘点差异查询
     */
    List<CheckReportVo> queryCheckDiff(@Param("warehouseId") Long warehouseId,
                                       @Param("itemName") String itemName,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);
}
