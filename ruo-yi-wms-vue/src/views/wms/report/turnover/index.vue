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
        <div class="table-title">库存周转分析</div>
        <span class="tip">周转率 = 期间出库量 ÷ 当前库存；周转天数 = 当前库存 ÷ 日均出库</span>
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
        <el-table-column label="当前库存" prop="stockQuantity" width="100" align="right" />
        <el-table-column label="期间入库" prop="inQuantity" width="100" align="right" />
        <el-table-column label="期间出库" prop="outQuantity" width="100" align="right" />
        <el-table-column label="日均出库" prop="avgDailyOut" width="90" align="right" />
        <el-table-column label="周转率" width="100" align="right">
          <template #default="{ row }">
            <el-tag v-if="Number(row.turnoverRate) >= 1" type="success">{{ row.turnoverRate }}</el-tag>
            <el-tag v-else-if="Number(row.turnoverRate) > 0" type="warning">{{ row.turnoverRate }}</el-tag>
            <el-tag v-else type="info">0</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="周转天数" prop="turnoverDays" width="100" align="right">
          <template #default="{ row }">
            <span>{{ row.turnoverDays }} 天</span>
          </template>
        </el-table-column>
        <el-table-column label="库存金额(成本)" prop="stockAmount" width="130" align="right">
          <template #default="{ row }">
            <span>¥{{ Number(row.stockAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="Turnover">
import { ref, onMounted } from 'vue'
import { listTurnover } from '@/api/wms/report'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const list = ref([])
const loading = ref(false)
const dateRange = ref([])
const queryParams = ref({
  warehouseId: undefined,
  itemName: undefined
})

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0] + ' 00:00:00'
    params.endTime = dateRange.value[1] + ' 23:59:59'
  }
  listTurnover(params).then(res => {
    list.value = res.data || []
    loading.value = false
  }).catch(() => { loading.value = false })
}

function handleQuery() {
  getList()
}

function resetQuery() {
  queryParams.value = { warehouseId: undefined, itemName: undefined }
  dateRange.value = []
  getList()
}

function getSummary({ columns }) {
  const sums = []
  columns.forEach((col, idx) => {
    if (idx === 0) {
      sums[idx] = '合计'
      return
    }
    const prop = col.property
    if (prop === 'stockQuantity' || prop === 'inQuantity' || prop === 'outQuantity') {
      sums[idx] = list.value.reduce((acc, row) => acc + Number(row[prop] || 0), 0)
    } else if (prop === 'stockAmount') {
      const total = list.value.reduce((acc, row) => acc + Number(row.stockAmount || 0), 0)
      sums[idx] = '¥' + total.toFixed(2)
    } else {
      sums[idx] = ''
    }
  })
  return sums
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.table-title { font-size: 16px; font-weight: bold; color: #303133; }
.tip { font-size: 12px; color: #909399; }
.sub { font-size: 12px; color: #909399; }
</style>
