<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="商品名称" prop="itemName">
          <el-input v-model="queryParams.itemName" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="正常" value="0" />
            <el-option label="已过期" value="1" />
            <el-option label="已用完" value="2" />
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
        <el-col :span="6"><span style="font-size: large">批次列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['wms:batch:edit']">新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="batchList" border empty-text="暂无批次">
        <el-table-column label="批次号" prop="batchNo" width="150" />
        <el-table-column label="商品名称" prop="itemName" min-width="140" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="120" show-overflow-tooltip />
        <el-table-column label="批次数量" prop="quantity" width="90" align="right" />
        <el-table-column label="生产日期" prop="productionDate" width="110" />
        <el-table-column label="有效期至" prop="expiryDate" width="110" />
        <el-table-column label="供应商" prop="supplier" min-width="130" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === '1'" type="danger">已过期</el-tag>
            <el-tag v-else type="info">已用完</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="140">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wms:batch:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wms:batch:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <el-dialog :title="title" v-model="open" width="560px" append-to-body :close-on-click-modal="false">
      <el-form ref="batchRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="form.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="商品规格" prop="skuId">
          <el-select v-model="form.skuId" placeholder="请选择商品规格" filterable style="width: 100%">
            <el-option v-for="item in skuOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择生产日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期至" prop="expiryDate">
          <el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择有效期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" placeholder="请输入供应商/来源" />
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

<script setup name="Batch">
import { listBatch, getBatch, delBatch, addBatch, updateBatch } from '@/api/wms/batch'
import { listItemSku } from '@/api/wms/itemSku'
import { listItem } from '@/api/wms/item'

const { proxy } = getCurrentInstance()
const batchList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')
const skuOptions = ref([])

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, batchNo: undefined, itemName: undefined, status: undefined },
  rules: {
    batchNo: [{ required: true, message: '批次号不能为空', trigger: 'blur' }],
    skuId: [{ required: true, message: '请选择商品规格', trigger: 'change' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listBatch(queryParams.value).then(res => {
    batchList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadSkuOptions() {
  listItem({}).then(res => {
    const items = res.rows || []
    listItemSku({}).then(r2 => {
      const skus = r2.data || []
      const itemMap = new Map(items.map(i => [i.id, i.itemName]))
      skuOptions.value = skus.map(s => ({ id: s.id, label: (itemMap.get(s.itemId) || '') + ' - ' + s.skuName }))
    })
  })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { id: null, batchNo: null, skuId: null, quantity: null, productionDate: null, expiryDate: null, supplier: null, status: '0', remark: null }
  proxy.resetForm('batchRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleAdd() { reset(); open.value = true; title.value = '添加批次' }
function handleUpdate(row) {
  reset()
  getBatch(row.id).then(res => { form.value = res.data; open.value = true; title.value = '修改批次' })
}
function submitForm() {
  proxy.$refs['batchRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateBatch(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      } else {
        addBatch(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除批次【' + row.batchNo + '】吗？').then(function () {
    return delBatch(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

onMounted(() => { getList(); loadSkuOptions() })
</script>
