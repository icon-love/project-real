import db, { save, paginate, nextId } from '../db'
import { ok, fail, body, num, parseParams } from '../helpers'

export default function registerNotice(mock) {
  mock.onGet('/api/notice/list').reply((config) => {
    const params = parseParams(config)
    let list = db.notices.slice().sort((a, b) => b.createTime - a.createTime)
    if (params.keyword) {
      list = list.filter(
        (i) => i.title.includes(params.keyword) || i.author.includes(params.keyword)
      )
    }
    return ok(paginate(list, params.page, params.pageSize))
  })

  mock.onPost('/api/notice').reply((config) => {
    const data = body(config)
    if (!data.title) return fail('请填写公告标题')
    db.notices.push({
      id: nextId('notice'),
      title: data.title,
      content: data.content || '',
      author: data.author || '超级管理员',
      status: 1,
      createTime: Date.now()
    })
    save()
    return ok(null, '新增成功')
  })

  mock.onPut('/api/notice').reply((config) => {
    const data = body(config)
    const item = db.notices.find((n) => n.id === num(data.id))
    if (!item) return fail('公告不存在')
    item.title = data.title
    item.content = data.content || ''
    item.author = data.author || item.author
    save()
    return ok(null, '修改成功')
  })

  mock.onDelete(/\/api\/notice\/\d+$/).reply((config) => {
    const id = num(config.url.split('/').pop())
    const index = db.notices.findIndex((n) => n.id === id)
    if (index === -1) return fail('公告不存在')
    db.notices.splice(index, 1)
    save()
    return ok(null, '删除成功')
  })
}
