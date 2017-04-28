import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
// import topBar from '@/components/TopBar'
// import lineChart from '@/components/lineChart'
import Home from '@/components/Home'
import DashBoard from '@/components/DashBoard'
import Page from '@/components/Page'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      component: Home
    },
    {
      path: '/hello',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/dash/',
      name: 'DashBoard',
      component: DashBoard
    },
    {
      path: '/dash/:id',
      name: 'Page',
      component: Page
    }
  ]
})
