<template>
  <div class="store-trade">
    <!-- 店铺销售 -->
    <div class="store app-card p-4">
      <div class="panel-title mb-3">店铺销售 TOP</div>
      <el-table :data="data.stores" size="small" style="width: 100%">
        <el-table-column prop="name" label="店铺名称" />
        <el-table-column label="今日销售额">
          <template #default="{ row }">
            <span class="text-danger font-600">¥{{ thousand(row.sales) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="今日提现">
          <template #default="{ row }">
            <span class="text-success">¥{{ thousand(row.tips) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 交易提示 -->
    <div class="tips app-card p-4">
      <div class="panel-title mb-3">交易提示</div>
      <div class="tips-list">
        <div v-for="tip in data.tips" :key="tip.title" class="tip-item">
          <span class="tip-badge" :class="'is-' + (tip.type || 'info')">
            <el-icon :size="16">
              <WarningFilled v-if="tip.type === 'warning'" />
              <CircleCloseFilled v-else-if="tip.type === 'danger'" />
              <CircleCheckFilled v-else />
            </el-icon>
          </span>
          <div class="tip-info">
            <span class="tip-title">{{ tip.title }}</span>
            <span class="tip-value" :class="'is-' + (tip.type || 'info')">
              {{ tip.value }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 店铺和交易提示组件（对应课程：店铺和交易提示组件开发和交互）
 */
import { thousand } from '@/utils'

defineProps({
  data: {
    type: Object,
    default: () => ({ stores: [], tips: [] })
  }
})
</script>

<style scoped lang="scss">
.store-trade {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  .panel-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }

  .tips-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;

    .tip-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px;
      background: #f5f7fa;
      border-radius: 8px;

      .tip-badge {
        width: 34px;
        height: 34px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        background: #e4e7ed;

        &.is-warning {
          color: #e6a23c;
          background: #fdf6ec;
        }
        &.is-danger {
          color: #f56c6c;
          background: #fef0f0;
        }
        &.is-success {
          color: #67c23a;
          background: #f0f9eb;
        }
      }

      .tip-info {
        display: flex;
        flex-direction: column;
        gap: 2px;

        .tip-title {
          font-size: 12px;
          color: #909399;
        }

        .tip-value {
          font-size: 18px;
          font-weight: 600;
          color: #303133;

          &.is-warning { color: #e6a23c; }
          &.is-danger { color: #f56c6c; }
          &.is-success { color: #67c23a; }
        }
      }
    }
  }
}

@media (max-width: 992px) {
  .store-trade {
    grid-template-columns: 1fr;
  }
}
</style>
