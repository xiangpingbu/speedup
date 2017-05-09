import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/pages/Home'
import DashBoard from '@/pages/DashBoard'
import Page from '@/pages/Page'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      component: Home
    },
    {
      path: '/:model',
      name: 'DashBoard',
      component: DashBoard
    },
    {
      path: '/:model/:id',
      name: 'Page',
      component: Page
    }
  ]
})
