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
import com.ruoyi.wms.domain.bo.LoadOrderBo;
import com.ruoyi.wms.domain.entity.LoadOrder;
import com.ruoyi.wms.domain.entity.LoadOrderDetail;
import com.ruoyi.wms.domain.entity.ShipPlan;
import com.ruoyi.wms.domain.vo.LoadOrderVo;
import com.ruoyi.wms.mapper.LoadOrderDetailMapper;
import com.ruoyi.wms.mapper.LoadOrderMapper;
import com.ruoyi.wms.mapper.ShipPlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 装车单Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class LoadOrderService extends ServiceImpl<LoadOrderMapper, LoadOrder> {

    private final LoadOrderMapper loadOrderMapper;
    private final LoadOrderDetailMapper loadOrderDetailMapper;
    private final ShipPlanMapper shipPlanMapper;

    public LoadOrderVo queryById(Long id) {
        LoadOrderVo vo = loadOrderMapper.selectVoById(id);
        Assert.notNull(vo, "装车单不存在");
        fillPlanNos(List.of(vo));
        return vo;
    }

    public TableDataInfo<LoadOrderVo> queryPageList(LoadOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<LoadOrder> lqw = buildQueryWrapper(bo);
        Page<LoadOrderVo> result = loadOrderMapper.selectVoPage(pageQuery.build(), lqw);
        fillPlanNos(result.getRecords());
        return TableDataInfo.build(result);
    }

    public List<LoadOrderVo> queryList(LoadOrderBo bo) {
        LambdaQueryWrapper<LoadOrder> lqw = buildQueryWrapper(bo);
        List<LoadOrderVo> list = loadOrderMapper.selectVoList(lqw);
        fillPlanNos(list);
        return list;
    }

    private LambdaQueryWrapper<LoadOrder> buildQueryWrapper(LoadOrderBo bo) {
        LambdaQueryWrapper<LoadOrder> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getLoadNo()), LoadOrder::getLoadNo, bo.getLoadNo());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), LoadOrder::getStatus, bo.getStatus());
        lqw.like(StrUtil.isNotBlank(bo.getVehicleNo()), LoadOrder::getVehicleNo, bo.getVehicleNo());
        lqw.like(StrUtil.isNotBlank(bo.getDriverName()), LoadOrder::getDriverName, bo.getDriverName());
        lqw.orderByDesc(LoadOrder::getId);
        return lqw;
    }

    private void fillPlanNos(List<LoadOrderVo> list) {
        if (CollUtil.isEmpty(list)) return;
        for (LoadOrderVo vo : list) {
            List<LoadOrderDetail> details = queryDetailsByLoadOrderId(vo.getId());
            vo.setShipPlanNos(details.stream().map(LoadOrderDetail::getShipPlanNo).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    public List<LoadOrderDetail> queryDetailsByLoadOrderId(Long loadOrderId) {
        return loadOrderDetailMapper.selectList(Wrappers.<LoadOrderDetail>lambdaQuery().eq(LoadOrderDetail::getLoadOrderId, loadOrderId));
    }

    /**
     * 新增装车单（一车多计划）
     * 业务规则:
     * 1. 只能选择"待装车"(status=0)的发货计划
     * 2. 一个发货计划只能装在一辆车上(不能重复装车)
     * 3. 装车后计划状态 0→1
     */
    @Transactional
    public void insertByBo(LoadOrderBo bo) {
        validateLoadNo(bo);
        List<ShipPlan> plans = validateAndGetPlans(bo.getShipPlanIds(), null);
        LoadOrder add = MapstructUtils.convert(bo, LoadOrder.class);
        if (StrUtil.isBlank(add.getStatus())) add.setStatus("0");
        add.setTotalQuantity(plans.stream().map(ShipPlan::getTotalQuantity).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        add.setTotalPlan(plans.size());
        loadOrderMapper.insert(add);
        saveDetails(add.getId(), plans);
        // 计划状态 0→1
        for (ShipPlan plan : plans) {
            plan.setStatus("1");
            shipPlanMapper.updateById(plan);
        }
    }

    @Transactional
    public void updateByBo(LoadOrderBo bo) {
        validateLoadNo(bo);
        LoadOrder existing = loadOrderMapper.selectById(bo.getId());
        Assert.notNull(existing, "装车单不存在");
        Assert.isTrue("0".equals(existing.getStatus()), "已发车的装车单不能修改");
        List<ShipPlan> plans = validateAndGetPlans(bo.getShipPlanIds(), bo.getId());
        LoadOrder update = MapstructUtils.convert(bo, LoadOrder.class);
        update.setTotalQuantity(plans.stream().map(ShipPlan::getTotalQuantity).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        update.setTotalPlan(plans.size());
        loadOrderMapper.updateById(update);
        // 重建明细：先释放旧计划(状态1→0)，再绑定新计划
        List<LoadOrderDetail> oldDetails = queryDetailsByLoadOrderId(bo.getId());
        for (LoadOrderDetail od : oldDetails) {
            ShipPlan oldPlan = shipPlanMapper.selectById(od.getShipPlanId());
            if (oldPlan != null && "1".equals(oldPlan.getStatus())) {
                oldPlan.setStatus("0");
                shipPlanMapper.updateById(oldPlan);
            }
        }
        loadOrderDetailMapper.delete(Wrappers.<LoadOrderDetail>lambdaQuery().eq(LoadOrderDetail::getLoadOrderId, bo.getId()));
        saveDetails(bo.getId(), plans);
        for (ShipPlan plan : plans) {
            plan.setStatus("1");
            shipPlanMapper.updateById(plan);
        }
    }

    private List<ShipPlan> validateAndGetPlans(List<Long> shipPlanIds, Long excludeLoadOrderId) {
        Assert.isFalse(CollUtil.isEmpty(shipPlanIds), "请选择要装车的发货计划");
        List<ShipPlan> plans = shipPlanMapper.selectBatchIds(shipPlanIds);
        Assert.isTrue(plans.size() == shipPlanIds.size(), "存在无效的发货计划");
        for (ShipPlan plan : plans) {
            Assert.isTrue("0".equals(plan.getStatus()), "发货计划【" + plan.getPlanNo() + "】不是待装车状态");
        }
        // 校验计划未被其他装车单占用
        for (ShipPlan plan : plans) {
            LambdaQueryWrapper<LoadOrderDetail> lqw = Wrappers.lambdaQuery();
            lqw.eq(LoadOrderDetail::getShipPlanId, plan.getId());
            if (excludeLoadOrderId != null) {
                lqw.ne(LoadOrderDetail::getLoadOrderId, excludeLoadOrderId);
            }
            Long count = loadOrderDetailMapper.selectCount(lqw);
            Assert.isTrue(count == 0, "发货计划【" + plan.getPlanNo() + "】已加入其他装车单");
        }
        return plans;
    }

    private void saveDetails(Long loadOrderId, List<ShipPlan> plans) {
        for (ShipPlan plan : plans) {
            LoadOrderDetail detail = new LoadOrderDetail();
            detail.setLoadOrderId(loadOrderId);
            detail.setShipPlanId(plan.getId());
            detail.setShipPlanNo(plan.getPlanNo());
            loadOrderDetailMapper.insert(detail);
        }
    }

    private void validateLoadNo(LoadOrderBo bo) {
        LambdaQueryWrapper<LoadOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(LoadOrder::getLoadNo, bo.getLoadNo());
        List<LoadOrder> list = loadOrderMapper.selectList(lqw);
        boolean repeat = list.stream().anyMatch(it -> Objects.equals(it.getLoadNo(), bo.getLoadNo()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(repeat, "装车单号重复");
    }

    /**
     * 发车（配送状态: 待发车0 → 在途1）
     */
    public void depart(Long id) {
        LoadOrder load = loadOrderMapper.selectById(id);
        Assert.notNull(load, "装车单不存在");
        Assert.isTrue("0".equals(load.getStatus()), "只有待发车的装车单才能发车");
        load.setStatus("1");
        load.setDepartTime(LocalDateTime.now());
        loadOrderMapper.updateById(load);
    }

    /**
     * 签收（配送状态: 在途1 → 已签收2）
     */
    public void sign(Long id, String signName) {
        LoadOrder load = loadOrderMapper.selectById(id);
        Assert.notNull(load, "装车单不存在");
        Assert.isTrue("1".equals(load.getStatus()), "只有在途的装车单才能签收");
        load.setStatus("2");
        load.setSignTime(LocalDateTime.now());
        load.setSignName(signName);
        loadOrderMapper.updateById(load);
    }

    /**
     * 标记异常（配送状态: 在途1 → 异常3）
     */
    public void abnormal(Long id, String remark) {
        LoadOrder load = loadOrderMapper.selectById(id);
        Assert.notNull(load, "装车单不存在");
        Assert.isTrue("1".equals(load.getStatus()), "只有在途的装车单才能标记异常");
        load.setStatus("3");
        load.setRemark(remark);
        loadOrderMapper.updateById(load);
    }

    /**
     * 异常恢复为在途（异常3 → 在途1）
     */
    public void recover(Long id) {
        LoadOrder load = loadOrderMapper.selectById(id);
        Assert.notNull(load, "装车单不存在");
        Assert.isTrue("3".equals(load.getStatus()), "只有异常状态的装车单才能恢复");
        load.setStatus("1");
        loadOrderMapper.updateById(load);
    }

    public void deleteById(Long id) {
        LoadOrder load = loadOrderMapper.selectById(id);
        Assert.notNull(load, "装车单不存在");
        Assert.isTrue("0".equals(load.getStatus()), "已发车的装车单不能删除");
        // 释放关联计划(1→0)
        List<LoadOrderDetail> details = queryDetailsByLoadOrderId(id);
        for (LoadOrderDetail detail : details) {
            ShipPlan plan = shipPlanMapper.selectById(detail.getShipPlanId());
            if (plan != null && "1".equals(plan.getStatus())) {
                plan.setStatus("0");
                shipPlanMapper.updateById(plan);
            }
        }
        loadOrderDetailMapper.delete(Wrappers.<LoadOrderDetail>lambdaQuery().eq(LoadOrderDetail::getLoadOrderId, id));
        loadOrderMapper.deleteById(id);
    }
}
