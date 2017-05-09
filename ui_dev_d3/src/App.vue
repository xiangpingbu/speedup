<template>
  <div id="app">
    <!-- <img src="./assets/logo.png"> -->
    <!-- <router-link to="/">Go to Home</router-link> -->
    <!-- <router-link to="/hello">Go to Hello</router-link> -->
    <!-- <amenu></amenu> -->
    <!-- <topbar></topbar> -->
    <router-view></router-view>
    <div class="footer">
      MAAS Visualization
    </div>
  </div>
</template>

<script>
// import topbar from '@/components/TopBar.vue'
// import amenu from './components/Menu.vue'
export default {
  name: 'app'
  // components: {topbar}
  // components: {amenu}
}
</script>

<style>
 @import "./assets/css/pure-min.css";
 @import "./assets/css/common.css";
 @import "./assets/css/chart-layout.css";
 /*@import "css/font-awesome-4.7.0/css/font-awesome.css"*/
</style>
