<template>
  <div class="app-container">
    <el-card>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="仓库">
          <el-select v-model="queryParams.warehouseId" placeholder="全部仓库" filterable clearable style="width: 180px">
            <el-option v-for="item in useWmsStore().warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.itemName" placeholder="商品名称" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <div class="mb8 flex-space-between">
        <div class="table-title">出入库月度趋势</div>
      </div>
      <div ref="trendRef" style="height: 320px; width: 100%"></div>
    </el-card>

    <el-card class="mt20">
      <div class="mb8 flex-space-between">
        <div class="table-title">出入库统计明细</div>
        <span class="tip">期初库存 = 期末库存 - 期间入库 + 期间出库 - 期间盘盈盘亏</span>
      </div>
      <el-table :data="list" border v-loading="loading" show-summary :summary-method="getSummary" empty-text="暂无数据">
        <el-table-column label="商品信息" min-width="180">
          <template #default="{ row }">
            <div>{{ row.itemName }}</div>
            <div class="sub">{{ row.itemCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="规格" min-width="150">
          <template #default="{ row }">
            <div>{{ row.skuName }}</div>
            <div class="sub">{{ row.skuCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="单位" prop="unit" width="70" align="center" />
        <el-table-column label="期初库存" prop="beginQuantity" width="100" align="right" />
        <el-table-column label="入库数量" prop="inQuantity" width="100" align="right" />
        <el-table-column label="出库数量" prop="outQuantity" width="100" align="right" />
        <el-table-column label="期末库存" prop="endQuantity" width="100" align="right" />
        <el-table-column label="盘盈盘亏" width="100" align="right">
          <template #default="{ row }">
            <span :class="Number(row.checkQuantity) > 0 ? 'profit' : (Number(row.checkQuantity) < 0 ? 'loss' : '')">{{ row.checkQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="入库金额" width="120" align="right">
          <template #default="{ row }">
            <span>¥{{ Number(row.inAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出库金额" width="120" align="right">
          <template #default="{ row }">
            <span>¥{{ Number(row.outAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="FlowReport">
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { listFlow, listFlowTrend } from '@/api/wms/report'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const list = ref([])
const loading = ref(false)
const dateRange = ref([])
const queryParams = ref({
  warehouseId: undefined,
  itemName: undefined
})
const trendRef = ref()
let chart = null

function buildParams() {
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0] + ' 00:00:00'
    params.endTime = dateRange.value[1] + ' 23:59:59'
  }
  return params
}

function getList() {
  loading.value = true
  listFlow(buildParams()).then(res => {
    list.value = res.data || []
    loading.value = false
  }).catch(() => { loading.value = false })
}

function getTrend() {
  listFlowTrend(buildParams()).then(res => {
    const data = res.data || []
    const months = data.map(d => d.month)
    const inQty = data.map(d => Number(d.inQuantity))
    const outQty = data.map(d => Number(d.outQuantity))
    renderTrend(months, inQty, outQty)
  })
}

function renderTrend(months, inQty, outQty) {
  nextTick(() => {
    if (!chart) {
      chart = echarts.init(trendRef.value)
    }
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['入库数量', '出库数量'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', name: '数量' },
      series: [
        { name: '入库数量', type: 'bar', data: inQty, itemStyle: { color: '#409eff' } },
        { name: '出库数量', type: 'bar', data: outQty, itemStyle: { color: '#f56c6c' } }
      ]
    })
  })
}

function handleQuery() {
  getList()
  getTrend()
}

function resetQuery() {
  queryParams.value = { warehouseId: undefined, itemName: undefined }
  dateRange.value = []
  handleQuery()
}

function getSummary({ columns }) {
  const sums = []
  columns.forEach((col, idx) => {
    if (idx === 0) {
      sums[idx] = '合计'
      return
    }
    const prop = col.property
    if (['beginQuantity', 'inQuantity', 'outQuantity', 'endQuantity', 'checkQuantity'].includes(prop)) {
      sums[idx] = list.value.reduce((acc, row) => acc + Number(row[prop] || 0), 0)
    } else {
      sums[idx] = ''
    }
  })
  return sums
}

onMounted(() => {
  getList()
  getTrend()
})

onBeforeUnmount(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.table-title { font-size: 16px; font-weight: bold; color: #303133; }
.tip { font-size: 12px; color: #909399; }
.sub { font-size: 12px; color: #909399; }
.profit { color: #67c23a; font-weight: bold; }
.loss { color: #f56c6c; font-weight: bold; }
</style>
