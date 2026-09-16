package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.WaveBo;
import com.ruoyi.wms.domain.entity.Wave;
import com.ruoyi.wms.domain.entity.WaveDetail;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.entity.ShipmentOrder;
import com.ruoyi.wms.domain.vo.WaveVo;
import com.ruoyi.wms.mapper.WaveMapper;
import com.ruoyi.wms.mapper.WaveDetailMapper;
import com.ruoyi.wms.mapper.WarehouseMapper;
import com.ruoyi.wms.mapper.ShipmentOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WaveService extends ServiceImpl<WaveMapper, Wave> {

    private final WaveMapper waveMapper;
    private final WaveDetailMapper waveDetailMapper;
    private final WarehouseMapper warehouseMapper;
    private final ShipmentOrderMapper shipmentOrderMapper;

    public WaveVo queryById(Long id) {
        WaveVo vo = waveMapper.selectVoById(id);
        fillWarehouseName(List.of(vo));
        fillShipmentNos(List.of(vo));
        return vo;
    }

    public TableDataInfo<WaveVo> queryPageList(WaveBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Wave> lqw = buildQueryWrapper(bo);
        Page<WaveVo> result = waveMapper.selectVoPage(pageQuery.build(), lqw);
        fillWarehouseName(result.getRecords());
        fillShipmentNos(result.getRecords());
        return TableDataInfo.build(result);
    }

    public List<WaveVo> queryList(WaveBo bo) {
        LambdaQueryWrapper<Wave> lqw = buildQueryWrapper(bo);
        List<WaveVo> list = waveMapper.selectVoList(lqw);
        fillWarehouseName(list);
        fillShipmentNos(list);
        return list;
    }

    private LambdaQueryWrapper<Wave> buildQueryWrapper(WaveBo bo) {
        LambdaQueryWrapper<Wave> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getWaveNo()), Wave::getWaveNo, bo.getWaveNo());
        lqw.eq(bo.getWarehouseId() != null, Wave::getWarehouseId, bo.getWarehouseId());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), Wave::getStatus, bo.getStatus());
        lqw.orderByDesc(Wave::getId);
        return lqw;
    }

    private void fillWarehouseName(List<WaveVo> list) {
        if (CollUtil.isEmpty(list)) return;
        Map<Long, String> map = warehouseMapper.selectList().stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName, (a, b) -> a));
        list.forEach(vo -> vo.setWarehouseName(map.get(vo.getWarehouseId())));
    }

    private void fillShipmentNos(List<WaveVo> list) {
        if (CollUtil.isEmpty(list)) return;
        for (WaveVo vo : list) {
            List<WaveDetail> details = queryDetailsByWaveId(vo.getId());
            vo.setShipmentOrderNos(details.stream().map(WaveDetail::getShipmentOrderNo).filter(Objects::nonNull).collect(Collectors.toList()));
            vo.setShipmentCount(details.size());
        }
    }

    public List<WaveDetail> queryDetailsByWaveId(Long waveId) {
        return waveDetailMapper.selectList(Wrappers.<WaveDetail>lambdaQuery().eq(WaveDetail::getWaveId, waveId));
    }

    /**
     * 新增波次（关联出库单）
     */
    @Transactional
    public void insertByBo(WaveBo bo) {
        validateWaveNo(bo);
        Wave add = MapstructUtils.convert(bo, Wave.class);
        if (add.getStatus() == null) add.setStatus("0");
        // 统计总数量/SKU
        calculateTotals(add, bo.getShipmentOrderIds());
        waveMapper.insert(add);
        // 保存波次明细
        saveDetails(add.getId(), bo.getShipmentOrderIds());
    }

    @Transactional
    public void updateByBo(WaveBo bo) {
        validateWaveNo(bo);
        Wave update = MapstructUtils.convert(bo, Wave.class);
        calculateTotals(update, bo.getShipmentOrderIds());
        waveMapper.updateById(update);
        // 重新保存明细
        waveDetailMapper.delete(Wrappers.<WaveDetail>lambdaQuery().eq(WaveDetail::getWaveId, bo.getId()));
        saveDetails(bo.getId(), bo.getShipmentOrderIds());
    }

    private void calculateTotals(Wave wave, List<Long> shipmentOrderIds) {
        if (CollUtil.isEmpty(shipmentOrderIds)) {
            wave.setTotalQuantity(BigDecimal.ZERO);
            wave.setTotalSku(0);
            return;
        }
        List<ShipmentOrder> orders = shipmentOrderMapper.selectBatchIds(shipmentOrderIds);
        BigDecimal totalQty = orders.stream().map(ShipmentOrder::getTotalQuantity).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        wave.setTotalQuantity(totalQty);
        wave.setTotalSku(orders.size());
    }

    private void saveDetails(Long waveId, List<Long> shipmentOrderIds) {
        if (CollUtil.isEmpty(shipmentOrderIds)) return;
        List<ShipmentOrder> orders = shipmentOrderMapper.selectBatchIds(shipmentOrderIds);
        for (ShipmentOrder order : orders) {
            WaveDetail detail = new WaveDetail();
            detail.setWaveId(waveId);
            detail.setShipmentOrderId(order.getId());
            detail.setShipmentOrderNo(order.getOrderNo());
            waveDetailMapper.insert(detail);
        }
    }

    private void validateWaveNo(WaveBo bo) {
        LambdaQueryWrapper<Wave> lqw = Wrappers.lambdaQuery();
        lqw.eq(Wave::getWaveNo, bo.getWaveNo());
        List<Wave> list = waveMapper.selectList(lqw);
        boolean repeat = list.stream().anyMatch(it -> Objects.equals(it.getWaveNo(), bo.getWaveNo()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(repeat, "波次号重复");
    }

    /**
     * 状态流转：开始拣货
     */
    public void startPicking(Long id) {
        Wave wave = waveMapper.selectById(id);
        Assert.notNull(wave, "波次不存在");
        Assert.isTrue("0".equals(wave.getStatus()), "只有待拣货的波次才能开始拣货");
        wave.setStatus("1");
        waveMapper.updateById(wave);
    }

    /**
     * 状态流转：完成波次
     */
    public void completeWave(Long id) {
        Wave wave = waveMapper.selectById(id);
        Assert.notNull(wave, "波次不存在");
        Assert.isTrue("0".equals(wave.getStatus()) || "1".equals(wave.getStatus()), "波次已结束，无法完成");
        wave.setStatus("2");
        waveMapper.updateById(wave);
    }

    /**
     * 状态流转：取消波次
     */
    public void cancelWave(Long id) {
        Wave wave = waveMapper.selectById(id);
        Assert.notNull(wave, "波次不存在");
        Assert.isTrue(!"2".equals(wave.getStatus()), "已完成的波次无法取消");
        wave.setStatus("3");
        waveMapper.updateById(wave);
    }

    public void deleteById(Long id) {
        waveDetailMapper.delete(Wrappers.<WaveDetail>lambdaQuery().eq(WaveDetail::getWaveId, id));
        waveMapper.deleteById(id);
    }

}
