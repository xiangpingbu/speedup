<template>
  <div class="container-fluid">
    <SourceHead></SourceHead>
    <div class="col-sm-6">
      <div class="panel panel-primary">
        <div class="panel-body">
          <b>Dataset:</b> {{Dataset}}<br>
          <b>File name:</b> for_testing.csv<br>
          <b>Scope:</b> private<br>
          <b>Added at:</b> 7/7/17 2:31 PM<br>
          <b>Origin:</b> Dataset uploaded by user<br>
          <b>Maas is able to read this dataset.</b>
        </div>
        <div class="panel-footer">
          <button class="btn btn-sm btn-success">
            <span class="glyphicon glyphicon-eye-open"></span>
            Preview
          </button>

          <div class="pull-right">
            <button tooltip-placement="top" uib-tooltip="Edit data source" tooltip-append-to-body="true"
                    tooltip-enable="true" class="btn btn-sm btn-info ng-scope"
                    >
              <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
            </button>
            <button tooltip-placement="top" uib-tooltip="Delete data source" tooltip-append-to-body="true"
                    tooltip-enable="true" class="btn btn-sm btn-danger ng-scope">
              <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  // 引用头部标签
  import SourceHead from '@/components/datasource/SourceHead.vue'
//  import axios from 'axios'
  export default {
    created: function () {
      console.log(global.host)
      // `this` 指向 vm 实例
//      axios.get('localhost:8082/user?ID=12345')
//        .then(function (response) {
//          console.log(response)
//        })
    },
    data () {
      return {
        Dataset: 'dataset'
      }
    },
    components: {SourceHead}
  }
</script>

<style>
  .panel-body {
    padding: 15px;
  }

  .panel {
    margin-bottom: 20px;
    background-color: #fff;
    border: 1px solid transparent;
    border-radius: 4px;
    -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
    box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
  }

  .panel-primary {
    border-color: #337ab7;
  }

  .panel-footer {
    padding: 10px 15px;
    background-color: #f5f5f5;
    border-top: 1px solid #ddd;
    border-bottom-right-radius: 3px;
    border-bottom-left-radius: 3px;
  }

  .pull-right {
    float: right;
  }
</style>

