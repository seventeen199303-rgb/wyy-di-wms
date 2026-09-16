<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="波次号" prop="waveNo">
          <el-input v-model="queryParams.waveNo" placeholder="请输入波次号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" filterable clearable style="width: 160px">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待拣货" value="0" />
            <el-option label="拣货中" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已取消" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <el-row :gutter="10" class="mb8" type="flex" justify="space-between">
        <el-col :span="6"><span style="font-size: large">波次列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['wms:wave:edit']">新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="waveList" border empty-text="暂无波次">
        <el-table-column label="波次号" prop="waveNo" width="160" />
        <el-table-column label="仓库" prop="warehouseName" width="140" />
        <el-table-column label="出库单数" prop="shipmentCount" width="90" align="center" />
        <el-table-column label="总数量" prop="totalQuantity" width="100" align="right" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="info">待拣货</el-tag>
            <el-tag v-else-if="row.status === '1'" type="warning">拣货中</el-tag>
            <el-tag v-else-if="row.status === '2'" type="success">已完成</el-tag>
            <el-tag v-else type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联出库单" prop="shipmentOrderNos" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="no in (row.shipmentOrderNos || [])" :key="no" size="small" class="mr5">{{ no }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="100" show-overflow-tooltip />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="260">
          <template #default="scope">
            <el-button v-if="scope.row.status === '0'" link type="primary" icon="VideoPlay" @click="handleStart(scope.row)" v-hasPermi="['wms:wave:edit']">开始拣货</el-button>
            <el-button v-if="scope.row.status === '0' || scope.row.status === '1'" link type="success" icon="Check" @click="handleComplete(scope.row)" v-hasPermi="['wms:wave:edit']">完成</el-button>
            <el-button v-if="scope.row.status !== '2'" link type="warning" icon="Close" @click="handleCancel(scope.row)" v-hasPermi="['wms:wave:edit']">取消</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wms:wave:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wms:wave:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body :close-on-click-modal="false">
      <el-form ref="waveRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="波次号" prop="waveNo">
          <el-input v-model="form.waveNo" placeholder="请输入波次号" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="handleWarehouseChange">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联出库单" prop="shipmentOrderIds">
          <el-select v-model="form.shipmentOrderIds" multiple filterable placeholder="请选择出库单" style="width: 100%">
            <el-option v-for="item in shipmentOptions" :key="item.id" :label="item.orderNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Wave">
import { listWave, getWave, delWave, addWave, updateWave, startWave, completeWave, cancelWave } from '@/api/wms/wave'
import { listShipmentOrder } from '@/api/wms/shipmentOrder'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const wmsStore = useWmsStore()
const warehouseList = computed(() => wmsStore.warehouseList)
const shipmentOptions = ref([])

const waveList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, waveNo: undefined, warehouseId: undefined, status: undefined },
  rules: {
    waveNo: [{ required: true, message: '波次号不能为空', trigger: 'blur' }],
    warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listWave(queryParams.value).then(res => {
    waveList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadShipmentOptions() {
  listShipmentOrder({ pageNum: 1, pageSize: 999, orderStatus: 0 }).then(res => {
    shipmentOptions.value = res.rows || []
  })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { id: null, waveNo: null, warehouseId: null, shipmentOrderIds: [], remark: null }
  proxy.resetForm('waveRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleWarehouseChange() { form.value.shipmentOrderIds = []; loadShipmentOptions() }
function handleAdd() { reset(); open.value = true; title.value = '添加波次'; loadShipmentOptions() }
function handleUpdate(row) {
  reset()
  getWave(row.id).then(res => {
    form.value = res.data
    form.value.shipmentOrderIds = (res.data.shipmentOrderNos || []).map(no => {
      const opt = shipmentOptions.value.find(o => o.orderNo === no)
      return opt ? opt.id : null
    }).filter(Boolean)
    open.value = true
    title.value = '修改波次'
    loadShipmentOptions()
  })
}
function submitForm() {
  proxy.$refs['waveRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateWave(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      } else {
        addWave(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}
function handleStart(row) {
  proxy.$modal.confirm('确认开始拣货波次【' + row.waveNo + '】吗？').then(() => {
    return startWave(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('已开始拣货') }).catch(() => {})
}
function handleComplete(row) {
  proxy.$modal.confirm('确认完成波次【' + row.waveNo + '】吗？').then(() => {
    return completeWave(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('波次已完成') }).catch(() => {})
}
function handleCancel(row) {
  proxy.$modal.confirm('确认取消波次【' + row.waveNo + '】吗？').then(() => {
    return cancelWave(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('波次已取消') }).catch(() => {})
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除波次【' + row.waveNo + '】吗？').then(function () {
    return delWave(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

onMounted(() => { getList(); if (warehouseList.value.length === 0) wmsStore.getWarehouseList() })
</script>
