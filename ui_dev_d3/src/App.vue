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
export default {
  name: 'app'
  // components: {topbar}
  // components: {amenu}
}
</script>

<style scoped>
@import "./assets/css/pure-min.css";
@import "./assets/css/common.css";
@import "./assets/css/chart-layout.css";
#app {
  position: relative;
  padding-bottom: 2rem;
  min-height: calc(100vh - 2rem);
}
</style>
