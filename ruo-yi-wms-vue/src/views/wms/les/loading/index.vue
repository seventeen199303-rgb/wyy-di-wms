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
        <el-form-item label="司机" prop="driverName">
          <el-input v-model="queryParams.driverName" placeholder="请输入司机姓名" clearable style="width: 150px" />
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
      <el-row :gutter="10" class="mb8" type="flex" justify="space-between">
        <el-col :span="6"><span style="font-size: large">装车单列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['les:loading:edit']">新增装车单</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="loadList" border empty-text="暂无装车单">
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
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning">待发车</el-tag>
            <el-tag v-else-if="row.status === '1'" type="primary">在途</el-tag>
            <el-tag v-else-if="row.status === '2'" type="success">已签收</el-tag>
            <el-tag v-else type="danger">异常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发车时间" prop="departTime" width="160" align="center" />
        <el-table-column label="装车计划" prop="shipPlanNos" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="no in (row.shipPlanNos || [])" :key="no" size="small" class="mr5">{{ no }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="150">
          <template #default="scope">
            <el-button v-if="scope.row.status === '0'" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['les:loading:edit']">修改</el-button>
            <el-button v-if="scope.row.status === '0'" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['les:loading:edit']">删除</el-button>
            <el-button v-if="scope.row.status === '0'" link type="success" icon="Position" @click="handleDepart(scope.row)" v-hasPermi="['les:delivery:edit']">发车</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body :close-on-click-modal="false">
      <el-form ref="loadRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="装车单号" prop="loadNo">
          <el-input v-model="form.loadNo" placeholder="请输入装车单号，如 ZC20260905001" />
        </el-form-item>
        <el-form-item label="车牌号" prop="vehicleNo">
          <el-input v-model="form.vehicleNo" placeholder="请输入车牌号，如 苏A·88888" />
        </el-form-item>
        <el-form-item label="司机姓名" prop="driverName">
          <el-input v-model="form.driverName" placeholder="请输入司机姓名" />
        </el-form-item>
        <el-form-item label="司机电话" prop="driverPhone">
          <el-input v-model="form.driverPhone" placeholder="请输入司机电话" />
        </el-form-item>
        <el-form-item label="装车计划" prop="shipPlanIds">
          <el-select v-model="form.shipPlanIds" multiple filterable placeholder="请选择待装车的发货计划(可多选)" style="width: 100%">
            <el-option v-for="item in planOptions" :key="item.id" :label="item.planNo + ' - ' + item.customerName" :value="item.id" />
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

<script setup name="Loading">
import { listLoadOrder, getLoadOrder, delLoadOrder, addLoadOrder, updateLoadOrder, departLoadOrder } from '@/api/wms/les'
import { listShipPlanNoPage } from '@/api/wms/les'

const { proxy } = getCurrentInstance()
const planOptions = ref([])

const loadList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, loadNo: undefined, vehicleNo: undefined, driverName: undefined, status: undefined },
  rules: {
    loadNo: [{ required: true, message: '装车单号不能为空', trigger: 'blur' }],
    vehicleNo: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
    shipPlanIds: [{ required: true, type: 'array', message: '请选择发货计划', trigger: 'change' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listLoadOrder(queryParams.value).then(res => {
    loadList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadPlanOptions() {
  // 加载待装车的发货计划；修改时后端会校验已装车的计划
  listShipPlanNoPage({ status: '0' }).then(res => {
    planOptions.value = res.data || []
  })
}

function loadAllPlanOptions() {
  // 加载全部计划(用于修改装车单时回显已关联的计划)
  listShipPlanNoPage({}).then(res => {
    planOptions.value = res.data || []
  })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { id: null, loadNo: null, vehicleNo: null, driverName: null, driverPhone: null, shipPlanIds: [], remark: null }
  proxy.resetForm('loadRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }

function handleAdd() { reset(); open.value = true; title.value = '新增装车单'; loadPlanOptions() }
function handleUpdate(row) {
  reset()
  getLoadOrder(row.id).then(res => {
    form.value = res.data
    // 加载全部计划以便回显已关联的计划
    listShipPlanNoPage({}).then(allRes => {
      planOptions.value = allRes.data || []
      form.value.shipPlanIds = (res.data.shipPlanNos || []).map(no => {
        const opt = planOptions.value.find(o => o.planNo === no)
        return opt ? opt.id : null
      }).filter(Boolean)
      open.value = true
      title.value = '修改装车单'
    })
  })
}
function submitForm() {
  proxy.$refs['loadRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateLoadOrder(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      } else {
        addLoadOrder(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}
function handleDepart(row) {
  proxy.$modal.confirm('确认装车单【' + row.loadNo + '】发车吗？').then(() => {
    return departLoadOrder(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('已发车') }).catch(() => {})
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除装车单【' + row.loadNo + '】吗？关联的发货计划将退回待装车状态。').then(function () {
    return delLoadOrder(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

onMounted(() => { getList() })
</script>

<style scoped>
.sub { font-size: 12px; color: #909399; }
</style>
