import db, { save, paginate, nextId } from '../db'
import { ok, fail, body, num, parseParams } from '../helpers'
import { randomImage } from '../placeholder'

export default function registerGallery(mock) {
  // ================= 分类 =================
  mock.onGet('/api/gallery/category/list').reply((config) => {
    const params = parseParams(config)
    let list = db.galleryCategories.slice().sort((a, b) => a.sort - b.sort)
    if (params.name) {
      list = list.filter((i) => i.name.includes(params.name))
    }
    return ok(paginate(list, params.page, params.pageSize))
  })

  mock.onPost('/api/gallery/category').reply((config) => {
    const data = body(config)
    if (!data.name) return fail('请填写分类名称')
    if (db.galleryCategories.some((c) => c.name === data.name)) {
      return fail('分类名称已存在')
    }
    db.galleryCategories.push({
      id: nextId('galleryCategory'),
      name: data.name,
      sort: num(data.sort, 0),
      remark: data.remark || '',
      createTime: Date.now()
    })
    save()
    return ok(null, '新增成功')
  })

  mock.onPut('/api/gallery/category').reply((config) => {
    const data = body(config)
    const item = db.galleryCategories.find((c) => c.id === num(data.id))
    if (!item) return fail('分类不存在')
    if (
      db.galleryCategories.some(
        (c) => c.name === data.name && c.id !== item.id
      )
    ) {
      return fail('分类名称已存在')
    }
    item.name = data.name
    item.sort = num(data.sort, item.sort)
    item.remark = data.remark || ''
    save()
    return ok(null, '修改成功')
  })

  mock.onDelete(/\/api\/gallery\/category\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const index = db.galleryCategories.findIndex((c) => c.id === id)
    if (index === -1) return fail('分类不存在')
    if (db.images.some((img) => img.categoryId === id)) {
      return fail('该分类下存在图片，无法删除')
    }
    db.galleryCategories.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })

  // ================= 图片 =================
  mock.onGet('/api/gallery/list').reply((config) => {
    const params = parseParams(config)
    let list = db.images.slice().sort((a, b) => b.createTime - a.createTime)
    if (params.categoryId) {
      list = list.filter((i) => i.categoryId === num(params.categoryId))
    }
    if (params.name) {
      list = list.filter((i) => i.name.includes(params.name))
    }
    return ok(paginate(list, params.page, params.pageSize))
  })

  // 上传图片
  mock.onPost('/api/gallery/upload').reply((config) => {
    const form = config.data
    const files = form.getAll ? form.getAll('files') : []
    const categoryId = num(form.get ? form.get('categoryId') : 1, 1)
    if (!files.length) return fail('未选择文件')
    const records = files.map((file) => ({
      id: nextId('image'),
      categoryId,
      name: file.name ? file.name.replace(/\.[^.]+$/, '') : '未命名图片',
      url: randomImage('上传'),
      size: Math.max(1, Math.round((file.size || 100 * 1024) / 1024)),
      createTime: Date.now()
    }))
    db.images.push(...records)
    save()
    return ok(records, `成功上传 ${records.length} 张图片`)
  })

  // 删除图片
  mock.onDelete(/\/api\/gallery\/image\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const index = db.images.findIndex((img) => img.id === id)
    if (index === -1) return fail('图片不存在')
    db.images.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })

  // 重命名图片
  mock.onPut(/\/api\/gallery\/image\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const img = db.images.find((i) => i.id === id)
    if (!img) return fail('图片不存在')
    const { name } = body(config)
    if (!name) return fail('请输入图片名称')
    img.name = name
    save()
    return ok(null, '重命名成功')
  })
}
