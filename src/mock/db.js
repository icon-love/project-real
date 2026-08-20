import { svgAvatar, randomImage } from './placeholder'

/**
 * 内存数据库：用 localStorage 持久化，模拟真实后端数据。
 * 浏览器刷新后数据仍保留，可在「菜单权限」页重置或通过 localStorage 清理。
 */
const DB_KEY = 'admin_mock_db_v1'

function seed() {
  const now = Date.now()
  const day = 24 * 60 * 60 * 1000
  const time = (offset) => new Date(now - offset * day).getTime()

  // ---------- 菜单权限树 ----------
  const menus = [
    {
      id: 1,
      parentId: 0,
      title: '仪表盘',
      name: 'Dashboard',
      path: 'dashboard',
      component: 'dashboard/index',
      icon: 'Odometer',
      sort: 1,
      visible: true,
      status: 1,
      perms: 'dashboard:view',
      children: []
    },
    {
      id: 2,
      parentId: 0,
      title: '图库管理',
      name: 'Gallery',
      path: 'gallery',
      component: 'gallery/index',
      icon: 'Picture',
      sort: 2,
      visible: true,
      status: 1,
      perms: 'gallery:view',
      children: []
    },
    {
      id: 3,
      parentId: 0,
      title: '公告管理',
      name: 'Notice',
      path: 'notice',
      component: 'notice/index',
      icon: 'Bell',
      sort: 3,
      visible: true,
      status: 1,
      perms: 'notice:view',
      children: []
    },
    {
      id: 4,
      parentId: 0,
      title: '系统管理',
      name: 'System',
      path: 'system',
      component: '',
      icon: 'Setting',
      sort: 5,
      visible: true,
      status: 1,
      perms: '',
      children: [
        {
          id: 5,
          parentId: 4,
          title: '管理员管理',
          name: 'Admin',
          path: 'admin',
          component: 'admin/index',
          icon: 'User',
          sort: 1,
          visible: true,
          status: 1,
          perms: 'system:admin',
          children: []
        },
        {
          id: 6,
          parentId: 4,
          title: '菜单权限',
          name: 'Menu',
          path: 'menu',
          component: 'menu/index',
          icon: 'Menu',
          sort: 2,
          visible: true,
          status: 1,
          perms: 'system:menu',
          children: []
        },
        {
          id: 7,
          parentId: 4,
          title: '角色管理',
          name: 'Role',
          path: 'role',
          component: 'role/index',
          icon: 'Avatar',
          sort: 3,
          visible: true,
          status: 1,
          perms: 'system:role',
          children: []
        }
      ]
    },
    {
      id: 8,
      parentId: 0,
      title: '商品管理',
      name: 'Product',
      path: 'product',
      component: 'product/index',
      icon: 'Goods',
      sort: 4,
      visible: true,
      status: 1,
      perms: 'product:view',
      children: []
    }
  ]

  // ---------- 角色 ----------
  const roles = [
    {
      id: 1,
      name: '超级管理员',
      remark: '拥有系统全部权限',
      permissionIds: [1, 2, 3, 4, 5, 6, 7, 8],
      status: 1,
      createTime: time(90)
    },
    {
      id: 2,
      name: '运营人员',
      remark: '负责内容维护（仪表盘/图库/公告/商品）',
      permissionIds: [1, 2, 3, 8],
      status: 1,
      createTime: time(60)
    },
    {
      id: 3,
      name: '访客',
      remark: '仅可查看仪表盘',
      permissionIds: [1],
      status: 1,
      createTime: time(30)
    }
  ]

  // ---------- 管理员 ----------
  const admins = [
    {
      id: 1,
      username: 'admin',
      password: '123456',
      nickname: '超级管理员',
      roleId: 1,
      roleName: '超级管理员',
      phone: '13800000001',
      email: 'admin@example.com',
      avatar: svgAvatar('管'),
      status: 1,
      createTime: time(90)
    },
    {
      id: 2,
      username: 'editor',
      password: '123456',
      nickname: '运营小李',
      roleId: 2,
      roleName: '运营人员',
      phone: '13800000002',
      email: 'editor@example.com',
      avatar: svgAvatar('李'),
      status: 1,
      createTime: time(60)
    },
    {
      id: 3,
      username: 'guest',
      password: '123456',
      nickname: '访客小王',
      roleId: 3,
      roleName: '访客',
      phone: '13800000003',
      email: 'guest@example.com',
      avatar: svgAvatar('王'),
      status: 0,
      createTime: time(30)
    }
  ]

  // ---------- 图库分类 ----------
  const galleryCategories = [
    { id: 1, name: '首页轮播', sort: 1, remark: '首页轮播图', createTime: time(50) },
    { id: 2, name: '商品图片', sort: 2, remark: '', createTime: time(40) },
    { id: 3, name: '活动宣传', sort: 3, remark: '', createTime: time(30) },
    { id: 4, name: '其他', sort: 4, remark: '', createTime: time(20) }
  ]

  // ---------- 图片 ----------
  const images = []
  const imgNames = [
    'banner-home', 'product-phone', 'product-laptop', 'activity-summer',
    'banner-spring', 'product-shoes', 'activity-618', 'product-bag',
    'banner-winter', 'product-watch', 'activity-newyear', 'product-hat',
    'banner-news', 'product-cup', 'activity-mid', 'product-keyboard'
  ]
  for (let i = 1; i <= 32; i++) {
    images.push({
      id: i,
      categoryId: (i % 4) + 1,
      name: imgNames[i % imgNames.length] + '-' + String(i).padStart(2, '0'),
      url: randomImage('IMG ' + String(i).padStart(2, '0')),
      size: Math.floor(Math.random() * 480) + 40, // KB
      createTime: time(Math.floor(Math.random() * 30))
    })
  }

  // ---------- 公告 ----------
  const notices = []
  const noticeData = [
    ['系统将于本周六凌晨进行升级维护', '为保证系统稳定运行，计划于本周六 00:00-02:00 进行升级维护，期间部分功能可能不可用，请提前保存工作内容。'],
    ['「618 大促」活动物料提交提醒', '请各运营同学于本周五前完成 618 大促活动页面的图片与文案物料提交，逾期将无法参与资源位排期。'],
    ['关于新增图片批量上传功能的公告', '图库模块现已支持批量上传，单次最多 20 张，支持 jpg/png/webp 格式，单张不超过 5MB。'],
    ['五一劳动节放假安排', '五一假期为 5.1 - 5.5，放假期间请保持电话畅通，如有紧急问题请联系值班人员。'],
    ['新版后台登录安全策略上线', '为保障账号安全，现已启用登录失败锁定策略：连续输错 5 次将锁定 30 分钟。'],
    ['数据看板指标口径调整说明', '自本月起，「今日销售额」统计口径调整为含运费实付金额，与财务报表保持一致。'],
    ['角色权限配置功能上线公告', '系统管理新增「角色管理」，可灵活为不同角色分配菜单权限，实现精细化权限控制。'],
    ['关于清理历史公告的通知', '为保证公告列表整洁，系统将每季度自动归档 3 个月前的历史公告，请及时查阅。'],
    ['新版图标库更新说明', '菜单图标库已更新至最新版，新增 30+ 常用图标，可在「菜单权限」中为菜单选择图标。'],
    ['移动端适配说明', '后台系统已对移动端进行基础适配，小屏设备可正常浏览数据，推荐使用桌面浏览器获得最佳体验。']
  ]
  for (let i = 1; i <= noticeData.length; i++) {
    notices.push({
      id: i,
      title: noticeData[i - 1][0],
      content: noticeData[i - 1][1],
      author: i % 2 === 0 ? '运营小李' : '超级管理员',
      status: 1,
      createTime: time(i * 3)
    })
  }

  // ---------- 商品 ----------
  // 商品图片直接引用图库 images 中的图片 url
  const products = [
    {
      id: 1,
      name: '智能手机 X1',
      category: '手机数码',
      price: 2999,
      stock: 200,
      image: images[1].url,
      status: 1,
      description: '6.7 英寸高清全面屏，双卡双待，5000mAh 大电池长续航。',
      createTime: time(28)
    },
    {
      id: 2,
      name: '轻薄笔记本 Pro',
      category: '手机数码',
      price: 5999,
      stock: 80,
      image: images[2].url,
      status: 1,
      description: '14 英寸 2.8K 屏，16G 内存 + 512G SSD，轻薄便携，性能强劲。',
      createTime: time(26)
    },
    {
      id: 3,
      name: '轻量运动跑鞋',
      category: '服饰鞋包',
      price: 399,
      stock: 500,
      image: images[5].url,
      status: 1,
      description: '轻量缓震，透气网面，日常跑步通勤皆宜。',
      createTime: time(24)
    },
    {
      id: 4,
      name: '商务双肩包',
      category: '服饰鞋包',
      price: 299,
      stock: 300,
      image: images[7].url,
      status: 1,
      description: '大容量多隔层，防泼水面料，独立电脑仓保护。',
      createTime: time(22)
    },
    {
      id: 5,
      name: '智能运动手表',
      category: '手机数码',
      price: 1299,
      stock: 150,
      image: images[9].url,
      status: 1,
      description: '血氧心率监测，50 米防水，14 天超长续航。',
      createTime: time(20)
    },
    {
      id: 6,
      name: '机械键盘 87 键',
      category: '手机数码',
      price: 499,
      stock: 260,
      image: images[11].url,
      status: 1,
      description: '87 键热插拔，PBT 键帽，RGB 背光，手感出色。',
      createTime: time(18)
    },
    {
      id: 7,
      name: '鲜切水果礼盒',
      category: '食品生鲜',
      price: 129,
      stock: 1000,
      image: images[3].url,
      status: 1,
      description: '当季时令鲜果组合，冷链配送，新鲜直达。',
      createTime: time(15)
    },
    {
      id: 8,
      name: '北欧简约护眼台灯',
      category: '家居生活',
      price: 189,
      stock: 400,
      image: images[0].url,
      status: 1,
      description: '三档调光，护眼无频闪，简约百搭。',
      createTime: time(12)
    }
  ]

  return {
    menus,
    roles,
    admins,
    galleryCategories,
    images,
    notices,
    products,
    currentUserId: 1,
    nextId: {
      menu: 100,
      role: 100,
      admin: 100,
      galleryCategory: 100,
      image: 100,
      notice: 100,
      product: 100
    }
  }
}

