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
    // default router, TODO: use router config
    this.$router.push(`${this.$route.path}/datasource`)
    this.$root.$on('toggleStatus', toggleStatus => {
      this.toggleStatus = toggleStatus
    })
  },
  components: {
    Sidebar
  }
}
</script>
<style lang="scss" scoped>
.main {
  margin-left: 280px;
  padding: 20px;
  &.toggle-hide {
    margin-left: 60px;
  }
}
</style>

