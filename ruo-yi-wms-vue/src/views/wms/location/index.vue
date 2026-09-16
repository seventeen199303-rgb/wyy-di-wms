<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="库位编码" prop="locationCode">
          <el-input v-model="queryParams.locationCode" placeholder="请输入库位编码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" filterable clearable style="width: 180px" @change="handleWarehouseChange">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属库区" prop="zoneId">
          <el-select v-model="queryParams.zoneId" placeholder="请选择库区" filterable clearable style="width: 180px">
            <el-option v-for="item in zoneOptions" :key="item.id" :label="item.zoneName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库位状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option v-for="dict in wms_location_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
        <el-col :span="6"><span style="font-size: large">库位列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['wms:location:edit']">新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="locationList" border empty-text="暂无库位">
        <el-table-column label="库位编码" prop="locationCode" width="130" />
        <el-table-column label="库位名称" prop="locationName" min-width="120" />
        <el-table-column label="所属仓库" prop="warehouseName" width="130" />
        <el-table-column label="所属库区" prop="zoneName" width="140" />
        <el-table-column label="库位类型" prop="locationType" width="100">
          <template #default="scope">
            <dict-tag :options="wms_location_type" :value="scope.row.locationType" />
          </template>
        </el-table-column>
        <el-table-column label="承重(kg)" prop="maxWeight" width="90" align="right" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            <dict-tag :options="wms_location_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="140">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wms:location:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wms:location:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <!-- 添加或修改库位对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body :close-on-click-modal="false">
      <el-form ref="locationRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库位编码" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="请输入库位编码" />
        </el-form-item>
        <el-form-item label="库位名称" prop="locationName">
          <el-input v-model="form.locationName" placeholder="请输入库位名称" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="handleFormWarehouseChange">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属库区" prop="zoneId">
          <el-select v-model="form.zoneId" placeholder="请选择库区" filterable style="width: 100%">
            <el-option v-for="item in formZoneOptions" :key="item.id" :label="item.zoneName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库位类型" prop="locationType">
          <el-select v-model="form.locationType" placeholder="请选择库位类型" style="width: 100%">
            <el-option v-for="dict in wms_location_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
          </el-select>
        </el-form-item>
        <el-form-item label="承重(kg)" prop="maxWeight">
          <el-input-number v-model="form.maxWeight" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in wms_location_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
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

<script setup name="Location">
import { listLocation, getLocation, delLocation, addLocation, updateLocation } from '@/api/wms/location'
import { listZoneNoPage } from '@/api/wms/zone'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const { wms_location_type, wms_location_status } = proxy.useDict('wms_location_type', 'wms_location_status')
const wmsStore = useWmsStore()
const warehouseList = computed(() => wmsStore.warehouseList)

const locationList = ref([])
const allZones = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')

// 查询条件里的库区选项（按所选仓库过滤）
const zoneOptions = computed(() => {
  if (!queryParams.value.warehouseId) return allZones.value
  return allZones.value.filter(z => z.warehouseId === queryParams.value.warehouseId)
})
// 表单里的库区选项
const formZoneOptions = computed(() => {
  if (!form.value.warehouseId) return allZones.value
  return allZones.value.filter(z => z.warehouseId === form.value.warehouseId)
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    locationCode: undefined,
    locationName: undefined,
    warehouseId: undefined,
    zoneId: undefined,
    locationType: undefined,
    status: undefined
  },
  rules: {
    locationCode: [{ required: true, message: '库位编码不能为空', trigger: 'blur' }],
    warehouseId: [{ required: true, message: '所属仓库不能为空', trigger: 'change' }],
    zoneId: [{ required: true, message: '所属库区不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listLocation(queryParams.value).then(response => {
    locationList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function getZones() {
  listZoneNoPage({}).then(res => {
    allZones.value = res.data || []
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    locationCode: null,
    locationName: null,
    zoneId: null,
    warehouseId: null,
    locationType: 1,
    maxWeight: null,
    status: '0',
    remark: null
  }
  proxy.resetForm('locationRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value.zoneId = undefined
  handleQuery()
}

function handleWarehouseChange() {
  queryParams.value.zoneId = undefined
}

function handleFormWarehouseChange() {
  form.value.zoneId = null
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加库位'
}

function handleUpdate(row) {
  reset()
  getLocation(row.id).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改库位'
  })
}

function submitForm() {
  proxy.$refs['locationRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateLocation(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        }).finally(() => { buttonLoading.value = false })
      } else {
        addLocation(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除库位【' + row.locationCode + '】吗？').then(function () {
    return delLocation(row.id)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  getList()
  getZones()
  if (warehouseList.value.length === 0) {
    wmsStore.getWarehouseList()
  }
})
</script>
