export const STORAGE_KEY = 'charts'

// for testing
// if (navigator.userAgent.indexOf('PhantomJS') > -1) {
//   window.localStorage.clear()
// }

export const state = {
  // 只存在内存中，不本地存储，刷新重新获取
  urlCacheData: {},
  charts: JSON.parse(window.localStorage.getItem(STORAGE_KEY) || '[]')
}

export const mutations = {
  saveId (state, {id}) {
    state.charts.id = id
  },
  saveUrlCache (state, {url, data}) {
    state.urlCacheData[url] = data
  },
  saveData (state, {id, dataSet, variable, type}) {
    state.charts.id.dataSet = dataSet
    state.charts.id.variable = variable
    state.charts.id.type = type
  }
}
