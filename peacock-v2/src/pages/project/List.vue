<template>
  <div>
    <ProjectCard :id="id"></ProjectCard>
    <!-- mock  -->
    <ProjectCard :id="id"></ProjectCard>
  </div>
</template>

<script>
  import ProjectCard from '@/components/ProjectCard'
  export default {
    name: 'projectlist',
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
