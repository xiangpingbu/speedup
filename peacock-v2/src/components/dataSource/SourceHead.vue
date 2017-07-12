<template>
  <MainHead>
    <h1 class="pull-left">
      <i class="fa fa-database">  Data Sources</i>
    </h1>
    <span class="page-head-buttons"><button class="btn btn-primary">Add new database1</button></span>
  </MainHead>

</template>

<script>
  import MainHead from '@/components/component/body/MainHead'
  export default {
    components: {MainHead}
  }

</script>

<style>
  .page-head-buttons {
    position: absolute;
    top: 10px;
    right: 20px;
  }

  .pull-left {
    float: left;
  }

</style>
