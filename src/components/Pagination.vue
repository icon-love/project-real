<template>
  <div class="pagination-wrapper flex-between">
    <span class="text-gray-400 text-13px">共 {{ total }} 条</span>
    <el-pagination
      :current-page="page"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 30, 50]"
      layout="sizes, prev, pager, next, jumper"
      background
      @current-change="(p) => $emit('page-change', p)"
      @size-change="(s) => $emit('size-change', s)"
    />
  </div>
</template>

<script setup>
defineProps({
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 }
})

defineEmits(['page-change', 'size-change'])
</script>
