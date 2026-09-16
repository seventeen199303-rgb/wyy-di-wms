<template>
  <div class="board-container">
    <div v-if="showWhich" class="back-btn" @click="backHome"><img src="@/assets/images/home.png">返回</div>
    <img v-else src="@/assets/images/fullscreen.png" alt="" class="fullscreen-img" @click="toDataBoard">
    <div class="time-stamp">{{ nowTime }}</div>
    <div class="board-title"><span>王有用 WMS 数据大屏</span></div>

    <div class="board-content flex-between">
      <!-- 左栏 -->
      <div class="content-left flex-column-between">
        <div class="content-overview">
          <div class="box-title">库存总览</div>
          <div class="box-content flex-between">
            <div class="overview-object flex-column-center">
              <div class="object-count">{{ warehouseTotal }}</div>
              <div class="object-name">仓库总数</div>
            </div>
            <div class="overview-meter flex-column-center">
              <div class="object-count">{{ itemTotal }}</div>
              <div class="object-name">商品总数</div>
            </div>
            <div class="overview-alarm flex-column-center">
              <div class="object-count">{{ stockTotal }}</div>
              <div class="object-name">库存总量</div>
            </div>
          </div>
        </div>

        <div class="content-status">
          <div class="box-title">各仓库库存占比</div>
          <div class="box-content">
            <CirclePieChart height="100%" :pieData="warehousePieData"/>
          </div>
        </div>

        <div class="content-alarm">
          <div class="box-title">出入库流水</div>
          <div class="box-content">
            <Vue3SeamlessScroll :list="flowList" :hover="true" :step="0.4" :wheel="true" :isWatch="true" :limitScrollNum="5" style="height: 100%;overflow: hidden;">
              <div class="flow-item" v-for="(item, index) in flowList" :key="index">
                <div class="flex-between mb5">
                  <span class="flow-no">{{ item.orderNo }}</span>
                  <span class="flow-type" :class="'type-' + item.orderType">{{ orderTypeName(item.orderType) }}</span>
                </div>
                <div class="flex-between">
                  <span>{{ warehouseName(item.warehouseId) }}</span>
                  <span :class="Number(item.quantity) >= 0 ? 'flow-in' : 'flow-out'">{{ Number(item.quantity) >= 0 ? '+' : '' }}{{ formatQuantity(item.quantity) }}</span>
                </div>
              </div>
            </Vue3SeamlessScroll>
          </div>
        </div>
      </div>

      <!-- 中栏 -->
      <div class="content-middle flex-column-between">
        <div class="content-chart content-chart-big">
          <div class="box-title">各仓库库存量</div>
          <div class="box-content">
            <BarChart
              :height="'100%'"
              :barColor="['#f56c0a', '#ffb25e']"
              :yName="'件'"
              :xData="warehouseNames"
              :yData="warehouseStock"
            />
          </div>
        </div>
        <div class="content-chart content-chart-small">
          <div class="box-title">今日出入库流水趋势</div>
          <div class="box-content">
            <TrendLineChart
              :height="'100%'"
              :yName="'件'"
              :xData="flowXData"
              :yData="flowYData"
            />
          </div>
        </div>
      </div>

      <!-- 右栏 -->
      <div class="content-right flex-column-between">
        <div class="content-statistics">
          <div class="box-title">入库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ receiptCount }}</div>
              <div style="text-align: center">入库单总数</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ receiptDone }}</div>
              <div style="text-align: center">已入库</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ receiptPending }}</div>
              <div style="text-align: center">待入库</div>
            </div>
          </div>
        </div>

        <div class="content-statistics">
          <div class="box-title">出库统计</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ shipmentCount }}</div>
              <div style="text-align: center">出库单总数</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ shipmentDone }}</div>
              <div style="text-align: center">已出库</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ shipmentPending }}</div>
              <div style="text-align: center">待出库</div>
            </div>
          </div>
        </div>

        <div class="content-statistics">
          <div class="box-title">移库 / 盘库</div>
          <div class="box-content flex-between">
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ movementCount }}</div>
              <div style="text-align: center">移库单</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ checkCount }}</div>
              <div style="text-align: center">盘库单</div>
            </div>
            <div class="statistics-item flex-column-center">
              <div class="item-count">{{ merchantCount }}</div>
              <div style="text-align: center">往来企业</div>
            </div>
          </div>
        </div>

        <div class="content-carbon">
          <div class="box-title">各仓库入库数量</div>
          <div class="box-content">
            <BarChart
              :height="'100%'"
              :barColor="['#1c508e', '#1be5e7']"
              :yName="'件'"
              :xData="warehouseNames"
              :yData="receiptByWarehouse"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import CirclePieChart from './components/dashboard/CirclePieChart.vue'
