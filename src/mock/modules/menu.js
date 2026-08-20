import db, { save, nextId } from '../db'
import { ok, fail, body, num } from '../helpers'

function findNode(nodes, id) {
  for (const node of nodes) {
    if (node.id === id) return node
    if (node.children && node.children.length) {
      const found = findNode(node.children, id)
      if (found) return found
    }
  }
  return null
}

export default function registerMenu(mock) {
  // ---------- 菜单树 ----------
  mock.onGet('/api/menu/list').reply(() => ok(db.menus))

  // ---------- 新增菜单 ----------
  mock.onPost('/api/menu').reply((config) => {
    const data = body(config)
    if (!data.title) return fail('请填写菜单名称')
    const node = {
      id: nextId('menu'),
      parentId: num(data.parentId, 0),
      title: data.title,
      name: data.name || 'Menu' + Date.now(),
      path: data.path || '',
      component: data.component || '',
      icon: data.icon || 'Menu',
      sort: num(data.sort, 0),
      visible: data.visible !== false,
      status: data.status === 0 ? 0 : 1,
      perms: data.perms || '',
      children: []
    }
    if (node.parentId === 0) {
      db.menus.push(node)
    } else {
      const parent = findNode(db.menus, node.parentId)
      if (!parent) return fail('父级菜单不存在')
      parent.children = parent.children || []
      parent.children.push(node)
    }
    save()
    return ok(null, '新增成功')
  })

  // ---------- 修改菜单 ----------
  mock.onPut('/api/menu').reply((config) => {
    const data = body(config)
    const node = findNode(db.menus, num(data.id))
    if (!node) return fail('菜单不存在')
    Object.assign(node, {
      title: data.title,
      name: data.name || node.name,
      path: data.path,
      component: data.component,
      icon: data.icon,
      sort: num(data.sort, node.sort),
      visible: data.visible !== undefined ? data.visible : node.visible,
      status: data.status !== undefined ? data.status : node.status,
      perms: data.perms || ''
    })
    save()
    return ok(null, '修改成功')
  })

  // ---------- 删除菜单 ----------
  mock.onDelete(/\/api\/menu\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const node = findNode(db.menus, id)
    if (!node) return fail('菜单不存在')
    if (node.children && node.children.length) {
      return fail('请先删除该菜单下的子菜单')
    }
    removeNode(db.menus, id)
    save()
    return ok(null, '删除成功')
  })

  // ---------- 修改状态 ----------
  mock.onPut(/\/api\/menu\/status\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const node = findNode(db.menus, id)
    if (!node) return fail('菜单不存在')
    const { status } = body(config)
    node.status = status === 0 ? 0 : 1
    save()
    return ok(null, '修改成功')
  })
}

function removeNode(nodes, id) {
  for (let i = 0; i < nodes.length; i++) {
    if (nodes[i].id === id) {
      nodes.splice(i, 1)
      return true
    }
    if (nodes[i].children && nodes[i].children.length) {
      if (removeNode(nodes[i].children, id)) return true
    }
  }
  return false
}
