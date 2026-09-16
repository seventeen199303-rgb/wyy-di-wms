<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="装车单号" prop="loadNo">
          <el-input v-model="queryParams.loadNo" placeholder="请输入装车单号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="车牌号" prop="vehicleNo">
          <el-input v-model="queryParams.vehicleNo" placeholder="请输入车牌号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待发车" value="0" />
            <el-option label="在途" value="1" />
            <el-option label="已签收" value="2" />
            <el-option label="异常" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <div class="mb8 flex-space-between">
        <span style="font-size: large">配送跟踪</span>
        <el-button type="primary" plain icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="loadList" border empty-text="暂无配送记录">
        <el-table-column label="装车单号" prop="loadNo" width="150" />
        <el-table-column label="车牌号" prop="vehicleNo" width="120" />
        <el-table-column label="司机" width="110">
          <template #default="{ row }">
            <div>{{ row.driverName }}</div>
            <div class="sub">{{ row.driverPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="计划单数" prop="totalPlan" width="90" align="center" />
        <el-table-column label="总数量" prop="totalQuantity" width="90" align="right" />
        <el-table-column label="配送状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning">待发车</el-tag>
            <el-tag v-else-if="row.status === '1'" type="primary">在途</el-tag>
            <el-tag v-else-if="row.status === '2'" type="success">已签收</el-tag>
            <el-tag v-else type="danger">异常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发车时间" prop="departTime" width="160" align="center" />
        <el-table-column label="签收人" prop="signName" width="100" align="center" />
        <el-table-column label="签收时间" prop="signTime" width="160" align="center" />
        <el-table-column label="配送计划" prop="shipPlanNos" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="no in (row.shipPlanNos || [])" :key="no" size="small" class="mr5">{{ no }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-button v-if="scope.row.status === '1'" link type="success" icon="Check" @click="handleSign(scope.row)" v-hasPermi="['les:delivery:edit']">签收</el-button>
            <el-button v-if="scope.row.status === '1'" link type="danger" icon="Warning" @click="handleAbnormal(scope.row)" v-hasPermi="['les:delivery:edit']">标记异常</el-button>
            <el-button v-if="scope.row.status === '3'" link type="primary" icon="RefreshLeft" @click="handleRecover(scope.row)" v-hasPermi="['les:delivery:edit']">恢复在途</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="Delivery">
import { listLoadOrder, signLoadOrder, abnormalLoadOrder, recoverLoadOrder } from '@/api/wms/les'

const { proxy } = getCurrentInstance()
const loadList = ref([])
const loading = ref(true)
const total = ref(0)

const queryParams = ref({ pageNum: 1, pageSize: 10, loadNo: undefined, vehicleNo: undefined, status: undefined })

function getList() {
  loading.value = true
  listLoadOrder(queryParams.value).then(res => {
    loadList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }

function handleSign(row) {
  proxy.$prompt('请输入签收人姓名', '签收确认', {
    confirmButtonText: '确认签收',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    if (!value) { proxy.$modal.msgWarning('签收人不能为空'); return }
    return signLoadOrder(row.id, value)
  }).then(() => { getList(); proxy.$modal.msgSuccess('已签收') }).catch(() => {})
}

function handleAbnormal(row) {
  proxy.$prompt('请输入异常原因', '标记异常', {
    confirmButtonText: '确认标记',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    return abnormalLoadOrder(row.id, value || '配送异常')
  }).then(() => { getList(); proxy.$modal.msgSuccess('已标记异常') }).catch(() => {})
}

function handleRecover(row) {
  proxy.$modal.confirm('确认将装车单【' + row.loadNo + '】恢复为在途状态吗？').then(() => {
    return recoverLoadOrder(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('已恢复在途') }).catch(() => {})
}

onMounted(() => { getList() })
</script>

<style scoped>
.sub { font-size: 12px; color: #909399; }
</style>
