<template>
  <div>
    <Sidebar></Sidebar>
    <div class="main">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'
export default {
  name: 'projectinner',
  created() {
    // default router, TODO: use router config
    this.$router.push(`${this.$route.path}/datapreview`)
  },
  components: {
    Sidebar
  }
}
</script>
<style scoped>
.main {
  margin-left: 280px;
  padding: 20px;
}
</style>

