<template>
  <div class="dashboard">
    <!-- 统计面板 + 骨架屏 -->
    <div class="stats-grid">
      <el-skeleton v-if="loading" :rows="1" animated class="app-card p-4">
        <template #template>
          <div class="flex items-center gap-16px">
            <el-skeleton-item variant="rect" style="width: 56px; height: 56px" />
            <div class="flex-1">
              <el-skeleton-item variant="text" style="width: 40%" />
              <el-skeleton-item variant="h1" style="width: 60%; margin-top: 8px" />
              <el-skeleton-item variant="text" style="width: 30%; margin-top: 8px" />
            </div>
          </div>
        </template>
      </el-skeleton>
      <template v-else>
        <StatCard
          v-for="item in stats"
          :key="item.key"
          :item="item"
        />
      </template>
    </div>

    <!-- 图表区 -->
    <div class="charts-grid mt-4">
      <SalesChart :data="salesData" title="销售趋势" />
      <CategoryChart :data="categoryData" title="商品分类占比" />
    </div>

    <!-- 店铺与交易提示 -->
    <div class="mt-4">
      <StoreTrade :data="storeTrade" />
    </div>

    <!-- 按钮级权限演示 -->
    <div class="app-card mt-4 p-4">
      <div class="panel-title mb-2">按钮级权限演示（v-permission 指令）</div>
      <p class="text-gray-400 text-13px mb-3">
        使用 admin（超级管理员，拥有全部权限）登录可看到全部按钮；
        使用 editor（运营人员）登录将看不到「删除」「配置权限」等受控按钮。
      </p>
      <el-button type="primary" v-permission="['dashboard:view']">
        查看（dashboard:view）
      </el-button>
      <el-button type="success" v-permission="['system:admin']">
        管理（system:admin）
      </el-button>
      <el-button type="warning" v-permission="['system:menu']">
        菜单（system:menu）
      </el-button>
      <el-button type="danger" v-permission="['system:role']">
        角色（system:role）
      </el-button>
    </div>
  </div>
</template>

<script setup>
/**
 * 仪表盘首页（对应课程：统计面板/骨架屏/数字滚动/图表/店铺交易/权限指令）
 */
import { ref, reactive, onMounted, onActivated } from 'vue'
import {
  getDashboardStats,
  getSalesTrend,
  getCategoryRatio,
  getStoreTrade
} from '@/api/dashboard'
import StatCard from './components/StatCard.vue'
import SalesChart from './components/SalesChart.vue'
import CategoryChart from './components/CategoryChart.vue'
import StoreTrade from './components/StoreTrade.vue'

defineOptions({ name: 'Dashboard' })

const loading = ref(true)
const stats = ref([])
const salesData = reactive({ months: [], sales: [], orders: [] })
const categoryData = ref([])
const storeTrade = reactive({ stores: [], tips: [] })

async function fetchData() {
  loading.value = true
  try {
    const [statsRes, salesRes, categoryRes, storeRes] = await Promise.all([
      getDashboardStats(),
      getSalesTrend(),
      getCategoryRatio(),
      getStoreTrade()
    ])
    stats.value = statsRes
    Object.assign(salesData, salesRes)
    categoryData.value = categoryRes
    Object.assign(storeTrade, storeRes)
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    loading.value = false
  }
}

// keep-alive 缓存时再次激活也刷新数据
onMounted(fetchData)
onActivated(() => {
  if (stats.value.length) fetchData()
})
</script>

<style scoped lang="scss">
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
