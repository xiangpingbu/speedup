<template>
<div class="header">
  <router-link to="/">MAAS Visualization</router-link>
  <span class="fr">
      <router-link to="/">To DashBoard</router-link>
      &nbsp;
      <span @click="reload"><SvgReload /></span>
  </span>

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
  },
  methods: {
    reload() {
      // TODO: reload soft
      location.reload()
    }
  }
}
</script>

<style scoped>
.header {
  background: rgba(88, 88, 88, .2);
  padding: 1rem .75rem;
  margin-bottom: .75rem;
  /*border-bottom: 1px solid #28343a;*/
}
</style>
