package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.wms.domain.entity.*;
import com.ruoyi.wms.domain.vo.PickingTaskVo;
import com.ruoyi.wms.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 拣货任务Service
 */
@RequiredArgsConstructor
@Service
public class PickingService {

    private final WaveService waveService;
    private final ShipmentOrderDetailMapper shipmentOrderDetailMapper;
    private final ItemSkuMapper itemSkuMapper;
    private final ItemMapper itemMapper;
    private final LocationMapper locationMapper;
    private final ZoneMapper zoneMapper;

    /**
     * 查询波次的拣货任务（按库位排序）
     */
    public List<PickingTaskVo> queryPickingTasks(Long waveId) {
        // 查出波次关联的出库单
        List<WaveDetail> waveDetails = waveService.queryDetailsByWaveId(waveId);
        if (CollUtil.isEmpty(waveDetails)) {
            return new ArrayList<>();
        }
        List<Long> shipmentOrderIds = waveDetails.stream().map(WaveDetail::getShipmentOrderId).collect(Collectors.toList());
        Map<Long, String> orderNoMap = waveDetails.stream().collect(Collectors.toMap(WaveDetail::getShipmentOrderId, WaveDetail::getShipmentOrderNo, (a, b) -> a));

        // 查出所有出库单明细
        List<ShipmentOrderDetail> details = new ArrayList<>();
        for (Long orderId : shipmentOrderIds) {
            details.addAll(shipmentOrderDetailMapper.selectList(Wrappers.<ShipmentOrderDetail>lambdaQuery().eq(ShipmentOrderDetail::getOrderId, orderId)));
        }

        // 填充 SKU/商品/库位信息
        Map<Long, ItemSku> skuMap = itemSkuMapper.selectList().stream().collect(Collectors.toMap(ItemSku::getId, s -> s, (a, b) -> a));
        Map<Long, Item> itemMap = itemMapper.selectList().stream().collect(Collectors.toMap(Item::getId, i -> i, (a, b) -> a));
        Map<Long, Location> locMap = locationMapper.selectList().stream().collect(Collectors.toMap(Location::getId, l -> l, (a, b) -> a));
        Map<Long, Zone> zoneMap = zoneMapper.selectList().stream().collect(Collectors.toMap(Zone::getId, z -> z, (a, b) -> a));

        List<PickingTaskVo> tasks = new ArrayList<>();
        for (ShipmentOrderDetail d : details) {
            PickingTaskVo task = new PickingTaskVo();
            task.setShipmentOrderNo(orderNoMap.get(d.getOrderId()));
            task.setSkuId(d.getSkuId());
            task.setQuantity(d.getQuantity());
            task.setLocationId(d.getLocationId());
            task.setStatus("0");
            ItemSku sku = skuMap.get(d.getSkuId());
            if (sku != null) {
                task.setSkuName(sku.getSkuName());
                Item item = itemMap.get(sku.getItemId());
                if (item != null) {
                    task.setItemName(item.getItemName());
                }
            }
            if (d.getLocationId() != null) {
                Location loc = locMap.get(d.getLocationId());
                if (loc != null) {
                    task.setLocationCode(loc.getLocationCode());
                    if (loc.getZoneId() != null) {
                        Zone zone = zoneMap.get(loc.getZoneId());
                        if (zone != null) {
                            task.setZoneName(zone.getZoneName());
                        }
                    }
                }
            }
            tasks.add(task);
        }

        // 按库位排序（优化拣货路径）
        tasks.sort(Comparator.comparing(PickingTaskVo::getLocationCode, Comparator.nullsLast(String::compareTo))
            .thenComparing(PickingTaskVo::getItemName, Comparator.nullsLast(String::compareTo)));
        return tasks;
    }
}
