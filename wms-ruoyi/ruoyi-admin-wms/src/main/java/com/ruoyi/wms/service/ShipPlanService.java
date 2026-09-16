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
import com.ruoyi.wms.domain.bo.ShipPlanBo;
import com.ruoyi.wms.domain.entity.Merchant;
import com.ruoyi.wms.domain.entity.ShipPlan;
import com.ruoyi.wms.domain.entity.ShipPlanDetail;
import com.ruoyi.wms.domain.entity.ShipmentOrder;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.vo.ShipPlanVo;
import com.ruoyi.wms.mapper.MerchantMapper;
import com.ruoyi.wms.mapper.ShipPlanDetailMapper;
import com.ruoyi.wms.mapper.ShipPlanMapper;
import com.ruoyi.wms.mapper.ShipmentOrderMapper;
import com.ruoyi.wms.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货计划Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class ShipPlanService extends ServiceImpl<ShipPlanMapper, ShipPlan> {

    private final ShipPlanMapper shipPlanMapper;
    private final ShipPlanDetailMapper shipPlanDetailMapper;
    private final ShipmentOrderMapper shipmentOrderMapper;
    private final MerchantMapper merchantMapper;
    private final WarehouseMapper warehouseMapper;

    public ShipPlanVo queryById(Long id) {
        ShipPlanVo vo = shipPlanMapper.selectVoById(id);
        Assert.notNull(vo, "发货计划不存在");
        fillNames(List.of(vo));
        return vo;
    }

    public TableDataInfo<ShipPlanVo> queryPageList(ShipPlanBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShipPlan> lqw = buildQueryWrapper(bo);
        Page<ShipPlanVo> result = shipPlanMapper.selectVoPage(pageQuery.build(), lqw);
        fillNames(result.getRecords());
        return TableDataInfo.build(result);
    }

    public List<ShipPlanVo> queryList(ShipPlanBo bo) {
        LambdaQueryWrapper<ShipPlan> lqw = buildQueryWrapper(bo);
        List<ShipPlanVo> list = shipPlanMapper.selectVoList(lqw);
        fillNames(list);
        return list;
    }

    private LambdaQueryWrapper<ShipPlan> buildQueryWrapper(ShipPlanBo bo) {
        LambdaQueryWrapper<ShipPlan> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getPlanNo()), ShipPlan::getPlanNo, bo.getPlanNo());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), ShipPlan::getStatus, bo.getStatus());
        lqw.eq(bo.getCustomerId() != null, ShipPlan::getCustomerId, bo.getCustomerId());
        lqw.eq(bo.getWarehouseId() != null, ShipPlan::getWarehouseId, bo.getWarehouseId());
        lqw.orderByDesc(ShipPlan::getId);
        return lqw;
    }

    /** 填充客户名称/仓库名称/出库单号列表 */
    private void fillNames(List<ShipPlanVo> list) {
        if (CollUtil.isEmpty(list)) return;
        Map<Long, String> merchantMap = merchantMapper.selectList().stream()
            .collect(Collectors.toMap(Merchant::getId, Merchant::getMerchantName, (a, b) -> a));
        Map<Long, String> warehouseMap = warehouseMapper.selectList().stream()
            .collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName, (a, b) -> a));
        for (ShipPlanVo vo : list) {
            vo.setCustomerName(merchantMap.get(vo.getCustomerId()));
            vo.setWarehouseName(warehouseMap.get(vo.getWarehouseId()));
            List<ShipPlanDetail> details = queryDetailsByPlanId(vo.getId());
            vo.setShipmentOrderNos(details.stream().map(ShipPlanDetail::getShipmentOrderNo).filter(Objects::nonNull).collect(Collectors.toList()));
            vo.setShipmentCount(details.size());
        }
    }

    public List<ShipPlanDetail> queryDetailsByPlanId(Long planId) {
        return shipPlanDetailMapper.selectList(Wrappers.<ShipPlanDetail>lambdaQuery().eq(ShipPlanDetail::getPlanId, planId));
    }

    /**
     * 新增发货计划（关联出库单）
     * 业务规则:
     * 1. 只能关联"销售出库单"(opt_type=2)且已出库(order_status=1)
     * 2. 同一出库单不能重复加入任何发货计划
     * 3. 同一计划内所有出库单必须是同一客户
     */
    @Transactional
    public void insertByBo(ShipPlanBo bo) {
        validatePlanNo(bo);
        List<ShipmentOrder> orders = validateAndGetOrders(bo.getShipmentOrderIds(), null);
        // 校验客户一致
        Long customerId = bo.getCustomerId();
        Assert.notNull(customerId, "请选择客户");
        for (ShipmentOrder order : orders) {
            Assert.isTrue(Objects.equals(customerId, order.getMerchantId()), "出库单【" + order.getOrderNo() + "】客户与计划客户不一致");
        }
        ShipPlan add = MapstructUtils.convert(bo, ShipPlan.class);
        if (StrUtil.isBlank(add.getStatus())) add.setStatus("0");
        calculateTotals(add, orders);
        shipPlanMapper.insert(add);
        saveDetails(add.getId(), orders);
    }

    @Transactional
    public void updateByBo(ShipPlanBo bo) {
        validatePlanNo(bo);
        List<ShipmentOrder> orders = validateAndGetOrders(bo.getShipmentOrderIds(), bo.getId());
        Long customerId = bo.getCustomerId();
        Assert.notNull(customerId, "请选择客户");
        for (ShipmentOrder order : orders) {
            Assert.isTrue(Objects.equals(customerId, order.getMerchantId()), "出库单【" + order.getOrderNo() + "】客户与计划客户不一致");
        }
        ShipPlan update = MapstructUtils.convert(bo, ShipPlan.class);
        calculateTotals(update, orders);
        shipPlanMapper.updateById(update);
        // 重建明细
        shipPlanDetailMapper.delete(Wrappers.<ShipPlanDetail>lambdaQuery().eq(ShipPlanDetail::getPlanId, bo.getId()));
        saveDetails(bo.getId(), orders);
    }

    /**
     * 校验并获取出库单
     */
    private List<ShipmentOrder> validateAndGetOrders(List<Long> shipmentOrderIds, Long excludePlanId) {
        Assert.isFalse(CollUtil.isEmpty(shipmentOrderIds), "请选择要发货的出库单");
        List<ShipmentOrder> orders = shipmentOrderMapper.selectBatchIds(shipmentOrderIds);
        Assert.isTrue(orders.size() == shipmentOrderIds.size(), "存在无效的出库单");
        for (ShipmentOrder order : orders) {
            // 只允许销售出库单
            Assert.isTrue(Objects.equals(2L, order.getOptType()), "出库单【" + order.getOrderNo() + "】不是销售出库单，不能加入发货计划");
            // 必须已出库
            Assert.isTrue(Objects.equals(1, order.getOrderStatus()), "出库单【" + order.getOrderNo() + "】尚未出库，不能加入发货计划");
        }
        // 校验出库单未被其他计划占用
        for (ShipmentOrder order : orders) {
            LambdaQueryWrapper<ShipPlanDetail> lqw = Wrappers.lambdaQuery();
            lqw.eq(ShipPlanDetail::getShipmentOrderId, order.getId());
            if (excludePlanId != null) {
                lqw.ne(ShipPlanDetail::getPlanId, excludePlanId);
            }
            Long count = shipPlanDetailMapper.selectCount(lqw);
            Assert.isTrue(count == 0, "出库单【" + order.getOrderNo() + "】已加入其他发货计划");
        }
        return orders;
    }

    private void calculateTotals(ShipPlan plan, List<ShipmentOrder> orders) {
        BigDecimal totalQty = orders.stream().map(ShipmentOrder::getTotalQuantity).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = orders.stream().map(ShipmentOrder::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        plan.setTotalQuantity(totalQty);
        plan.setTotalAmount(totalAmount);
        plan.setTotalSku(orders.size());
    }

    private void saveDetails(Long planId, List<ShipmentOrder> orders) {
        for (ShipmentOrder order : orders) {
            ShipPlanDetail detail = new ShipPlanDetail();
            detail.setPlanId(planId);
            detail.setShipmentOrderId(order.getId());
            detail.setShipmentOrderNo(order.getOrderNo());
            shipPlanDetailMapper.insert(detail);
        }
    }

    private void validatePlanNo(ShipPlanBo bo) {
        LambdaQueryWrapper<ShipPlan> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShipPlan::getPlanNo, bo.getPlanNo());
        List<ShipPlan> list = shipPlanMapper.selectList(lqw);
        boolean repeat = list.stream().anyMatch(it -> Objects.equals(it.getPlanNo(), bo.getPlanNo()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(repeat, "计划单号重复");
    }

    /**
     * 取消发货计划
     */
    public void cancel(Long id) {
        ShipPlan plan = shipPlanMapper.selectById(id);
        Assert.notNull(plan, "发货计划不存在");
        Assert.isTrue("0".equals(plan.getStatus()), "只有待装车的计划才能取消");
        plan.setStatus("2");
        shipPlanMapper.updateById(plan);
    }

    public void deleteById(Long id) {
        ShipPlan plan = shipPlanMapper.selectById(id);
        Assert.notNull(plan, "发货计划不存在");
        Assert.isTrue("0".equals(plan.getStatus()), "已装车或已取消的计划不能删除");
        shipPlanDetailMapper.delete(Wrappers.<ShipPlanDetail>lambdaQuery().eq(ShipPlanDetail::getPlanId, id));
        shipPlanMapper.deleteById(id);
    }
}
