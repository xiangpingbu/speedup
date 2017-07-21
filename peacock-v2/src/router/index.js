import Vue from 'vue'
import Router from 'vue-router'
// homepage
import HomePage from '@/pages/HomePage'
// project
import Project from '@/pages/Project'
import ProjectInner from '@/pages/project/Inner'

// project info
import Info from '@/pages/project/Info'

import ProjectList from '@/pages/project/ProjectList'
// project data
import DataPreview from '@/pages/project/data/Preview'
// import DataSource from '@/pages/project/data/Source'
import DataSource from '@/pages/project/data/Source/SourceBody'

// project train
import TrainExperiment from '@/pages/project/train/Experiment'
import TrainResult from '@/pages/project/train/Result'
// project analytics
import AnalyticsFeature from '@/pages/project/analytics/Feature'
import AnalyticsInsight from '@/pages/project/analytics/Insight'
import AnalyticsPredict from '@/pages/project/analytics/Predict'

// project share

// import SideBar from '@/components/sidebar/Sidebar'
// import source from '@/components/dataSource/SourceBody'
// import modal from '@/components/dataSource/SourceModal'
// import side from '@/components/component/body/Body'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'homepage',
      component: HomePage
    },
    {
      path: '/app',
      name: 'app',
      component: Project,
      // 当/app匹配成功,ProjectList会被渲染在Project的<router-view>中
      children: [{
        path: '',
        name: 'project.list',
        component: ProjectList
      }, {
        path: 'p/:id',
        name: 'project',
        component: ProjectInner,
        children: [
          // info
          {
            path: 'info',
            name: 'project.info',
            component: Info
          },
          // data
          {
            path: 'datapreview',
            name: 'project.preview',
            component: DataPreview
          }, {
            path: 'datasource',
            name: 'project.source',
            component: DataSource
          },
          // train
          {
            path: 'experiment',
            name: 'project.experiment',
            component: TrainExperiment
          }, {
            path: 'result',
            name: 'project.result',
            component: TrainResult
          },
          // analytics
          {
            path: 'feature',
            name: 'project.feature',
            component: AnalyticsFeature
          }, {
            path: 'insight',
            name: 'project.insight',
            component: AnalyticsInsight
          }, {
            path: 'predict',
            name: 'project.predict',
            component: AnalyticsPredict
          },
          {
            path: '*',
            redirect: { name: 'project.info' }
          }
        ]
      }]
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})
