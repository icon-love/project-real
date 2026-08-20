<template>
  <div class="chart-card app-card p-4">
    <div class="chart-title mb-3">{{ title }}</div>
    <BaseChart :option="option" :height="height" />
  </div>
</template>

<script setup>
/**
 * 销售趋势折线图（对应课程：echarts 图表组件开发和交互）
 */
import { computed } from 'vue'
import BaseChart from '@/components/BaseChart.vue'

const props = defineProps({
  data: { type: Object, required: true },
  title: { type: String, default: '销售趋势' },
  height: { type: String, default: '320px' }
})

const option = computed(() => {
  const { months = [], sales = [], orders = [] } = props.data
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单量'], top: 0 },
    grid: { left: 45, right: 20, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months,
      axisLine: { lineStyle: { color: '#dcdfe6' } }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: sales,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64,158,255,0.25)' },
              { offset: 1, color: 'rgba(64,158,255,0)' }
            ]
          }
        }
      },
      {
        name: '订单量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: orders,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
})
</script>

<style scoped lang="scss">
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
