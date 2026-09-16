package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.ZoneBo;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.entity.Zone;
import com.ruoyi.wms.domain.vo.ZoneVo;
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
 * 库区Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class ZoneService extends ServiceImpl<ZoneMapper, Zone> {

    private final ZoneMapper zoneMapper;
    private final WarehouseService warehouseService;
    private final LocationMapper locationMapper;

    /**
     * 查询库区
     */
    public ZoneVo queryById(Long id) {
        ZoneVo vo = zoneMapper.selectVoById(id);
        fillWarehouseName(List.of(vo));
        return vo;
    }

    /**
     * 查询库区列表
     */
    public TableDataInfo<ZoneVo> queryPageList(ZoneBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Zone> lqw = buildQueryWrapper(bo);
        Page<ZoneVo> result = zoneMapper.selectVoPage(pageQuery.build(), lqw);
        fillWarehouseName(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询库区列表(不分页)
     */
    public List<ZoneVo> queryList(ZoneBo bo) {
        LambdaQueryWrapper<Zone> lqw = buildQueryWrapper(bo);
        List<ZoneVo> list = zoneMapper.selectVoList(lqw);
        fillWarehouseName(list);
        return list;
    }

    private LambdaQueryWrapper<Zone> buildQueryWrapper(ZoneBo bo) {
        LambdaQueryWrapper<Zone> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getZoneCode()), Zone::getZoneCode, bo.getZoneCode());
        lqw.like(StrUtil.isNotBlank(bo.getZoneName()), Zone::getZoneName, bo.getZoneName());
        lqw.eq(bo.getWarehouseId() != null, Zone::getWarehouseId, bo.getWarehouseId());
        lqw.eq(bo.getZoneType() != null, Zone::getZoneType, bo.getZoneType());
        lqw.orderByAsc(Zone::getWarehouseId).orderByAsc(Zone::getOrderNum);
        return lqw;
    }

    private void fillWarehouseName(List<ZoneVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<Warehouse> warehouses = warehouseService.list();
        Map<Long, String> map = warehouses.stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName, (a, b) -> a));
        list.forEach(vo -> vo.setWarehouseName(map.get(vo.getWarehouseId())));
    }

    /**
     * 新增库区
     */
    public void insertByBo(ZoneBo bo) {
        validateZone(bo);
        Zone add = MapstructUtils.convert(bo, Zone.class);
        add.setOrderNum(getNextOrderNum());
        zoneMapper.insert(add);
    }

    private Long getNextOrderNum() {
        LambdaQueryWrapper<Zone> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Zone::getOrderNum);
        wrapper.last("limit 1");
        Zone zone = zoneMapper.selectOne(wrapper);
        return zone == null ? 0L : zone.getOrderNum() + 1;
    }

    /**
     * 修改库区
     */
    public void updateByBo(ZoneBo bo) {
        validateZone(bo);
        Zone update = MapstructUtils.convert(bo, Zone.class);
        zoneMapper.updateById(update);
    }

    private void validateZone(ZoneBo bo) {
        LambdaQueryWrapper<Zone> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Zone::getZoneName, bo.getZoneName())
            .or().eq(StrUtil.isNotBlank(bo.getZoneCode()), Zone::getZoneCode, bo.getZoneCode());
        List<Zone> list = zoneMapper.selectList(queryWrapper);
        boolean nameRepeat = list.stream().anyMatch(it -> Objects.equals(it.getZoneName(), bo.getZoneName()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(nameRepeat, "库区名称重复");
        boolean codeRepeat = list.stream().anyMatch(it -> Objects.equals(it.getZoneCode(), bo.getZoneCode()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(codeRepeat, "库区编码重复");
    }

    /**
     * 删除库区
     */
    public void deleteById(Long id) {
        if (existsByZoneId(id)) {
            throw new ServiceException("删除失败", HttpStatus.CONFLICT, "该库区下已有库位，无法删除！");
        }
        zoneMapper.deleteById(id);
    }

    public void deleteByIds(Collection<Long> ids) {
        zoneMapper.deleteBatchIds(ids);
    }

    /**
     * 判断库区下是否存在库位
     */
    public boolean existsByZoneId(Long zoneId) {
        return locationMapper.exists(Wrappers.<com.ruoyi.wms.domain.entity.Location>lambdaQuery().eq(com.ruoyi.wms.domain.entity.Location::getZoneId, zoneId));
    }

}
