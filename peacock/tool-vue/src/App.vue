<template>
<div id="app">
  <router-view></router-view>
  <Sidebar />
</div>
</template>

<script>
import * as d3 from 'd3'
import * as c3 from 'c3'
import $ from 'jquery'
window.jQuery = window.$ = $
export default {
  name: 'app',
  created() {
      // import lib
      console.warn($)
      console.warn(c3)
      console.warn(d3)
  }
}
</script>

<style lang="css">
@import '../node_modules/bootstrap/dist/css/bootstrap.css';
@import './assets/style/c3.css';
@import './assets/style/tool.css';
/*@import '../static/css/font-awesome.css';*/
@import '../static/css/animate.css';
/*@import '../static/css/style.css';*/
@import '../static/css/dropzone/basic.css';
@import '../static/css/dropzone/dropzone.css';
@import '../static/css/iCheck/custom.css';
@import '../static/css/select2.min.css';
</style>
