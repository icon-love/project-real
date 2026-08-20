<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    :width="width"
    :close-on-click-modal="false"
    :append-to-body="appendToBody"
    destroy-on-close
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <!-- 表单内容（由父组件通过默认插槽提供） -->
    <slot />

    <!-- 底部按钮，可自定义 -->
    <template #footer>
      <slot name="footer">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirm">
          确 定
        </el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 通用弹框表单组件（对应课程：通用弹框表单组件封装）
 * 用法：
 *  <DialogForm v-model="visible" title="新增" :loading="submitting" @confirm="submitForm">
 *    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">...</el-form>
 *  </DialogForm>
 */
defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '提示' },
  width: { type: String, default: '520px' },
  loading: { type: Boolean, default: false },
  appendToBody: { type: Boolean, default: true }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

function handleCancel() {
  emit('update:modelValue', false)
}

function handleConfirm() {
  emit('confirm')
}
</script>
