<template>
  <div>
    <ProjectHead></ProjectHead>
    <div class="container-full">
      <div class="container-fluid">
        <div class="row">
          <ProjectCard :projects='projects'></ProjectCard>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import ProjectCard from '@/components/ProjectCard'
  import ProjectHead from '@/pages/project/ProjectHead'
  import http from '@/constant/honeybeeConfig'
  export default {
    name: 'projectlist',
    created () {
      http.get('/project/list/222').then((response) => {
        this.$data.projects = response.data.data
      })
    },
    data () {
      return {
        id: 'source',
        loading: true,
        projects: null
      }
    },
    components: {
      ProjectCard,
      ProjectHead
    }
  }
</script>

<style>
  .container-full {
    max-width: 100%;
    padding-left: 7.5px;
    padding-right: 7.5px;
  }

  .container-fluid {
    padding-right: 7.5px;
    padding-left: 7.5px;
  }
</style>
