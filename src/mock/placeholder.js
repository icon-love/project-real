/**
 * 生成本地 SVG 占位图（data URI），
 * 避免依赖外网图片（picsum 等在国内网络不稳定）。
 */

const gradients = [
  ['#409EFF', '#36CFC9'],
  ['#67C23A', '#36CFC9'],
  ['#E6A23C', '#F56C6C'],
  ['#909399', '#409EFF'],
  ['#F56C6C', '#E6A23C'],
  ['#36CFC9', '#409EFF'],
  ['#a16bfe', '#36cfc9'],
  ['#ff9a9e', '#fad0c4']
]

function pickGradient() {
  return gradients[Math.floor(Math.random() * gradients.length)]
}

/**
 * 生成 SVG 图片 data URI
 * @param {string} text 图片上的文字
 * @param {number} width
 * @param {number} height
 */
export function svgImage(text = 'IMAGE', width = 400, height = 300) {
  const [c1, c2] = pickGradient()
  const safeText = String(text).replace(/[<>&"']/g, '')
  const svg =
    `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">` +
    `<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">` +
    `<stop offset="0" stop-color="${c1}"/><stop offset="1" stop-color="${c2}"/>` +
    `</linearGradient></defs>` +
    `<rect width="100%" height="100%" fill="url(#g)"/>` +
    `<text x="50%" y="50%" fill="rgba(255,255,255,0.92)" font-size="26" font-family="Arial, sans-serif" font-weight="bold" text-anchor="middle" dominant-baseline="middle">${safeText}</text>` +
    `</svg>`
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
}

/** 头像占位图 */
export function svgAvatar(text = 'U') {
  return svgImage(text.slice(0, 1), 200, 200)
}

/** 随机图片占位图 */
export function randomImage(text) {
  return svgImage(text || `IMG-${Math.floor(Math.random() * 1000)}`, 400, 300)
}
