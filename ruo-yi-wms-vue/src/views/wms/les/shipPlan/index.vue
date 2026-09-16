<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="计划单号" prop="planNo">
          <el-input v-model="queryParams.planNo" placeholder="请输入计划单号" clearable @keyup.enter="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="queryParams.customerId" placeholder="请选择客户" filterable clearable style="width: 200px">
            <el-option v-for="item in customerList" :key="item.id" :label="item.merchantName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待装车" value="0" />
            <el-option label="已装车" value="1" />
            <el-option label="已取消" value="2" />
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
        <el-col :span="6"><span style="font-size: large">发货计划列表</span></el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['les:shipPlan:edit']">新增发货计划</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="planList" border empty-text="暂无发货计划">
        <el-table-column label="计划单号" prop="planNo" width="160" />
        <el-table-column label="客户" prop="customerName" min-width="180" show-overflow-tooltip />
        <el-table-column label="发货仓库" prop="warehouseName" width="130" />
        <el-table-column label="出库单数" prop="shipmentCount" width="90" align="center" />
        <el-table-column label="总数量" prop="totalQuantity" width="90" align="right" />
        <el-table-column label="总金额" prop="totalAmount" width="110" align="right" />
        <el-table-column label="期望发货日期" prop="expectDate" width="120" align="center" />
        <el-table-column label="收货信息" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div>{{ row.receiverName }} {{ row.receiverPhone }}</div>
            <div class="sub">{{ row.receiverAddress }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === '0'" type="warning">待装车</el-tag>
            <el-tag v-else-if="row.status === '1'" type="success">已装车</el-tag>
            <el-tag v-else type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联出库单" prop="shipmentOrderNos" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="no in (row.shipmentOrderNos || [])" :key="no" size="small" class="mr5">{{ no }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-button v-if="scope.row.status === '0'" link type="warning" icon="Close" @click="handleCancel(scope.row)" v-hasPermi="['les:shipPlan:edit']">取消</el-button>
            <el-button v-if="scope.row.status === '0'" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['les:shipPlan:edit']">修改</el-button>
            <el-button v-if="scope.row.status === '0'" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['les:shipPlan:edit']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>

    <el-dialog :title="title" v-model="open" width="700px" append-to-body :close-on-click-modal="false">
      <el-form ref="planRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="计划单号" prop="planNo">
          <el-input v-model="form.planNo" placeholder="请输入计划单号，如 FH20260905001" />
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%" @change="handleCustomerChange">
            <el-option v-for="item in customerList" :key="item.id" :label="item.merchantName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发货仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择发货仓库" filterable style="width: 100%" @change="handleWarehouseChange">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联出库单" prop="shipmentOrderIds">
          <el-select v-model="form.shipmentOrderIds" multiple filterable placeholder="请选择销售出库单(已出库)" style="width: 100%">
            <el-option v-for="item in shipmentOptions" :key="item.id" :label="item.orderNo + ' - ' + item.totalQuantity + '件'" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="期望发货日期" prop="expectDate">
          <el-date-picker v-model="form.expectDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择期望发货日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="收货电话" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入收货电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input v-model="form.receiverAddress" placeholder="请输入收货地址" />
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

<script setup name="ShipPlan">
import { listShipPlan, getShipPlan, delShipPlan, addShipPlan, updateShipPlan, cancelShipPlan } from '@/api/wms/les'
import { listShipmentOrder } from '@/api/wms/shipmentOrder'
import { listMerchantNoPage } from '@/api/wms/merchant'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const wmsStore = useWmsStore()
const warehouseList = computed(() => wmsStore.warehouseList)
const customerList = ref([])
const shipmentOptions = ref([])

const planList = ref([])
const open = ref(false)
const buttonLoading = ref(false)
const loading = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, planNo: undefined, customerId: undefined, status: undefined },
  rules: {
    planNo: [{ required: true, message: '计划单号不能为空', trigger: 'blur' }],
    customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
    shipmentOrderIds: [{ required: true, type: 'array', message: '请选择出库单', trigger: 'change' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listShipPlan(queryParams.value).then(res => {
    planList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadCustomers() {
  listMerchantNoPage({ merchantType: 1 }).then(res => {
    customerList.value = res.data || []
  })
}

function loadShipmentOptions() {
  // 销售出库单(optType=2)且已出库(orderStatus=1)
  listShipmentOrder({ pageNum: 1, pageSize: 999, optType: 2, orderStatus: 1 }).then(res => {
    shipmentOptions.value = res.rows || []
  })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { id: null, planNo: null, customerId: null, warehouseId: null, shipmentOrderIds: [], expectDate: null, receiverName: null, receiverPhone: null, receiverAddress: null, remark: null }
  proxy.resetForm('planRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleWarehouseChange() { form.value.shipmentOrderIds = [] }

function handleCustomerChange(customerId) {
  const cust = customerList.value.find(c => c.id === customerId)
  if (cust) {
    form.value.receiverName = cust.contactPerson
    form.value.receiverPhone = cust.mobile || cust.tel
    form.value.receiverAddress = cust.address
  }
}

function handleAdd() { reset(); open.value = true; title.value = '新增发货计划'; loadShipmentOptions() }
function handleUpdate(row) {
  reset()
  getShipPlan(row.id).then(res => {
    form.value = res.data
    form.value.shipmentOrderIds = (res.data.shipmentOrderNos || []).map(no => {
      const opt = shipmentOptions.value.find(o => o.orderNo === no)
      return opt ? opt.id : null
    }).filter(Boolean)
    open.value = true
    title.value = '修改发货计划'
    loadShipmentOptions()
  })
}
function submitForm() {
  proxy.$refs['planRef'].validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        updateShipPlan(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      } else {
        addShipPlan(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList() }).finally(() => { buttonLoading.value = false })
      }
    }
  })
}
function handleCancel(row) {
  proxy.$modal.confirm('确认取消发货计划【' + row.planNo + '】吗？').then(() => {
    return cancelShipPlan(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('已取消') }).catch(() => {})
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除发货计划【' + row.planNo + '】吗？').then(function () {
    return delShipPlan(row.id)
  }).then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

onMounted(() => { getList(); loadCustomers(); if (warehouseList.value.length === 0) wmsStore.getWarehouseList() })
</script>

<style scoped>
.sub { font-size: 12px; color: #909399; }
</style>
