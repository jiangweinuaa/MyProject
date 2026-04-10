/**
 * 日期工具函数
 * 统一的日期处理逻辑，避免各页面重复定义
 */

/**
 * 获取今天日期 YYYY-MM-DD
 */
export function getToday() {
  return new Date().toISOString().split('T')[0]
}

/**
 * 获取当月 1 号 YYYY-MM-DD
 */
export function getFirstDayOfMonth() {
  const date = new Date()
  date.setDate(1)
  return date.toISOString().split('T')[0]
}

/**
 * 获取默认日期范围：当月 1 号 到 今天
 */
export function getDefaultDateRange() {
  return {
    startDate: getFirstDayOfMonth(),
    endDate: getToday()
  }
}

/**
 * 格式化日期 YYYY-MM-DD -> YYYYMMDD
 */
export function formatDateToDB(dateStr) {
  if (!dateStr) return ''
  return dateStr.replace(/-/g, '')
}

/**
 * 格式化日期 YYYYMMDD -> YYYY-MM-DD
 */
export function formatDateToDisplay(dateStr) {
  if (!dateStr || dateStr.length !== 8) return dateStr
  return `${dateStr.substring(0, 4)}-${dateStr.substring(4, 6)}-${dateStr.substring(6, 8)}`
}
