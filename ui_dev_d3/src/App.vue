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
#app {
  /*font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;*/
  /*background-color: #e0e0e0;*/
  /*background-color: #3c3c3c;*/
  background-color: #141414;
}

 @import "css/pure-min.css";
 @import "css/gallery.css";
 @import "css/chart-layout.css";
 /*@import "css/font-awesome-4.7.0/css/font-awesome.css"*/
</style>
