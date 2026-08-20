<template>
  <span ref="elRef" class="count-up">{{ displayValue }}</span>
</template>

<script setup>
/**
 * 数字滚动动画（对应课程：数字滚动动画实现）
 * 用法：<CountUp :end="12345" :duration="2000" />
 */
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  end: { type: Number, default: 0 },
  duration: { type: Number, default: 1800 },
  decimals: { type: Number, default: 0 },
  separator: { type: Boolean, default: true }
})

const elRef = ref(null)
const displayValue = ref('0')
let raf = 0
let observer = null

function format(n) {
  const fixed = n.toFixed(props.decimals)
  const [int, dec] = fixed.split('.')
  const intStr = props.separator
    ? int.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    : int
  return dec ? `${intStr}.${dec}` : intStr
}

function animate() {
  const start = performance.now()
  const from = 0
  const to = props.end
  const step = (now) => {
    const progress = Math.min((now - start) / props.duration, 1)
    // easeOutCubic
    const eased = 1 - Math.pow(1 - progress, 3)
    displayValue.value = format(from + (to - from) * eased)
    if (progress < 1) {
      raf = requestAnimationFrame(step)
    }
  }
  raf = requestAnimationFrame(step)
}

onMounted(() => {
  // 进入视口时才开始动画
  if (!('IntersectionObserver' in window)) {
    animate()
    return
  }
  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
      animate()
      observer.disconnect()
    }
  })
  observer.observe(elRef.value)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(raf)
  observer && observer.disconnect()
})

watch(
  () => props.end,
  () => {
    cancelAnimationFrame(raf)
    displayValue.value = '0'
    animate()
  }
)
</script>
