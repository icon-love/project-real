<template>
  <div class="stat-card">
    <div
      class="stat-icon"
      :style="{ background: item.color + '1a', color: item.color }"
    >
      <el-icon :size="26"><component :is="item.icon" /></el-icon>
    </div>
    <div class="stat-info">
      <div class="stat-name">{{ item.name }}</div>
      <div class="stat-value">
        <CountUp :end="item.value" />
        <span class="unit">{{ item.unit }}</span>
      </div>
      <div class="stat-trend" :class="item.trend >= 0 ? 'up' : 'down'">
        <el-icon>
          <CaretTop v-if="item.trend >= 0" />
          <CaretBottom v-else />
        </el-icon>
        {{ Math.abs(item.trend) }}%
        <span class="desc">{{ item.desc }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 统计面板组件（对应课程：统计面板组件开发）
 */
import CountUp from './CountUp.vue'

defineProps({
  item: { type: Object, required: true }
})
</script>

<style scoped lang="scss">
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0, 21, 41, 0.12);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-info {
    flex: 1;
    min-width: 0;

    .stat-name {
      font-size: 13px;
      color: #909399;
    }

    .stat-value {
      font-size: 26px;
      font-weight: 600;
      color: #303133;
      margin: 4px 0;

      .unit {
        font-size: 13px;
        font-weight: 400;
        color: #909399;
        margin-left: 4px;
      }
    }

    .stat-trend {
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: 12px;

      &.up {
        color: #67c23a;
      }
      &.down {
        color: #f56c6c;
      }

      .desc {
        color: #909399;
        margin-left: 4px;
      }
    }
  }
}
</style>
