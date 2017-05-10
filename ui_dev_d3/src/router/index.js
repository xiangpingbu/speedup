import Vue from 'vue'
import Router from 'vue-router'
import DashBoard from '@/pages/DashBoard'
import Page from '@/pages/Page'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashBoard
    },
    {
      path: '/model/:id',
      name: 'page',
      component: Page
    }
  ]
})
