import { ok } from '../helpers'

const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']

export default function registerDashboard(mock) {
  // 统计面板
  mock.onGet('/api/dashboard/stats').reply(() =>
    ok([
      {
        key: 'sales',
        name: '今日销售额',
        value: 126580,
        unit: '元',
        icon: 'Money',
        color: '#409EFF',
        trend: 12.5,
        desc: '较昨日'
      },
      {
        key: 'orders',
        name: '今日订单数',
        value: 8842,
        unit: '单',
        icon: 'ShoppingCart',
        color: '#67C23A',
        trend: -3.2,
        desc: '较昨日'
      },
      {
        key: 'products',
        name: '商品总数',
        value: 1203,
        unit: '件',
        icon: 'Goods',
        color: '#E6A23C',
        trend: 5.6,
        desc: '本月新增'
      },
      {
        key: 'users',
        name: '会员总数',
        value: 23345,
        unit: '人',
        icon: 'User',
        color: '#F56C6C',
        trend: 8.9,
        desc: '本月新增'
      }
    ])
  )

  // 销售趋势（折线图）
  mock.onGet('/api/dashboard/sales-trend').reply(() => {
    const sales = [820, 932, 901, 934, 1290, 1330, 1320, 1450, 1280, 1390, 1520, 1680]
    const orders = [620, 732, 701, 734, 890, 930, 920, 1050, 980, 1090, 1120, 1280]
    return ok({ months, sales, orders })
  })

  // 商品分类占比（饼图）
  mock.onGet('/api/dashboard/category-ratio').reply(() =>
    ok([
      { name: '手机数码', value: 335 },
      { name: '服饰鞋包', value: 548 },
      { name: '食品生鲜', value: 234 },
      { name: '家居生活', value: 310 },
      { name: '其他', value: 135 }
    ])
  )

  // 店铺与交易提示
  mock.onGet('/api/dashboard/store-trade').reply(() =>
    ok({
      stores: [
        { id: 1, name: '旗舰店', sales: 45820, tips: 3260 },
        { id: 2, name: '华东分店', sales: 31560, tips: 2140 },
        { id: 3, name: '华南分店', sales: 27410, tips: 1890 },
        { id: 4, name: '华北分店', sales: 19870, tips: 1230 }
      ],
      tips: [
        { title: '订单待发货', value: 26, type: 'warning' },
        { title: '售后待处理', value: 5, type: 'danger' },
        { title: '商品库存预警', value: 8, type: 'warning' },
        { title: '今日新增评论', value: 132, type: 'success' }
      ]
    })
  )
}
