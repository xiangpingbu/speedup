<template>
<div class="header">
  <router-link to="/">MAAS Visualization</router-link>
  <router-link :to="model" class="fr">Back to DashBoard</router-link>

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
  props: ['numIds', 'countIds', 'psiIds'],
  computed: {
    model() {
      return `/${this.$route.params.model}`
    }
  }
}
</script>

<style scoped>
.header {
  background: #292929;
  color: #fff;
  padding: 15px 10px;
  margin-bottom: 10px;
}
</style>
