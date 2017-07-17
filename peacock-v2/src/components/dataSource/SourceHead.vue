<template>
  <div>
    <MainHead>
      <h1 class="pull-left">
        <i class="fa fa-database"> Data Sources</i>
      </h1>
      <span class="page-head-buttons">
        <button data-toggle="modal" data-target="#dataset" class="btn btn-primary">Add new database1</button>
      </span>
    </MainHead>
    <SourceModal :toggleId="toggleId">
    </SourceModal>
  </div>
</template>

<script>
  import MainHead from '@/components/component/body/MainHead'
  import SourceModal from '@/components/dataSource/SourceModal.vue'

  export default {
    data () {
      return {
        toggleId: 'dataset'
      }
    },
    components: {MainHead, SourceModal}
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
