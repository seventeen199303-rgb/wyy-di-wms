<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="库区编码" prop="zoneCode">
          <el-input v-model="queryParams.zoneCode" placeholder="请输入库区编码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="库区名称" prop="zoneName">
          <el-input v-model="queryParams.zoneName" placeholder="请输入库区名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" filterable clearable style="width: 200px">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库区类型" prop="zoneType">
          <el-select v-model="queryParams.zoneType" placeholder="请选择库区类型" clearable style="width: 160px">
            <el-option v-for="dict in wms_zone_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
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
        <el-col :span="6"><span style="font-size: large">库区列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['wms:zone:edit']">新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="zoneList" border empty-text="暂无库区">
        <el-table-column label="库区编码" prop="zoneCode" width="120" />
        <el-table-column label="库区名称" prop="zoneName" min-width="140" />
        <el-table-column label="所属仓库" prop="warehouseName" width="140" />
        <el-table-column label="库区类型" prop="zoneType" width="110">
          <template #default="scope">
            <dict-tag :options="wms_zone_type" :value="scope.row.zoneType" />
          </template>
        </el-table-column>
        <el-table-column label="容量(库位)" prop="capacity" width="100" align="right" />
        <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="170" />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="140">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wms:zone:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wms:zone:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <!-- 添加或修改库区对话框 -->
    <el-dialog :title="title" v-model="open" width="560px" append-to-body :close-on-click-modal="false">
      <el-form ref="zoneRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库区名称" prop="zoneName">
          <el-input v-model="form.zoneName" placeholder="请输入库区名称" />
        </el-form-item>
        <el-form-item label="库区编码" prop="zoneCode">
          <el-input v-model="form.zoneCode" placeholder="请输入库区编码" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库区类型" prop="zoneType">
          <el-select v-model="form.zoneType" placeholder="请选择库区类型" style="width: 100%">
            <el-option v-for="dict in wms_zone_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量(库位)" prop="capacity">
          <el-input-number v-model="form.capacity" :min="0" :max="9999" controls-position="right" style="width: 100%" />
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

<script setup name="Zone">
import { listZone, getZone, delZone, addZone, updateZone } from '@/api/wms/zone'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const { wms_zone_type } = proxy.useDict('wms_zone_type')
const wmsStore = useWmsStore()
const warehouseList = computed(() => wmsStore.warehouseList)

const zoneList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    zoneCode: undefined,
    zoneName: undefined,
    warehouseId: undefined,
    zoneType: undefined
  },
  rules: {
    zoneName: [{ required: true, message: '库区名称不能为空', trigger: 'blur' }],
    warehouseId: [{ required: true, message: '所属仓库不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询库区列表 */
function getList() {
  loading.value = true
  listZone(queryParams.value).then(response => {
    zoneList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    zoneCode: null,
    zoneName: null,
    warehouseId: null,
    zoneType: 1,
    capacity: null,
    remark: null
  }
  proxy.resetForm('zoneRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加库区'
}

function handleUpdate(row) {
  reset()
  getZone(row.id).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改库区'
  })
}

function submitForm() {
  proxy.$refs['zoneRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateZone(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        }).finally(() => { buttonLoading.value = false })
      } else {
        addZone(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除库区【' + row.zoneName + '】吗？').then(function () {
    return delZone(row.id)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  getList()
  if (warehouseList.value.length === 0) {
    wmsStore.getWarehouseList()
  }
})
</script>
