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
        <div class="table-title">盘点差异明细</div>
        <span class="tip">盘盈盘亏 = 实盘数量 - 账面数量（正数盘盈，负数盘亏）</span>
      </div>
      <el-table :data="list" border v-loading="loading" empty-text="暂无差异数据">
        <el-table-column label="盘点单号" prop="orderNo" width="150" />
        <el-table-column label="盘点时间" prop="createTime" width="160" />
        <el-table-column label="仓库" prop="warehouseName" width="120" />
        <el-table-column label="商品信息" min-width="170">
          <template #default="{ row }">
            <div>{{ row.itemName }}</div>
            <div class="sub">{{ row.itemCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="规格" min-width="140">
          <template #default="{ row }">
            <div>{{ row.skuName }}</div>
            <div class="sub">{{ row.skuCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="库区" prop="zoneName" width="120" />
        <el-table-column label="库位" prop="locationCode" width="100" />
        <el-table-column label="账面数量" prop="quantity" width="100" align="right" />
        <el-table-column label="实盘数量" prop="checkQuantity" width="100" align="right" />
        <el-table-column label="盘盈盘亏" width="110" align="right">
          <template #default="{ row }">
            <span :class="Number(row.diffQuantity) > 0 ? 'profit' : 'loss'">{{ row.diffQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="差异金额(成本)" width="130" align="right">
          <template #default="{ row }">
            <span>¥{{ Number(row.diffAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="CheckReport">
import { ref, onMounted } from 'vue'
import { listCheckDiff } from '@/api/wms/report'
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
  listCheckDiff(params).then(res => {
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.table-title { font-size: 16px; font-weight: bold; color: #303133; }
.tip { font-size: 12px; color: #909399; }
.sub { font-size: 12px; color: #909399; }
.profit { color: #67c23a; font-weight: bold; }
.loss { color: #f56c6c; font-weight: bold; }
</style>
