<template>
  <div class="app-container">
    <el-card>
      <el-form :inline="true">
        <el-form-item label="选择波次">
          <el-select v-model="selectedWaveId" placeholder="请选择波次" filterable style="width: 260px" @change="handleWaveChange">
            <el-option v-for="item in waveOptions" :key="item.id" :label="item.waveNo + ' - ' + (item.warehouseName || '')" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Refresh" @click="loadTasks">刷新任务</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <el-alert title="拣货任务：按库位排序，优化拣货路径" type="info" :closable="false" class="mb10" show-icon />
      <el-table v-loading="loading" :data="taskList" border empty-text="请先选择波次">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="出库单号" prop="shipmentOrderNo" width="150" />
        <el-table-column label="商品名称" prop="itemName" min-width="130" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="110" show-overflow-tooltip />
        <el-table-column label="库区" prop="zoneName" width="120" />
        <el-table-column label="库位" prop="locationCode" width="110" />
        <el-table-column label="拣货数量" prop="quantity" width="100" align="right" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning">待拣</el-tag>
            <el-tag v-else type="success">已拣</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="Picking">
import { listWaveNoPage, listPickingTasks } from '@/api/wms/wave'

const { proxy } = getCurrentInstance()
const waveOptions = ref([])
const selectedWaveId = ref(null)
const taskList = ref([])
const loading = ref(false)

function loadWaveOptions() {
  listWaveNoPage({}).then(res => {
    waveOptions.value = res.data || []
    if (waveOptions.value.length > 0 && !selectedWaveId.value) {
      selectedWaveId.value = waveOptions.value[0].id
      loadTasks()
    }
  })
}

function loadTasks() {
  if (!selectedWaveId.value) {
    taskList.value = []
    return
  }
  loading.value = true
  listPickingTasks(selectedWaveId.value).then(res => {
    taskList.value = res.data || []
    loading.value = false
  })
}

function handleWaveChange() {
  loadTasks()
}

onMounted(() => { loadWaveOptions() })
</script>
