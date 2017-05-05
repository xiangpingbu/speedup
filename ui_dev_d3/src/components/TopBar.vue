<template>
  <div class="header">
    <!-- <link rel="stylesheet" href="https://unpkg.com/purecss@0.6.2/build/pure-min.css" integrity="sha384-" crossorigin="anonymous"> -->
    <div>
      <!-- <a class="pure-menu-heading" href="">MAAS 数据可视化</a> -->
      <ul id="menu" >
        <li><a class="pure-menu-heading" href="">MAAS Visualization</a></li>
          <li><router-link to="/">Home</router-link></li>
          <li>
              <a href="#">Categories</a>
              <ul>
                  <li><a href="#">CSS</a></li>
                  <li><a href="#">Graphic design</a></li>
                  <li><a href="#">Development tools</a></li>
                  <li><a href="#">Web design</a></li>
              </ul>
          </li>
          <li>
            <a href="#">Charts</a>
            <ul>
              <li>
                <a href="#">LineCharts</a>
                <ul>
                  <li v-for="id in numIds" class="pure-menu-item">
                    <router-link :to="'/'+model+'/'+id" class="pure-menu-link">{{id}}</router-link>
                    <!-- <a :href="'#/dash/'+id" class="pure-menu-link">{{id}}</a> -->
                  </li>
                </ul>
              </li>
              <li>
                <a href="#">BarCharts</a>
                <ul>
                  <li v-for="id in countIds" class="pure-menu-item">
                    <router-link :to="'/'+model+'/'+id" class="pure-menu-link">{{id}}</router-link>
                  </li>
                </ul>
              </li>
              <li><a href="#">PSI</a></li>
            </ul>
          </li>
          <li><a href="#">Work</a></li>
          <li><a href="#">About</a></li>
          <li><a href="#">Contact</a></li>

          <li style="float: right; width: 200px;">
            <router-link :to="'/'+model" style="position:absolute; right:0;">Back to DashBoard</router-link>
            <!-- <a href="/#/dash" style="position:absolute; right:0;">Back to DashBoard</a> -->
          </li>
      </ul>

      <!-- <a class="pure-menu-heading" href="#/dash" style="position:absolute; right:1%; top:1%;">Back to DashBoard</a> -->
    </div>

<!-- <li v-for="id in ids" class="pure-menu-item"><a :href="'#/dash/'+id" class="pure-menu-link">{{id}}</a></li> -->

    <!-- <div class="pure-menu pure-menu-horizontal" style="margin-left:50px">
      <ul class="pure-menu-list">
        <li class="pure-menu-item">健康: {{health}}</li>
        <li class="pure-menu-item">可用: {{available}}</li>
        <li class="pure-menu-item">繁忙: {{busy}}</li>
      </ul>
    </div> -->
  </div>
</template>
<script>
export default {
  name: 'topbar',
  props: ['numIds', 'countIds'],
  computed: {
    model () {
      return this.$route.params.model
    }
  }
}

</script>

<style>
 /*@import "https://unpkg.com/purecss@0.6.2/build/grids-responsive-min.css";*/
 /*@import "../css/gallery.css";*/
 @import "../css/pure-min.css";
 @import "../css/menu.css";
</style>
