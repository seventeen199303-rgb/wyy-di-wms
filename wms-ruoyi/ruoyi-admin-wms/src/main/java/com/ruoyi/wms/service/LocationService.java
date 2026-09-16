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
import com.ruoyi.wms.domain.bo.LocationBo;
import com.ruoyi.wms.domain.entity.Location;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.entity.Zone;
import com.ruoyi.wms.domain.vo.LocationVo;
import com.ruoyi.wms.mapper.LocationMapper;
import com.ruoyi.wms.mapper.ZoneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 库位Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class LocationService extends ServiceImpl<LocationMapper, Location> {

    private final LocationMapper locationMapper;
    private final WarehouseService warehouseService;
    private final ZoneMapper zoneMapper;

    /**
     * 查询库位
     */
    public LocationVo queryById(Long id) {
        LocationVo vo = locationMapper.selectVoById(id);
        fillName(List.of(vo));
        return vo;
    }

    /**
     * 查询库位列表
     */
    public TableDataInfo<LocationVo> queryPageList(LocationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Location> lqw = buildQueryWrapper(bo);
        Page<LocationVo> result = locationMapper.selectVoPage(pageQuery.build(), lqw);
        fillName(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询库位列表(不分页)
     */
    public List<LocationVo> queryList(LocationBo bo) {
        LambdaQueryWrapper<Location> lqw = buildQueryWrapper(bo);
        List<LocationVo> list = locationMapper.selectVoList(lqw);
        fillName(list);
        return list;
    }

    private LambdaQueryWrapper<Location> buildQueryWrapper(LocationBo bo) {
        LambdaQueryWrapper<Location> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getLocationCode()), Location::getLocationCode, bo.getLocationCode());
        lqw.like(StrUtil.isNotBlank(bo.getLocationName()), Location::getLocationName, bo.getLocationName());
        lqw.eq(bo.getZoneId() != null, Location::getZoneId, bo.getZoneId());
        lqw.eq(bo.getWarehouseId() != null, Location::getWarehouseId, bo.getWarehouseId());
        lqw.eq(bo.getLocationType() != null, Location::getLocationType, bo.getLocationType());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), Location::getStatus, bo.getStatus());
        lqw.orderByAsc(Location::getWarehouseId).orderByAsc(Location::getZoneId).orderByAsc(Location::getOrderNum);
        return lqw;
    }

    private void fillName(List<LocationVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<Warehouse> warehouses = warehouseService.list();
        Map<Long, String> warehouseMap = warehouses.stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName, (a, b) -> a));
        List<Zone> zones = zoneMapper.selectList();
        Map<Long, String> zoneMap = zones.stream().collect(Collectors.toMap(Zone::getId, Zone::getZoneName, (a, b) -> a));
        list.forEach(vo -> {
            vo.setWarehouseName(warehouseMap.get(vo.getWarehouseId()));
            vo.setZoneName(zoneMap.get(vo.getZoneId()));
        });
    }

    /**
     * 新增库位
     */
    public void insertByBo(LocationBo bo) {
        validateLocation(bo);
        Location add = MapstructUtils.convert(bo, Location.class);
        add.setOrderNum(getNextOrderNum());
        if (add.getStatus() == null) {
            add.setStatus("0");
        }
        locationMapper.insert(add);
    }

    private Long getNextOrderNum() {
        LambdaQueryWrapper<Location> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Location::getOrderNum);
        wrapper.last("limit 1");
        Location location = locationMapper.selectOne(wrapper);
        return location == null ? 0L : location.getOrderNum() + 1;
    }

    /**
     * 修改库位
     */
    public void updateByBo(LocationBo bo) {
        validateLocation(bo);
        Location update = MapstructUtils.convert(bo, Location.class);
        locationMapper.updateById(update);
    }

    private void validateLocation(LocationBo bo) {
        LambdaQueryWrapper<Location> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Location::getLocationCode, bo.getLocationCode());
        List<Location> list = locationMapper.selectList(queryWrapper);
        boolean codeRepeat = list.stream().anyMatch(it -> Objects.equals(it.getLocationCode(), bo.getLocationCode()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(codeRepeat, "库位编码重复");
    }

    /**
     * 删除库位
     */
    public void deleteById(Long id) {
        locationMapper.deleteById(id);
    }

    public void deleteByIds(Collection<Long> ids) {
        locationMapper.deleteBatchIds(ids);
    }

    /**
     * 判断库区下是否存在库位
     */
    public boolean existsByZoneId(Long zoneId) {
        LambdaQueryWrapper<Location> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Location::getZoneId, zoneId);
        return locationMapper.exists(wrapper);
    }

    /**
     * 判断仓库下是否存在库位
     */
    public boolean existsByWarehouseId(Long warehouseId) {
        LambdaQueryWrapper<Location> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Location::getWarehouseId, warehouseId);
        return locationMapper.exists(wrapper);
    }

}