import BarChart from './components/dashboard/BarChart.vue'
import TrendLineChart from './components/dashboard/TrendLineChart.vue'
import { Vue3SeamlessScroll } from 'vue3-seamless-scroll'
import moment from 'moment'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listWarehouseNoPage } from '@/api/wms/warehouse'
import { listInventoryBoard } from '@/api/wms/inventory'
import { listInventoryHistory } from '@/api/wms/inventoryHistory'
import { listReceiptOrder } from '@/api/wms/receiptOrder'
import { listShipmentOrder } from '@/api/wms/shipmentOrder'
import { listMovementOrder } from '@/api/wms/movementOrder'
import { listCheckOrder } from '@/api/wms/checkOrder'
import { listMerchantNoPage } from '@/api/wms/merchant'

const router = useRouter();
const showWhich = computed(() => router.currentRoute.value.path === '/system/dashboard')

const nowTime = ref()
const timer = ref()

// 仓库
const warehouseList = ref([])
const warehouseTotal = ref(0)
const warehouseMap = new Map()
// 库存
const stockTotal = ref(0)
const itemTotal = ref(0)
const warehousePieData = ref([])
const warehouseNames = ref([])
const warehouseStock = ref([])
const receiptByWarehouse = ref([])
// 流水
const flowList = ref([])
const flowXData = ref([])
const flowYData = ref([])
// 单据统计
const receiptCount = ref(0)
const receiptDone = ref(0)
const receiptPending = ref(0)
const shipmentCount = ref(0)
const shipmentDone = ref(0)
const shipmentPending = ref(0)
const movementCount = ref(0)
const checkCount = ref(0)
const merchantCount = ref(0)

function toDataBoard() { router.push('/system/dashboard') }
function backHome() { router.push('/dashboard') }

function warehouseName(id) {
  const w = warehouseMap.get(id)
  return w ? w.warehouseName : '未知仓库'
}

function orderTypeName(type) {
  const map = { 1: '入库', 2: '出库', 3: '移库', 4: '盘库' }
  return map[type] || '操作'
}

function formatQuantity(q) {
  const n = Number(q || 0)
  return Number.isInteger(n) ? n : n.toFixed(1)
}

// 加载仓库
async function loadWarehouses() {
  const res = await listWarehouseNoPage({})
  warehouseList.value = res.data || []
  warehouseTotal.value = warehouseList.value.length
  warehouseList.value.forEach(w => warehouseMap.set(w.id, w))
  warehouseNames.value = warehouseList.value.map(w => w.warehouseName)
}

// 加载库存
async function loadInventory() {
  const res = await listInventoryBoard({ pageSize: 999, pageNum: 1 }, 'warehouse')
  const rows = res.rows || []
  const byWarehouse = {}
  const itemIds = new Set()
  rows.forEach(it => {
    const wid = it.warehouseId
    if (!byWarehouse[wid]) byWarehouse[wid] = 0
    byWarehouse[wid] += Number(it.quantity || 0)
    if (it.item && it.item.id) itemIds.add(it.item.id)
  })
  stockTotal.value = rows.reduce((s, it) => s + Number(it.quantity || 0), 0)
  itemTotal.value = itemIds.size

  // 饼图数据（各仓库库存）
  warehousePieData.value = warehouseList.value.map(w => ({
    value: byWarehouse[w.id] || 0,
    name: w.warehouseName
  })).filter(it => it.value > 0)
  if (warehousePieData.value.length === 0) {
    warehousePieData.value = warehouseList.value.map(w => ({ value: 0, name: w.warehouseName }))
  }
  // 柱状图数据
  warehouseStock.value = warehouseList.value.map(w => byWarehouse[w.id] || 0)
}

// 加载流水
async function loadFlows() {
  const res = await listInventoryHistory({ pageSize: 999, pageNum: 1 })
  const rows = res.rows || []
  flowList.value = rows.slice(0, 30)
  // 按时间聚合出趋势（按日期）
  const byDate = {}
  rows.forEach(it => {
    const d = (it.createTime || '').slice(0, 10)
    if (!byDate[d]) byDate[d] = 0
    byDate[d] += Math.abs(Number(it.quantity || 0))
  })
  const dates = Object.keys(byDate).sort()
  flowXData.value = dates.map(d => d.slice(5))
  flowYData.value = dates.map(d => byDate[d])
  if (flowXData.value.length === 0) {
    flowXData.value = []
    flowYData.value = []
  }
}

// 加载单据统计
async function loadOrders() {
  const r = await listReceiptOrder({ pageSize: 999, pageNum: 1 })
  const receiptRows = r.rows || []
  receiptCount.value = receiptRows.length
  receiptDone.value = receiptRows.filter(it => it.orderStatus === 1).length
  receiptPending.value = receiptRows.filter(it => it.orderStatus === 0).length
  const byWarehouse = {}
  receiptRows.forEach(it => {
    if (!byWarehouse[it.warehouseId]) byWarehouse[it.warehouseId] = 0
    byWarehouse[it.warehouseId] += Number(it.totalQuantity || 0)
  })
  receiptByWarehouse.value = warehouseList.value.map(w => byWarehouse[w.id] || 0)

  const s = await listShipmentOrder({ pageSize: 999, pageNum: 1 })
  const shipRows = s.rows || []
  shipmentCount.value = shipRows.length
  shipmentDone.value = shipRows.filter(it => it.orderStatus === 1).length
  shipmentPending.value = shipRows.filter(it => it.orderStatus === 0).length

  const m = await listMovementOrder({ pageSize: 999, pageNum: 1 })
  movementCount.value = (m.rows || []).length

  const c = await listCheckOrder({ pageSize: 999, pageNum: 1 })
  checkCount.value = (c.rows || []).length

  const mc = await listMerchantNoPage({})
  merchantCount.value = (mc.data || []).length
}

