import { STORAGE_KEY } from './mutations'

const localStoragePlugin = store => {
  store.subscribe((mutation, { charts }) => {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(charts))
  })
}

export default [localStoragePlugin]
