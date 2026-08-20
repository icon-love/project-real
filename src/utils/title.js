/**
 * 动态页面标题
 */
export function getPageTitle(pageTitle) {
  const baseTitle = '后台管理系统'
  if (pageTitle) {
    return `${pageTitle} - ${baseTitle}`
  }
  return baseTitle
}
