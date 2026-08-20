<template>
  <div ref="chartRef" :style="{ height, width }" />
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: String, default: '320px' },
  width: { type: String, default: '100%' }
})

const chartRef = ref(null)
let chart = null

function render() {
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption(props.option, true)
}

function handleResize() {
  chart && chart.resize()
}

onMounted(() => {
  render()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart && chart.dispose()
  chart = null
})

watch(() => props.option, render, { deep: true })
</script>
