<template>
  <div class="app-container home">
    <!-- 顶部统计卡片 -->
    <div class="station-top">
      <el-row :gutter="16" class="mt5">
        <el-col :span="6">
          <div class="stat-card stat-card-one">
            <div class="stat-card-main">
              <div class="stat-card-icon">
                <el-icon :size="28"><Van /></el-icon>
              </div>
              <div class="stat-card-body">
                <div class="stat-card-label">入库</div>
                <div class="stat-card-value">33</div>
              </div>
            </div>
            <div class="stat-card-detail">
              <div class="detail-item"><span class="dot"></span>待入库：<b>15</b></div>
              <div class="detail-item"><span class="dot"></span>待质检：<b>15</b></div>
              <div class="detail-item"><span class="dot"></span>待上架：<b>2</b></div>
              <div class="detail-item"><span class="dot"></span>待完成：<b>1</b></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-card-two">
            <div class="stat-card-main">
              <div class="stat-card-icon">
                <el-icon :size="28"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-card-body">
                <div class="stat-card-label">出库</div>
                <div class="stat-card-value">20</div>
              </div>
            </div>
            <div class="stat-card-detail">
              <div class="detail-item"><span class="dot"></span>待配货：<b>5</b></div>
              <div class="detail-item"><span class="dot"></span>待波次：<b>5</b></div>
              <div class="detail-item"><span class="dot"></span>待拣货：<b>5</b></div>
              <div class="detail-item"><span class="dot"></span>待出库：<b>5</b></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-card-three">
            <div class="stat-card-main">
              <div class="stat-card-icon">
                <el-icon :size="28"><Tickets /></el-icon>
              </div>
              <div class="stat-card-body">
                <div class="stat-card-label">其他</div>
                <div class="stat-card-value">15</div>
              </div>
            </div>
            <div class="stat-card-detail">
              <div class="detail-item"><span class="dot"></span>待截单：<b>5</b></div>
              <div class="detail-item"><span class="dot"></span>异常单：<b>5</b></div>
              <div class="detail-item"><span class="dot"></span>今日到货：<b>5</b></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-card-four">
            <div class="stat-card-main">
              <div class="stat-card-icon">
                <el-icon :size="28"><Warning /></el-icon>
              </div>
              <div class="stat-card-body">
                <div class="stat-card-label">库存预警</div>
                <div class="stat-card-value">5</div>
              </div>
            </div>
            <div class="stat-card-detail">
              <div class="detail-item"><span class="dot"></span>松陵仓：<b>1</b></div>
              <div class="detail-item"><span class="dot"></span>盛泽仓：<b>2</b></div>
              <div class="detail-item"><span class="dot"></span>园区仓：<b>2</b></div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 中间：饼图 + 柱状图 -->
    <div class="station-middle">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">仓库货物占比</div>
            <div class="card-chart-body">
              <StationPie height="100%"></StationPie>
            </div>
          </el-card>
        </el-col>
        <el-col :span="18">
          <el-card class="box-card" shadow="never">
            <div class="card-header">
              <div class="card-title">生产入库趋势</div>
              <el-radio-group v-model="tabPosition" @change="dateChange">
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">今年</el-radio-button>
              </el-radio-group>
            </div>
            <div class="card-chart-body">
              <StationBar height="100%" :chartData="barChartData" :xName="barXName"/>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 底部：四个折线图 -->
    <div class="station-bottom">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日销售出库</div>
            <div class="card-chart-body">
              <StationLine height="100%" itemColor="#ee4368" yName="件" :chartData="lineDataOne"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日领料出库</div>
            <div class="card-chart-body">
              <StationLine height="100%" :chartData="lineDataTwo" yName="件" itemColor="#5470c6"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日移库</div>
            <div class="card-chart-body">
              <StationLine height="100%" :chartData="lineDataThree" yName="件" itemColor="#c58bea"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="box-card" shadow="never">
            <div class="card-title">近7日退货入库</div>
            <div class="card-chart-body">
              <StationLine height="100%" yName="件" :chartData="lineDataFour" itemColor="#c7a428"/>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import StationPie from './components/StationPie.vue'
