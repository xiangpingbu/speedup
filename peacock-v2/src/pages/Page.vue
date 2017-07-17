<template>
  <div>
    <!-- <TopBar v-if="loading"></TopBar> -->
    <span>Page</span>
  </div>
</template>

<script>
  import TopBar from '@/components/TopBar.vue'
  export default {
    name: 'Page',
    created () {
      // console.log(this.$route)
    },
    data () {
      return {
        loading: true
      }
    },
    components: {
      TopBar
    }
  }
</script>
