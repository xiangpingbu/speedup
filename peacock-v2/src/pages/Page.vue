<template>
  <div>
    <!-- <TopBar v-if="loading"></TopBar> -->
    <span>Page</span>
    <ProjectCard :id="id"></ProjectCard>
  </div>
</template>

<script>
  import ProjectCard from '@/components/ProjectCard'
  export default {
    name: 'Page',
    created () {
      // console.log(this.$route)
    },
    data () {
      return {
        id: 'source',
        loading: true
      }
    },
    components: {
      ProjectCard
    }
  }
</script>
