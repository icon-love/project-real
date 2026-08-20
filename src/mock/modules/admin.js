import db, { save, paginate, nextId } from '../db'
import { ok, fail, body, num, parseParams } from '../helpers'
import { svgAvatar } from '../placeholder'

export default function registerAdmin(mock) {
  mock.onGet('/api/admin/list').reply((config) => {
    const params = parseParams(config)
    let list = db.admins.slice().sort((a, b) => a.id - b.id)
    if (params.keyword) {
      list = list.filter(
        (i) =>
          i.username.includes(params.keyword) ||
          i.nickname.includes(params.keyword)
      )
    }
    if (params.status !== undefined && params.status !== '') {
      list = list.filter((i) => i.status === num(params.status, 1))
    }
    if (params.roleId) {
      list = list.filter((i) => i.roleId === num(params.roleId))
    }
    // 列表不返回密码
    const result = paginate(list, params.page, params.pageSize)
    return ok({ ...result, list: result.list.map(({ password, ...rest }) => rest) })
  })

  mock.onPut(/\/api\/admin\/status\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const user = db.admins.find((a) => a.id === id)
    if (!user) return fail('管理员不存在')
    const { status } = body(config)
    user.status = status === 0 ? 0 : 1
    save()
    return ok(null, '状态修改成功')
  })

  mock.onPost('/api/admin').reply((config) => {
    const data = body(config)
    if (!data.username || !data.password) return fail('请填写用户名和密码')
    if (db.admins.some((a) => a.username === data.username)) {
      return fail('用户名已存在')
    }
    const role = db.roles.find((r) => r.id === num(data.roleId))
    db.admins.push({
      id: nextId('admin'),
      username: data.username,
      password: data.password || '123456',
      nickname: data.nickname || data.username,
      roleId: num(data.roleId, 2),
      roleName: role ? role.name : '未分配',
      phone: data.phone || '',
      email: data.email || '',
      avatar: data.avatar || svgAvatar((data.nickname || data.username).slice(0, 1)),
      status: data.status === 0 ? 0 : 1,
      createTime: Date.now()
    })
    save()
    return ok(null, '新增成功')
  })

  mock.onPut('/api/admin').reply((config) => {
    const data = body(config)
    const user = db.admins.find((a) => a.id === num(data.id))
    if (!user) return fail('管理员不存在')
    if (
      db.admins.some((a) => a.username === data.username && a.id !== user.id)
    ) {
      return fail('用户名已存在')
    }
    const role = db.roles.find((r) => r.id === num(data.roleId))
    user.username = data.username
    user.nickname = data.nickname || data.username
    if (data.password) user.password = data.password
    user.roleId = num(data.roleId, user.roleId)
    user.roleName = role ? role.name : user.roleName
    user.phone = data.phone || ''
    user.email = data.email || ''
    user.avatar = data.avatar || user.avatar
    user.status = data.status === 0 ? 0 : 1
    save()
    return ok(null, '修改成功')
  })

  mock.onDelete(/\/api\/admin\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    if (id === 1) return fail('超级管理员不可删除')
    const index = db.admins.findIndex((a) => a.id === id)
    if (index === -1) return fail('管理员不存在')
    db.admins.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })
}
