import db, { save, paginate, nextId } from '../db'
import { ok, fail, body, num, parseParams } from '../helpers'

// 商品分类（演示用固定分类）
const categories = ['手机数码', '服饰鞋包', '食品生鲜', '家居生活', '其他']

export default function registerProduct(mock) {
  // ---------- 商品分类 ----------
  mock.onGet('/api/product/category/list').reply(() => ok(categories))

  // ---------- 商品列表（分页 + 搜索） ----------
  mock.onGet('/api/product/list').reply((config) => {
    const params = parseParams(config)
    let list = db.products.slice().sort((a, b) => b.id - a.id)
    if (params.name) {
      list = list.filter((i) => i.name.includes(params.name))
    }
    if (params.category) {
      list = list.filter((i) => i.category === params.category)
    }
    if (params.status !== undefined && params.status !== '') {
      list = list.filter((i) => i.status === num(params.status, 1))
    }
    return ok(paginate(list, params.page, params.pageSize))
  })

  // ---------- 修改状态（上架/下架） ----------
  mock.onPut(/\/api\/product\/status\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const p = db.products.find((i) => i.id === id)
    if (!p) return fail('商品不存在')
    const { status } = body(config)
    p.status = status === 0 ? 0 : 1
    save()
    return ok(null, '状态修改成功')
  })

  // ---------- 新增商品 ----------
  mock.onPost('/api/product').reply((config) => {
    const data = body(config)
    if (!data.name) return fail('请输入商品名称')
    if (db.products.some((p) => p.name === data.name)) {
      return fail('商品名称已存在')
    }
    db.products.push({
      id: nextId('product'),
      name: data.name,
      category: data.category || '其他',
      price: num(data.price, 0),
      stock: num(data.stock, 0),
      image: data.image || '',
      status: data.status === 0 ? 0 : 1,
      description: data.description || '',
      createTime: Date.now()
    })
    save()
    return ok(null, '新增成功')
  })

  // ---------- 修改商品 ----------
  mock.onPut('/api/product').reply((config) => {
    const data = body(config)
    const p = db.products.find((i) => i.id === num(data.id))
    if (!p) return fail('商品不存在')
    if (db.products.some((i) => i.name === data.name && i.id !== p.id)) {
      return fail('商品名称已存在')
    }
    p.name = data.name
    p.category = data.category || p.category
    p.price = num(data.price, p.price)
    p.stock = num(data.stock, p.stock)
    p.image = data.image || p.image
    p.status = data.status === 0 ? 0 : 1
    p.description = data.description || ''
    save()
    return ok(null, '修改成功')
  })

  // ---------- 删除商品 ----------
  mock.onDelete(/\/api\/product\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const index = db.products.findIndex((p) => p.id === id)
    if (index === -1) return fail('商品不存在')
    db.products.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })
}
