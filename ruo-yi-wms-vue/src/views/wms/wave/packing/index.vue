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
          <el-button type="success" icon="Check" @click="handleComplete" :disabled="!selectedWaveId">完成波次复核</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <el-alert title="复核打包：核对波次关联出库单的数量，确认无误后完成波次" type="warning" :closable="false" class="mb10" show-icon />
      <el-table v-loading="loading" :data="taskList" border empty-text="请先选择波次">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="出库单号" prop="shipmentOrderNo" width="150" />
        <el-table-column label="商品名称" prop="itemName" min-width="130" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="110" show-overflow-tooltip />
        <el-table-column label="库区" prop="zoneName" width="120" />
        <el-table-column label="库位" prop="locationCode" width="110" />
        <el-table-column label="拣货数量" prop="quantity" width="100" align="right" />
        <el-table-column label="复核状态" width="100" align="center">
          <template #default>
            <el-tag type="warning">待复核</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="Packing">
import { listWaveNoPage, listPickingTasks, completeWave } from '@/api/wms/wave'

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

function handleComplete() {
  if (!selectedWaveId.value) return
  proxy.$modal.confirm('确认波次拣货已完成，复核无误吗？').then(() => {
    return completeWave(selectedWaveId.value)
  }).then(() => {
    proxy.$modal.msgSuccess('波次已完成复核打包')
    loadWaveOptions()
  }).catch(() => {})
}

onMounted(() => { loadWaveOptions() })
</script>
