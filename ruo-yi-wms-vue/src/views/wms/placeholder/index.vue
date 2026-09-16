<template>
  <div class="app-container">
    <el-card shadow="never">
      <div class="placeholder-container">
        <div class="placeholder-icon">
          <el-icon :size="64" color="#c0c4cc"><Tools /></el-icon>
        </div>
        <div class="placeholder-title">{{ title }}模块</div>
        <div class="placeholder-desc">该功能模块正在建设中，敬请期待</div>
        <el-tag type="warning" size="large" effect="plain">功能开发中</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup name="Placeholder">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title || '功能')
</script>

<style scoped>
.placeholder-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 16px;
}
.placeholder-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.placeholder-desc {
  font-size: 14px;
  color: #909399;
}
</style>
