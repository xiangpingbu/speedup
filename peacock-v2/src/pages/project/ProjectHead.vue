<template>
  <div>
    <MainHead>
      <h1 class="pull-left">
        <!--<i class="fa fa-database"> Data Sources</i>-->
        Pantry
      </h1>
      <span class="page-head-buttons">
        <button class="btn btn-2-white" @click="refresh()"><li class="fa fa-fw fa-refresh"></li>Refresh</button>
      </span>
    </MainHead>
    <!--<SourceModal :toggleId="modalId">-->
    <!--</SourceModal>-->
  </div>
</template>

<script>
  import MainHead from '@/components/MainHead'
//  import SourceModal from '@/pages/project/data/Source/SourceModal'

  export default {
    data () {
      return {
        modalId: 'dataset'
      }
    },
    methods: {
      refresh () {
      }
    },
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


  .btn-2-white {
    font-family: "Roboto Condensed",sans-serif;
    background-color: #fff;
    color: #404040;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    font-weight: 400;
    transition: all .2s ease;
  }

</style>
