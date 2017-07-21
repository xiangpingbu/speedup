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
    <SourceModal :toggleId="modalId">
    </SourceModal>
  </div>
</template>

<script>
  import MainHead from '@/components/MainHead'
  import SourceModal from '@/pages/project/data/Source/SourceModal'

  export default {
    data () {
      return {
        modalId: 'dataset'
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
