export const STORAGE_KEY = 'charts'

// for testing
// if (navigator.userAgent.indexOf('PhantomJS') > -1) {
//   window.localStorage.clear()
// }

export const state = {
  // 只存在内存中，不本地存储，刷新重新获取
  urlCacheData: {},
  map: JSON.parse(window.localStorage.getItem(STORAGE_KEY) || '[]')
}

export const mutations = {
  saveId (state, {id}) {
    state.map.id = id
  },
  saveUrlCache (state, {url, data}) {
    state.urlCacheData[url] = data
  },
  saveData (state, {id, dataSet, variable, type}) {
    state.map.id.dataSet = dataSet
    state.map.id.variable = variable
    state.map.id.type = type
  }
}
