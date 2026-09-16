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
import com.ruoyi.wms.domain.bo.BatchBo;
import com.ruoyi.wms.domain.entity.Batch;
import com.ruoyi.wms.domain.entity.ItemSku;
import com.ruoyi.wms.domain.entity.Item;
import com.ruoyi.wms.domain.vo.BatchVo;
import com.ruoyi.wms.mapper.BatchMapper;
import com.ruoyi.wms.mapper.ItemSkuMapper;
import com.ruoyi.wms.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 批次Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class BatchService extends ServiceImpl<BatchMapper, Batch> {

    private final BatchMapper batchMapper;
    private final ItemSkuMapper itemSkuMapper;
    private final ItemMapper itemMapper;

    /**
     * 查询批次
     */
    public BatchVo queryById(Long id) {
        BatchVo vo = batchMapper.selectVoById(id);
        fillSkuAndItem(List.of(vo));
        return vo;
    }

    /**
     * 查询批次列表
     */
    public TableDataInfo<BatchVo> queryPageList(BatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Batch> lqw = buildQueryWrapper(bo);
        Page<BatchVo> result = batchMapper.selectVoPage(pageQuery.build(), lqw);
        fillSkuAndItem(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询批次列表(不分页)
     */
    public List<BatchVo> queryList(BatchBo bo) {
        LambdaQueryWrapper<Batch> lqw = buildQueryWrapper(bo);
        List<BatchVo> list = batchMapper.selectVoList(lqw);
        fillSkuAndItem(list);
        return list;
    }

    private LambdaQueryWrapper<Batch> buildQueryWrapper(BatchBo bo) {
        LambdaQueryWrapper<Batch> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getBatchNo()), Batch::getBatchNo, bo.getBatchNo());
        lqw.eq(bo.getSkuId() != null, Batch::getSkuId, bo.getSkuId());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), Batch::getStatus, bo.getStatus());
        lqw.ge(bo.getParams().get("expiryDateStart") != null, Batch::getExpiryDate, bo.getParams().get("expiryDateStart"));
        lqw.le(bo.getParams().get("expiryDateEnd") != null, Batch::getExpiryDate, bo.getParams().get("expiryDateEnd"));
        lqw.orderByDesc(Batch::getExpiryDate);
        return lqw;
    }

    private void fillSkuAndItem(List<BatchVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<Long, ItemSku> skuMap = itemSkuMapper.selectList().stream().collect(Collectors.toMap(ItemSku::getId, s -> s, (a, b) -> a));
        Map<Long, Item> itemMap = itemMapper.selectList().stream().collect(Collectors.toMap(Item::getId, i -> i, (a, b) -> a));
        list.forEach(vo -> {
            ItemSku sku = skuMap.get(vo.getSkuId());
            if (sku != null) {
                vo.setSkuName(sku.getSkuName());
                Item item = itemMap.get(sku.getItemId());
                if (item != null) {
                    vo.setItemName(item.getItemName());
                    vo.setUnit(item.getUnit());
                }
            }
        });
    }

    /**
     * 新增批次
     */
    public void insertByBo(BatchBo bo) {
        validateBatchNo(bo);
        Batch add = MapstructUtils.convert(bo, Batch.class);
        if (add.getStatus() == null) {
            add.setStatus("0");
        }
        batchMapper.insert(add);
    }

    /**
     * 修改批次
     */
    public void updateByBo(BatchBo bo) {
        validateBatchNo(bo);
        Batch update = MapstructUtils.convert(bo, Batch.class);
        batchMapper.updateById(update);
    }

    private void validateBatchNo(BatchBo bo) {
        LambdaQueryWrapper<Batch> lqw = Wrappers.lambdaQuery();
        lqw.eq(Batch::getBatchNo, bo.getBatchNo());
        List<Batch> list = batchMapper.selectList(lqw);
        boolean repeat = list.stream().anyMatch(it -> Objects.equals(it.getBatchNo(), bo.getBatchNo()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(repeat, "批次号重复");
    }

    /**
     * 删除批次
     */
    public void deleteById(Long id) {
        batchMapper.deleteById(id);
    }

    public void deleteByIds(Collection<Long> ids) {
        batchMapper.deleteBatchIds(ids);
    }

}
