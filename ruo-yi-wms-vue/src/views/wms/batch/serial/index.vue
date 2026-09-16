<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="序列号" prop="serialNo">
          <el-input v-model="queryParams.serialNo" placeholder="请输入序列号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="在库" value="0" />
            <el-option label="已出库" value="1" />
            <el-option label="报废" value="2" />
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
        <el-col :span="6"><span style="font-size: large">序列号列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['wms:serial:edit']">新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="serialList" border empty-text="暂无序列号">
        <el-table-column label="序列号" prop="serialNo" width="180" />
        <el-table-column label="批次号" prop="batchNo" width="150" />
        <el-table-column label="商品名称" prop="itemName" min-width="140" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="120" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="success">在库</el-tag>
            <el-tag v-else-if="row.status === '1'" type="warning">已出库</el-tag>
            <el-tag v-else type="danger">报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="140">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wms:serial:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wms:serial:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <el-dialog :title="title" v-model="open" width="520px" append-to-body :close-on-click-modal="false">
      <el-form ref="serialRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="序列号" prop="serialNo">
          <el-input v-model="form.serialNo" placeholder="请输入序列号" />
        </el-form-item>
        <el-form-item label="所属批次" prop="batchId">
          <el-select v-model="form.batchId" placeholder="请选择批次" filterable clearable style="width: 100%" @change="handleBatchChange">
            <el-option v-for="item in batchOptions" :key="item.id" :label="item.batchNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">在库</el-radio>
            <el-radio value="1">已出库</el-radio>
            <el-radio value="2">报废</el-radio>
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

<script setup name="Serial">
import { listSerial, getSerial, delSerial, addSerial, updateSerial } from '@/api/wms/serial'
import { listBatchNoPage } from '@/api/wms/batch'

const { proxy } = getCurrentInstance()
const serialList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')
const batchOptions = ref([])

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, serialNo: undefined, batchNo: undefined, status: undefined },
  rules: {
    serialNo: [{ required: true, message: '序列号不能为空', trigger: 'blur' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listSerial(queryParams.value).then(res => {
    serialList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadBatchOptions() {
  listBatchNoPage({}).then(res => { batchOptions.value = res.data || [] })
}

function handleBatchChange(val) {
  const batch = batchOptions.value.find(b => b.id === val)
  if (batch) {
    form.value.batchNo = batch.batchNo
    form.value.skuId = batch.skuId
  }
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { id: null, serialNo: null, batchId: null, batchNo: null, skuId: null, status: '0', remark: null }
  proxy.resetForm('serialRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleAdd() { reset(); open.value = true; title.value = '添加序列号' }
function handleUpdate(row) {
  reset()
  getSerial(row.id).then(res => { form.value = res.data; open.value = true; title.value = '修改序列号' })
}
function submitForm() {
  proxy.$refs['serialRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateSerial(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      } else {
        addSerial(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除序列号【' + row.serialNo + '】吗？').then(function () {
    return delSerial(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

onMounted(() => { getList(); loadBatchOptions() })
</script>
