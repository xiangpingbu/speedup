import { STORAGE_KEY } from './mutations'

const localStoragePlugin = store => {
  store.subscribe((mutation, { map }) => {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(map))
  })
}

export default [localStoragePlugin]
