<template>
    <div class="top">
      <logo></logo>
      <div class="line">
        <ul>
          <li>
            <router-link to='/'>
              <span><i class="fa fa-home" aria-hidden="true"></i></span>
              Home
            </router-link>
          </li>
          <li>
            <router-link to='/test/'>
              <span><i class="fa fa-bookmark" aria-hidden="true"></i></span>
              Project
            </router-link>
          </li>
          <li>
            <router-link to='/source'>
              <span><i class="fa fa-folder-open" aria-hidden="true"></i></span>
              {{Algorithm}}
            </router-link>
          </li>
        </ul>

      </div>
      <div class="sign">
        <ul >
          <li>
            <router-link to='/'>
              Sign in
            </router-link>
          </li>
          <li>
            <router-link to='/' class="btn-sign">
              Sign up for maas
            </router-link>
          </li>
        </ul>
      </div>
    </div>
</template>

<script>
    import logo from '@/components/Logo.vue'
    // import {mapMutations} from 'vuex'
    export default {
      name: 'TopBar',
      props: ['Project', 'Algorithm'],
      created () {
        // console.log(this.$route)
        // console.log(this.$route.fullPath.split('/'))
        this.$nextTick(() => {
          this.pathList = this.$route.fullPath.split('/')
          this.pathList.shift()
          console.log(this.pathList)
        })
      },
      data () {
        return {
          // id: this.$route.id,
          activeIndex: '1',
          activeIndex2: '1',
          pathList: [],
          ProjectMap: []
        }
      },
      methods: {
        // ...mapMutations(['saveData', 'saveId']),
        handleSelect (key, keyPath) {
          console.log(key, keyPath)
          // console.log(this.$route)
        },
        getProjectDetail (id) {
          this.ProjectMap = this.$store.state.map.id
        }
      },
      components: {
        logo
      }
    }
</script>

<style>
  /*@import "../assets/inspinia/";*/
  @import '../assets/inspinia/font-awesome/css/font-awesome.min.css';
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
    margin-left: -30px;
    margin-top: 10px;
  }
  .line li {
    display: inline-block;
    font-family: "Roboto Condensed",sans-serif;
    text-transform: uppercase;
    color: white;
    text-decoration: none;
    padding-left: 5px;
  }
  .line a {
    /*display: inline-block;*/
    font-family: "Roboto Condensed",sans-serif;
    text-transform: uppercase;
    color: white;
    text-decoration: none;
  }
  .sign {
    float: right;
    /*padding-left: 10px;*/
    margin-top: 10px;
  }
  .sign li {
    display: inline-block;
    padding-left: 5px;
  }
  .sign a {
    font-family: "Roboto Condensed",sans-serif;
    text-transform: uppercase;
    color: white;
    text-decoration: none;
    padding: 5px;
  }
  .btn-sign {
    font-family: "Roboto Condensed",sans-serif;
    background-color: rgba(255,255,255,.15);
    color: #fff;
    border-radius: 4px;
    transition: all .2s ease;
  }
</style>
