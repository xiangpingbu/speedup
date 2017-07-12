<template>
    <div class="top">
      <logo></logo>
      <div class="line">
        <router-link to='/'>
          Home
        </router-link>
        <router-link to=pathList[1]>
          <!-- {{Project}} -->
          {{pathList[1]}}
        </router-link>
        <router-link to='/'>
          {{Algorithm}}
        </router-link>
      </div>
    </div>
</template>

<script>
    import logo from '@/components/Logo.vue'
    export default {
      name: 'TopBar',
      props: ['Project', 'Algorithm'],
      created () {
        // console.log(this.$route)
        // console.log(this.$route.fullPath.split('/'))
        this.$nextTick(() => {
          this.pathList = this.$route.fullPath === '/' ? ['Home'] : this.$route.fullPath.split('/')
          this.pathList.shift()
          console.log(this.pathList)
          this.pathList.push('tt')
        })
      },
      data () {
        return {
          activeIndex: '1',
          activeIndex2: '1',
          pathList: []
        }
      },
      methods: {
        handleSelect (key, keyPath) {
          console.log(key, keyPath)
          // console.log(this.$route)
        }
      },
      components: {
        logo
      }
    }
</script>

<style>
  body {
    margin: 0;
  }
  .top {
    padding: 5px;
    background-color: #4aa3df;
    /*display: inline-block;*/
    /*float: left;*/
  }
  .line {
    /*float: left;*/
    padding-left: 10px;
    /*background-color: #4aa3df;*/
    display: inline-block;
    /*vertical-align: middle;*/
    /*text-align: center;*/
  }
  .line a {
    font-family: "Roboto Condensed",sans-serif;
    text-transform: uppercase;
    color: white;
    text-decoration: none;
  }
</style>