import StationLine from './components/StationLine.vue'
import StationBar from './components/StationBar.vue'
import { onMounted, ref } from 'vue'
import moment from 'moment';

const tabPosition = ref('month')
const barChartData = ref({
  yData: [79, 68, 56, 72, 51, 63, 67, 71, 58, 81, 64, 77, 56, 69]
})
const barXName = ref('日')
const lineDataOne = ref({
  yData: [79, 65, 21, 67, 21, 89, 56],
})
const lineDataTwo = ref({
  yData: [45, 72, 16, 37, 64, 28, 46],
})
const lineDataThree = ref(
  {
    yData: [16, 27, 37, 16, 27, 21, 11],
  },
)
const lineDataFour = ref({
  yData: [134, 107, 94, 173, 37, 143, 86],
})

onMounted((()=>{
  initTime();
  dateChange('month');
}))

// 时间类型选择
function dateChange(value) {
  let date = new Date()
  let month = date.getMonth() + 1
  let day = date.getDate()
  let barXData = []
  let barYData = []
  if(value === 'year') {
    for(let i = 0; i < month; i++) {
      barXData.push(moment().subtract(i, 'months').format('YYYY-MM'))
      barYData.push(Math.floor(Math.random()*(180-120+1))+120)
    }
    barXName.value = '月'
  } else {
    for(let i = 0; i < day; i++) {
      barXData.push(moment().subtract(i, 'days').format('MM-DD'))
      barYData.push(Math.floor(Math.random()*(30-15+1))+15)
    }
    barXName.value = '日'
  }
  barChartData.value = {
    xData: barXData.reverse(),
    yData: barYData
  }
}
// 初始化时间模拟数据
function initTime() {
  let lineXData = []
  for(let i = 0; i < 7; i++) {
    lineXData.push(moment().subtract(i, 'days').format('MM-DD'))
  }
  lineXData = lineXData.reverse()
  lineDataOne.value.xData = lineXData
  lineDataTwo.value.xData = lineXData
  lineDataThree.value.xData = lineXData
  lineDataFour.value.xData = lineXData
}


</script>


<style scoped>
.app-container {
  min-height: calc(100vh - 84px);
  padding: 16px;
  background: #f0f2f5;
}

/* ===== 顶部统计卡片 ===== */
.stat-card {
  position: relative;
  height: 150px;
  padding: 20px 22px;
  margin-bottom: 16px;
  border-radius: 14px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  transition: transform .25s ease, box-shadow .25s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card::after {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}

.stat-card-main {
  display: flex;
  align-items: center;
  gap: 14px;
  position: relative;
  z-index: 1;
}

.stat-card-icon {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.stat-card-label {
  font-size: 14px;
  opacity: 0.92;
}

.stat-card-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: 1px;
}

.stat-card-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 18px;
  font-size: 12px;
  opacity: 0.95;
  position: relative;
  z-index: 1;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.detail-item b {
  font-weight: 700;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #fff;
  display: inline-block;
  opacity: 0.85;
}

/* 卡片配色 */
.stat-card-one {
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.28);
}

.stat-card-two {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  box-shadow: 0 8px 20px rgba(245, 158, 11, 0.28);
}

.stat-card-three {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  box-shadow: 0 8px 20px rgba(124, 58, 237, 0.28);
}

.stat-card-four {
  background: linear-gradient(135deg, #ef4444, #fb7185);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.28);
}

/* ===== 图表卡片 ===== */
.box-card {
  height: 400px;
  margin-bottom: 16px;
  background-color: #fff;
  border: none;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow .25s ease;
}

.box-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.box-card :deep(.el-card__body) {
  height: 100%;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: 700;
  font-size: 15px;
  height: 30px;
  display: flex;
  align-items: center;
  color: #1f2937;
}

.card-title::before {
  content: '';
  height: 16px;
  width: 4px;
  border-radius: 2px;
  background: linear-gradient(180deg, #f56c0a, #fb923c);
  margin-right: 10px;
}

.card-chart-body {
  flex: 1;
  min-height: 0;
}
</style>
