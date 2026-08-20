import db, { save } from '../db'
import { ok, fail, body } from '../helpers'

/** 根据角色权限过滤菜单树 */
function filterMenus(nodes, ids) {
  if (!Array.isArray(nodes)) return []
  const result = []
  for (const node of nodes) {
    const children = filterMenus(node.children || [], ids)
    const matched = ids.includes(node.id)
    if (matched || children.length > 0) {
      result.push({
        ...node,
        children: matched ? node.children || [] : children
      })
    }
  }
  return result
}

export default function registerLogin(mock) {
  // ---------- 登录 ----------
  mock.onPost('/api/login').reply((config) => {
    const { username, password } = body(config)
    const user = db.admins.find(
      (a) => a.username === username && a.password === password
    )
    if (!user) {
      return fail('用户名或密码错误')
    }
    if (user.status === 0) {
      return fail('该账号已被禁用，请联系管理员')
    }
    db.currentUserId = user.id
    save()
    return ok({ token: `mock-token-${user.id}-${Date.now().toString(36)}` }, '登录成功')
  })

  // ---------- 退出登录 ----------
  mock.onPost('/api/logout').reply(() => {
    db.currentUserId = 0
    save()
    return ok(null, '退出成功')
  })

  // ---------- 获取用户信息（含菜单） ----------
  mock.onGet('/api/user/info').reply(() => {
    const user = db.admins.find((a) => a.id === db.currentUserId) || db.admins[0]
    const role = db.roles.find((r) => r.id === user.roleId)
    const menus = filterMenus(db.menus, role ? role.permissionIds : [1, 2, 3, 4, 5, 6, 7])

    // 收集按钮级权限标识
    const collectPerms = (nodes, acc = []) => {
      nodes.forEach((n) => {
        if (n.perms) acc.push(n.perms)
        if (n.children && n.children.length) collectPerms(n.children, acc)
      })
      return acc
    }
    const perms = collectPerms(menus)
    if (user.roleId === 1) perms.push('*')

    return ok({
      user: {
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        avatar: user.avatar,
        roleId: user.roleId,
        roleName: user.roleName,
        email: user.email,
        phone: user.phone,
        perms
      },
      menus
    })
  })

  // ---------- 修改密码 ----------
  mock.onPut('/api/user/password').reply((config) => {
    const { oldPassword, newPassword } = body(config)
    const user = db.admins.find((a) => a.id === db.currentUserId) || db.admins[0]
    if (oldPassword !== user.password) {
      return fail('原密码错误')
    }
    if (newPassword === user.password) {
      return fail('新密码不能与原密码相同')
    }
    user.password = newPassword
    save()
    return ok(null, '密码修改成功')
  })
}
