import db, { save, nextId } from '../db'
import { ok, fail, body, num } from '../helpers'

export default function registerRole(mock) {
  mock.onGet('/api/role/list').reply(() => ok(db.roles))

  mock.onPost('/api/role').reply((config) => {
    const data = body(config)
    if (!data.name) return fail('请填写角色名称')
    if (db.roles.some((r) => r.name === data.name)) {
      return fail('角色名称已存在')
    }
    db.roles.push({
      id: nextId('role'),
      name: data.name,
      remark: data.remark || '',
      permissionIds: data.permissionIds || [],
      status: data.status === 0 ? 0 : 1,
      createTime: Date.now()
    })
    save()
    return ok(null, '新增成功')
  })

  mock.onPut('/api/role').reply((config) => {
    const data = body(config)
    const role = db.roles.find((r) => r.id === num(data.id))
    if (!role) return fail('角色不存在')
    // 仅更新提供的字段（支持仅改状态）
    if (data.name !== undefined) {
      if (!data.name) return fail('请填写角色名称')
      if (db.roles.some((r) => r.name === data.name && r.id !== role.id)) {
        return fail('角色名称已存在')
      }
      role.name = data.name
    }
    if (data.remark !== undefined) role.remark = data.remark
    if (data.status !== undefined) role.status = data.status === 0 ? 0 : 1
    save()
    return ok(null, '修改成功')
  })

  mock.onDelete(/\/api\/role\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    if (id === 1) return fail('超级管理员角色不可删除')
    const index = db.roles.findIndex((r) => r.id === id)
    if (index === -1) return fail('角色不存在')
    if (db.admins.some((a) => a.roleId === id)) {
      return fail('该角色下存在管理员，无法删除')
    }
    db.roles.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })

  // 获取角色已分配的权限 id
  mock.onGet(/\/api\/role\/\d+\/permission-ids$/).reply((config) => {
    // 从 /api/role/{id}/permission-ids 中取 id（倒数第 2 段）
    const parts = config.url.split('/').filter(Boolean)
    const id = num(parts[parts.length - 2])
    const role = db.roles.find((r) => r.id === id)
    if (!role) return fail('角色不存在')
    return ok(role.permissionIds || [])
  })

  // 配置角色权限
  mock.onPut(/\/api\/role\/\d+\/permission$/).reply((config) => {
    const parts = config.url.split('/').filter(Boolean)
    const id = num(parts[parts.length - 2])
    const role = db.roles.find((r) => r.id === id)
    if (!role) return fail('角色不存在')
    const { permissionIds } = body(config)
    role.permissionIds = Array.isArray(permissionIds) ? permissionIds : []
    save()
    return ok(null, '权限配置成功')
  })
}
