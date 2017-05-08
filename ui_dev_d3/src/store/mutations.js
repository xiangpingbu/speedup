export const STORAGE_KEY = 'charts'

// for testing
// if (navigator.userAgent.indexOf('PhantomJS') > -1) {
//   window.localStorage.clear()
// }

export const state = {
  charts: JSON.parse(window.localStorage.getItem(STORAGE_KEY) || '[]')
}

export const mutations = {
  saveId (state, {id}) {
    state.charts.id = id
  },

  saveData (state, {id, dataSet, variable, type}) {
    state.charts.id.dataSet = dataSet
    state.charts.id.variable = variable
    state.charts.id.type = type
  }
}
