<template>
  <DialogForm
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增角色' : '修改角色'"
    width="480px"
    :loading="submitting"
    @confirm="submitForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入角色名称" maxlength="20" />
      </el-form-item>
      <el-form-item label="角色备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          maxlength="100"
          show-word-limit
          placeholder="选填"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 角色编辑弹窗（对应课程：复用代码快速实现角色模块开发）
 */
import DialogForm from '@/components/DialogForm.vue'
import { useForm } from '@/composables/useForm'
import { addRole, updateRole } from '@/api/role'

const emit = defineEmits(['success'])

const { dialogVisible, dialogType, submitting, formRef, form, rules, openAdd, openEdit, submitForm } =
  useForm({
    addApi: addRole,
    updateApi: updateRole,
    defaultForm: { id: null, name: '', remark: '', status: 1 },
    rules: {
      name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
    },
    afterSubmit: () => emit('success')
  })

defineExpose({ openAdd, openEdit })
</script>
