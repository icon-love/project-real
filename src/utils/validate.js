/**
 * 通用表单校验规则
 */

export const rules = {
  required: { required: true, message: '此项不能为空', trigger: 'blur' },
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { max: 30, message: '长度不能超过 30 个字符', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' }
  ],
  url: [{ required: true, message: '请填写地址', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '手机号格式不正确',
      trigger: 'blur'
    }
  ],
  number: [{ required: true, message: '请输入数字', trigger: 'blur' }]
}
