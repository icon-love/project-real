import * as Icons from '@element-plus/icons-vue'

/**
 * 按名称解析 Element Plus 图标组件，返回组件对象而非字符串。
 *
 * 为什么必须返回组件对象：
 * Vue 3 中 <component :is="'SomeName'"> 走 resolveDynamicComponent，
 * 当字符串与【当前渲染组件自身的 name】相同时，会优先解析为组件自引用
 * （递归组件机制），而不是全局注册的同名组件。
 *
 * 典型崩溃场景：菜单管理页组件 name 为 'Menu'，菜单树节点 icon 也为 'Menu'，
 * 模板里 <component :is="row.icon || 'Menu'"> 会把 'Menu' 解析成【菜单页组件自身】，
 * 导致页面无限递归渲染、DOM 爆炸、浏览器标签页崩溃（Target crashed）。
 *
 * 传入组件对象即可彻底绕过字符串解析，从根本上杜绝此类崩溃。
 */
export function resolveIcon(name) {
  return Icons[name] || Icons.Menu
}
