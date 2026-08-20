<template>
  <div class="chart-card app-card p-4">
    <div class="chart-title mb-3">{{ title }}</div>
    <BaseChart :option="option" :height="height" />
  </div>
</template>

<script setup>
/**
 * 商品分类占比饼图（对应课程：echarts 图表组件开发和交互）
 */
import { computed } from 'vue'
import BaseChart from '@/components/BaseChart.vue'

const props = defineProps({
  data: { type: Array, default: () => [] },
  title: { type: String, default: '商品分类占比' },
  height: { type: String, default: '320px' }
})

const option = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0, icon: 'circle' },
  color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'],
  series: [
    {
      name: '分类占比',
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '42%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}' },
      data: props.data
    }
  ]
}))
</script>

<style scoped lang="scss">
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
