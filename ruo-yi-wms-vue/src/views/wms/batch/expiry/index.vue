<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="90px">
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="临近天数" prop="nearDays">
          <el-input-number v-model="queryParams.nearDays" :min="1" :max="365" controls-position="right" style="width: 130px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <el-alert :title="'有效期预警：未来 ' + queryParams.nearDays + ' 天内到期或已过期的批次'" type="warning" :closable="false" class="mb10" show-icon />
      <el-table v-loading="loading" :data="batchList" border empty-text="暂无即将到期的批次">
        <el-table-column label="批次号" prop="batchNo" width="150" />
        <el-table-column label="商品名称" prop="itemName" min-width="140" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="120" show-overflow-tooltip />
        <el-table-column label="批次数量" prop="quantity" width="90" align="right" />
        <el-table-column label="生产日期" prop="productionDate" width="110" />
        <el-table-column label="有效期至" prop="expiryDate" width="110" />
        <el-table-column label="剩余天数" width="100" align="center">
          <template #default="{ row }">
            <span :class="remainDaysClass(row)">{{ remainDaysText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '1' || isExpired(row)" type="danger">已过期</el-tag>
            <el-tag v-else type="warning">即将到期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="供应商" prop="supplier" min-width="130" show-overflow-tooltip />
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="Expiry">
import { listBatch } from '@/api/wms/batch'

const { proxy } = getCurrentInstance()
const batchList = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: undefined,
  nearDays: 30
})

function getList() {
  loading.value = true
  listBatch(queryParams.value).then(res => {
    // 过滤出临近过期或已过期的批次
    const nearDays = queryParams.value.nearDays || 30
    const now = new Date()
    const threshold = new Date(now.getTime() + nearDays * 24 * 60 * 60 * 1000)
    const all = res.rows || []
    const filtered = all.filter(r => {
      if (!r.expiryDate) return false
      const exp = new Date(r.expiryDate)
      return exp <= threshold
    })
    batchList.value = filtered
    total.value = filtered.length
    loading.value = false
  })
}

function isExpired(row) {
  if (!row.expiryDate) return false
  return new Date(row.expiryDate) < new Date()
}

function remainDays(row) {
  if (!row.expiryDate) return null
  const exp = new Date(row.expiryDate)
  const now = new Date()
  return Math.ceil((exp - now) / (24 * 60 * 60 * 1000))
}

function remainDaysText(row) {
  const d = remainDays(row)
  if (d == null) return '-'
  if (d < 0) return '已过期' + Math.abs(d) + '天'
  return d + '天'
}

function remainDaysClass(row) {
  const d = remainDays(row)
  if (d == null) return ''
  return d < 0 ? 'text-danger' : 'text-warning'
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); queryParams.value.nearDays = 30; handleQuery() }

onMounted(() => { getList() })
</script>

<style scoped>
.text-danger { color: #f56c6c; font-weight: bold; }
.text-warning { color: #e6a23c; font-weight: bold; }
</style>
