import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 新增 / 编辑 弹窗表单封装（对应课程：组合式API特性封装-新增修改）
 *
 * @param {Object} options
 *  - addApi       新增接口 (data) => Promise
 *  - updateApi    修改接口 (data) => Promise
 *  - defaultForm  表单默认值
 *  - rules        校验规则
 *  - afterSubmit  提交成功回调（如刷新列表）
 */
export function useForm(options = {}) {
  const { addApi, updateApi, defaultForm = {}, rules = {}, afterSubmit } = options

  const dialogVisible = ref(false)
  const dialogType = ref('add') // add | edit
  const submitting = ref(false)
  const formRef = ref(null)
  const form = reactive({ ...defaultForm })

  function resetForm() {
    Object.keys(form).forEach((key) => delete form[key])
    Object.assign(form, JSON.parse(JSON.stringify(defaultForm)))
    formRef.value?.clearValidate()
  }

  function openAdd() {
    dialogType.value = 'add'
    resetForm()
    dialogVisible.value = true
  }

  function openEdit(row) {
    dialogType.value = 'edit'
    resetForm()
    Object.assign(form, JSON.parse(JSON.stringify(row)))
    dialogVisible.value = true
  }

  async function submitForm() {
    if (formRef.value) {
      try {
        await formRef.value.validate()
      } catch (e) {
        return
      }
    }
    submitting.value = true
    try {
      if (dialogType.value === 'add') {
        await addApi({ ...form })
        ElMessage.success('新增成功')
      } else {
        await updateApi({ ...form })
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      afterSubmit && afterSubmit()
    } catch (e) {
      // 错误已在拦截器中提示
    } finally {
      submitting.value = false
    }
  }

  return {
    dialogVisible,
    dialogType,
    submitting,
    formRef,
    form,
    rules,
    openAdd,
    openEdit,
    submitForm,
    resetForm
  }
}
