import Vue from 'vue'
import Sidebar from './Sidebar'
// bar
import BarHead from './bar/Head'
import BarItem from './bar/Item'

const components = {
    Sidebar,
    BarHead,
    BarItem
}

Object.keys(components).forEach(key => {
    Vue.component(key, components[key])
})
