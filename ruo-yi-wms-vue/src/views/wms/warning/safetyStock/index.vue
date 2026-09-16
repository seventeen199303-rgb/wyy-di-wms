<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="90px">
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" filterable clearable style="width: 180px">
            <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库区" prop="zoneId">
          <el-select v-model="queryParams.zoneId" placeholder="请选择库区" filterable clearable style="width: 160px">
            <el-option v-for="item in zoneList" :key="item.id" :label="item.zoneName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="itemName">
          <el-input v-model="queryParams.itemName" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">
      <el-alert title="安全库存预警：当前库存低于安全库存下限的物料" type="warning" :closable="false" class="mb10" show-icon />
      <el-table v-loading="loading" :data="list" border empty-text="暂无预警">
        <el-table-column label="商品名称" prop="itemName" min-width="140" show-overflow-tooltip />
        <el-table-column label="规格名称" prop="skuName" min-width="120" show-overflow-tooltip />
        <el-table-column label="仓库" prop="warehouseName" width="130" />
        <el-table-column label="库区" prop="zoneName" width="120" />
        <el-table-column label="库位" prop="locationCode" width="100" />
        <el-table-column label="当前库存" prop="quantity" width="100" align="right" />
        <el-table-column label="安全库存" prop="safetyStock" width="100" align="right" />
        <el-table-column label="缺口数量" prop="difference" width="100" align="right">
          <template #default="{ row }">
            <span class="text-danger">{{ row.difference }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="SafetyStock">
import { listSafetyStockWarning } from '@/api/wms/warning'
import { listZoneNoPage } from '@/api/wms/zone'
import { useWmsStore } from '@/store/modules/wms'

const { proxy } = getCurrentInstance()
const wmsStore = useWmsStore()
const warehouseList = computed(() => wmsStore.warehouseList)
const zoneList = ref([])

const list = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  warehouseId: undefined,
  zoneId: undefined,
  itemName: undefined
})

function getList() {
  loading.value = true
  listSafetyStockWarning(queryParams.value).then(res => {
    list.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

onMounted(() => {
  getList()
  listZoneNoPage({}).then(res => { zoneList.value = res.data || [] })
  if (warehouseList.value.length === 0) wmsStore.getWarehouseList()
})
</script>

<style scoped>
.text-danger { color: #f56c6c; font-weight: bold; }
</style>