function load() {
  try {
    const raw = localStorage.getItem(DB_KEY)
    if (raw) {
      const data = JSON.parse(raw)
      if (data && data.menus && data.admins) {
        // 旧数据迁移：为已持久化的数据补全新版本字段，避免重置用户数据
        migrate(data)
        return data
      }
    }
  } catch (e) {
    /* ignore */
  }
  return seed()
}

/**
 * 迁移：向 localStorage 中已有的旧数据补全新增模块（商品数据/商品菜单/角色权限）
 * 注意：此处不能用模块级 save()（db 尚未初始化），直接写回 localStorage
 */
function migrate(data) {
  const fresh = seed()
  if (!Array.isArray(data.products)) {
    data.products = fresh.products
  }
  const prodMenu = fresh.menus.find((m) => m.name === 'Product')
  if (prodMenu && !data.menus.some((m) => m.name === 'Product')) {
    data.menus.push(prodMenu)
  }
  if (prodMenu && Array.isArray(data.roles)) {
    ;[1, 2].forEach((roleId) => {
      const role = data.roles.find((r) => r.id === roleId)
      if (role && !role.permissionIds.includes(prodMenu.id)) {
        role.permissionIds.push(prodMenu.id)
      }
    })
  }
  if (data.nextId && data.nextId.product === undefined) {
    data.nextId.product = 100
  }
  try {
    localStorage.setItem(DB_KEY, JSON.stringify(data))
  } catch (e) {
    /* ignore */
  }
}

const db = load()

export function save() {
  try {
    localStorage.setItem(DB_KEY, JSON.stringify(db))
  } catch (e) {
    /* ignore */
  }
}

/** 重置数据库（供「菜单权限」页调试使用） */
export function resetDb() {
  Object.keys(db).forEach((k) => delete db[k])
  Object.assign(db, seed())
  save()
}

/** 分页 */
export function paginate(list, page = 1, pageSize = 10) {
  const p = Number(page) || 1
  const size = Number(pageSize) || 10
  const start = (p - 1) * size
  return {
    list: list.slice(start, start + size),
    total: list.length
  }
}

/** 自增 id */
export function nextId(key) {
  db.nextId[key] = (db.nextId[key] || 100) + 1
  return db.nextId[key]
}

export default db
