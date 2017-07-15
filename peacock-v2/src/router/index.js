import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
<<<<<<< HEAD
import Page from '@/pages/Page'
=======
import SideBar from '@/components/sidebar/Sidebar'
import source from '@/components/dataSource/SourceBody'
>>>>>>> upstream/dev-lifeng-0711-peacock-v2

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Hello',
      component: Hello
    },
    {
<<<<<<< HEAD
      path: '/test',
      name: 'Page',
      component: Page
=======
      path: '/side',
      name: 'Sidebar',
      component: SideBar
    },
    {
      path: '/source',
      name: 'source',
      component: source
>>>>>>> upstream/dev-lifeng-0711-peacock-v2
    }
  ]
})
