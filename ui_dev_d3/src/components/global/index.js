import Vue from 'vue'
import Loading from './Loading'
import SvgReload from './svg/Reload'
const components = {
  Loading,
  SvgReload
}
Object.keys(components).forEach(key => {
  Vue.component(key, components[key])
})