function getNowTime() {
  nowTime.value = moment().format('YYYY-MM-DD HH:mm:ss')
}

onMounted(async () => {
  getNowTime()
  timer.value = setInterval(getNowTime, 1000)
  try {
    await loadWarehouses()
    await Promise.all([loadInventory(), loadFlows(), loadOrders()])
  } catch (e) {
    console.warn('大屏数据加载失败', e)
  }
})

onBeforeUnmount(() => {
  clearInterval(timer.value)
})
</script>

<style scoped>
.board-container {
  width: 100%;
  height: 100vh;
  background-image: url("../../assets/images/board-bg.png");
  background-size: 100% 100%;
  background-color: #071a2e;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.back-btn {
  position: absolute;
  left: 2.5%;
  top: 3%;
  height: 3%;
  color: #00d0ff;
  font-size: 17px;
  display: flex;
  align-items: center;
  cursor: pointer;
  z-index: 10;
}
.back-btn img { height: 100%; margin-right: 6px; }

.fullscreen-img {
  position: absolute;
  left: 2.5%;
  top: 3%;
  height: 3%;
  cursor: pointer;
  z-index: 10;
}

.time-stamp {
  position: absolute;
  right: 2.5%;
  top: 3%;
  color: #00d1ff;
  font-size: 20px;
  z-index: 10;
}

.board-title {
  background-image: linear-gradient(to top, #f56c0a, #ffd180);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  font-size: 38px;
  font-weight: bold;
  letter-spacing: 8px;
  height: 9%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.board-content {
  height: 86%;
  width: 96%;
  margin: 0 auto;
}

.flex-between { display: flex; justify-content: space-between; }
.flex-column-between { display: flex; flex-direction: column; justify-content: space-between; }
.flex-column-center { display: flex; flex-direction: column; align-items: center; }

.content-left { width: calc(25% - 12px); margin-right: 12px; }
.content-middle { width: 50%; margin-right: 12px; }
.content-right { width: calc(25% - 12px); }

.content-overview,
.content-status,
.content-alarm,
.content-statistics,
.content-carbon,
.content-chart {
  background-image: url("../../assets/images/box-bg1.png");
  background-size: 100% 100%;
}

.content-overview { height: 20%; margin-bottom: 12px; padding: 12px 16px; }
.content-status { height: calc(40% - 12px); margin-bottom: 12px; padding: 16px 16px; }
.content-alarm { height: calc(40% - 12px); padding: 12px 16px; }

.box-title {
  height: 20px;
  margin-left: 12px;
  display: flex;
  align-items: center;
  color: #01d1ff;
  font-weight: bold;
}
.box-title::before {
  content: " ";
  width: 6px;
  height: 100%;
  border-radius: 10px;
  display: inline-block;
  margin-right: 6px;
  background: linear-gradient(to bottom, #ff9e2c, #f56c0a);
}

.box-content { height: calc(100% - 20px); overflow: hidden; padding: 16px 10px 6px; }

.overview-object,
.overview-meter,
.overview-alarm { width: calc(33% - 10px); align-items: center; text-align: center; }

.object-count {
  color: #ffb25e;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 6px;
}
.object-name { font-size: 12px; color: #b0c4de; }

.content-middle .content-chart-big { height: 58%; }
.content-middle .content-chart-small { height: calc(42% - 12px); }
.content-middle .content-chart { padding: 16px 24px; }

.content-statistics { height: 20%; padding: 10px 16px; }
.content-carbon { height: calc(40% - 12px); padding: 12px 16px; }

.statistics-item { width: calc(33% - 8px); align-items: center; font-size: 12px; color: #b0c4de; }
.item-count {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  color: #ffb25e;
  margin: 4px 0;
}

/* 流水列表 */
.flow-item {
  padding: 10px 0;
  border-bottom: 1px solid;
  border-image: linear-gradient(90deg, #f56c0a 0%, #286be9 50%, #f56c0a 100%) 2 2 2 2;
  font-size: 13px;
}
.flow-no { font-weight: bold; color: #00d0fe; }
.flow-type { padding: 2px 8px; font-size: 12px; color: #fff; border-radius: 10px 0 10px 0; }
.type-1 { background: #67c23a; }
.type-2 { background: #f56c0a; }
.type-3 { background: #e6a23c; }
.type-4 { background: #909399; }
.flow-in { color: #67c23a; font-weight: bold; }
.flow-out { color: #f56c6c; font-weight: bold; }
</style>
