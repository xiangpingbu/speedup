import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/pages/Home'
import Uploaded from '@/pages/Uploaded'
import Bar from '@/pages/Bar'
import Select from '@/pages/Select'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/',
    name: 'Home',
    component: Home
  }, {
    path: '/uploaded',
    name: 'Uploaded',
    component: Uploaded
  }, {
    path: '/bar',
    name: 'Bar',
    component: Bar
  }, {
    path: '/select',
    name: 'Select',
    component: Select
  }],
  // mode: 'history',
  scrollBehavior(to, from, savedPosition) {
    return {
      x: 0,
      y: 0
    }
  }
})
