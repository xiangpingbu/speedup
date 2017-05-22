import Vue from 'vue'
import Sidebar from './Sidebar'

const components = {
    Sidebar
}

Object.keys(components).forEach(key => {
    Vue.component(key, components[key])
})
