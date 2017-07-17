import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
import Page from '@/pages/Page'

// import SideBar from '@/components/sidebar/Sidebar'
import source from '@/components/dataSource/SourceBody'
import modal from '@/components/dataSource/SourceModal'
import side from '@/components/component/body/Body'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/test',
      name: 'Page',
      component: Page
      //
      // path: '/side',
      // name: 'Sidebar',
      // component: SideBar
    },
    {
      path: '/source',
      name: 'source',
      component: source
    },
    {
      path: '/modal',
      name: 'modal',
      component: modal
    },
    {
      path: '/side',
      name: 'side',
      component: side
    }
  ]
})
