<template>
  <div>
    <Sidebar></Sidebar>
    <div class="main" :class="{'toggle-hide': toggleStatus}">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'
export default {
  name: 'projectinner',
  data() {
    return {
      toggleStatus: false
    }
  },
  created() {
    // default router
    if (this.$route.path.split('/').length === 4) {
      // this.$router.push(`${this.$route.path}/datasource`)
      this.$router.push(`${this.$route.path}/info`)
    }
    this.$root.$on('toggleStatus', toggleStatus => {
      this.toggleStatus = toggleStatus
    })
  },
  watch: {
    '$route.path' () {
      // if (this.$route.path.split('/').length < 4) {
      //   this.$router.push(`${this.$route.path}/datasource`)
      // }
    }
  },
  components: {
    Sidebar
  }
}
</script>
<style lang="scss" scoped>
.main {
  margin-left: 280px;
  // padding: 20px;
  &.toggle-hide {
    margin-left: 60px;
  }
}
</style>
