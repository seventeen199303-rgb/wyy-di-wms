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
import com.ruoyi.wms.domain.bo.SerialNumberBo;
import com.ruoyi.wms.domain.entity.SerialNumber;
import com.ruoyi.wms.domain.entity.ItemSku;
import com.ruoyi.wms.domain.entity.Item;
import com.ruoyi.wms.domain.vo.SerialNumberVo;
import com.ruoyi.wms.mapper.SerialNumberMapper;
import com.ruoyi.wms.mapper.ItemSkuMapper;
import com.ruoyi.wms.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 序列号Service业务层处理
 *
 * @author wangyouyong
 * @date 2026-09-05
 */
@RequiredArgsConstructor
@Service
public class SerialNumberService extends ServiceImpl<SerialNumberMapper, SerialNumber> {

    private final SerialNumberMapper serialMapper;
    private final ItemSkuMapper itemSkuMapper;
    private final ItemMapper itemMapper;

    /**
     * 查询序列号
     */
    public SerialNumberVo queryById(Long id) {
        SerialNumberVo vo = serialMapper.selectVoById(id);
        fillSkuAndItem(java.util.List.of(vo));
        return vo;
    }

    /**
     * 查询序列号列表
     */
    public TableDataInfo<SerialNumberVo> queryPageList(SerialNumberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SerialNumber> lqw = buildQueryWrapper(bo);
        Page<SerialNumberVo> result = serialMapper.selectVoPage(pageQuery.build(), lqw);
        fillSkuAndItem(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询序列号列表(不分页)
     */
    public List<SerialNumberVo> queryList(SerialNumberBo bo) {
        LambdaQueryWrapper<SerialNumber> lqw = buildQueryWrapper(bo);
        List<SerialNumberVo> list = serialMapper.selectVoList(lqw);
        fillSkuAndItem(list);
        return list;
    }

    private LambdaQueryWrapper<SerialNumber> buildQueryWrapper(SerialNumberBo bo) {
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getSerialNo()), SerialNumber::getSerialNo, bo.getSerialNo());
        lqw.like(StrUtil.isNotBlank(bo.getBatchNo()), SerialNumber::getBatchNo, bo.getBatchNo());
        lqw.eq(bo.getBatchId() != null, SerialNumber::getBatchId, bo.getBatchId());
        lqw.eq(bo.getSkuId() != null, SerialNumber::getSkuId, bo.getSkuId());
        lqw.eq(StrUtil.isNotBlank(bo.getStatus()), SerialNumber::getStatus, bo.getStatus());
        lqw.orderByDesc(SerialNumber::getId);
        return lqw;
    }

    private void fillSkuAndItem(List<SerialNumberVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<Long, ItemSku> skuMap = itemSkuMapper.selectList().stream().collect(Collectors.toMap(ItemSku::getId, s -> s, (a, b) -> a));
        Map<Long, Item> itemMap = itemMapper.selectList().stream().collect(Collectors.toMap(Item::getId, i -> i, (a, b) -> a));
        list.forEach(vo -> {
            if (vo.getSkuId() != null) {
                ItemSku sku = skuMap.get(vo.getSkuId());
                if (sku != null) {
                    vo.setSkuName(sku.getSkuName());
                    Item item = itemMap.get(sku.getItemId());
                    if (item != null) {
                        vo.setItemName(item.getItemName());
                    }
                }
            }
        });
    }

    /**
     * 新增序列号
     */
    public void insertByBo(SerialNumberBo bo) {
        validateSerialNo(bo);
        SerialNumber add = MapstructUtils.convert(bo, SerialNumber.class);
        if (add.getStatus() == null) {
            add.setStatus("0");
        }
        serialMapper.insert(add);
    }

    /**
     * 修改序列号
     */
    public void updateByBo(SerialNumberBo bo) {
        validateSerialNo(bo);
        SerialNumber update = MapstructUtils.convert(bo, SerialNumber.class);
        serialMapper.updateById(update);
    }

    private void validateSerialNo(SerialNumberBo bo) {
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.eq(SerialNumber::getSerialNo, bo.getSerialNo());
        List<SerialNumber> list = serialMapper.selectList(lqw);
        boolean repeat = list.stream().anyMatch(it -> Objects.equals(it.getSerialNo(), bo.getSerialNo()) && !Objects.equals(it.getId(), bo.getId()));
        Assert.isFalse(repeat, "序列号重复");
    }

    /**
     * 删除序列号
     */
    public void deleteById(Long id) {
        serialMapper.deleteById(id);
    }

    public void deleteByIds(Collection<Long> ids) {
        serialMapper.deleteBatchIds(ids);
    }

}
