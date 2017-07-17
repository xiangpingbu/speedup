<template>
  <Modal :toggleId="toggleId">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">
          <i class="fa fa-database"></i>
          Add new dataset
        </h3>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group"
               data-ng-class="{ 'has-error': form.title.$invalid &amp;&amp; form.title.$dirty || controller.serverErrors.title }">
            <label for="RawDatasetTitle">Title</label>
            <input type="text" class="form-control" id="RawDatasetTitle" name="title" required="" autofocus="">
          </div>
          <div class="form-group">
            <label for="TrainFile">Data file (max 4GB)</label>
            <div>
              <input type="file" id="TrainFile" ngf-select="" name="trainfile">
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default">Close

        </button>
        <button type="button" class="btn btn-primary ng-scope">Submit</button>
      </div>
    </div><!-- /.modal-content -->
  </Modal>
</template>


<script>
  import Modal from '@/components/component/body/Modal'
  export default {
    components: {Modal},
    name: 'SourceModal',
    componentName: 'SourceModal',
    data () {
      return {
        toggleId: this.props
      }
    },
    props: ['toggleId']
  }

</script>

<style>
  .modal-header {
    padding: 15px;
    border-bottom: 1px solid #e5e5e5;
  }

  .modal-body {
    position: relative;
    padding: 15px;
  }

  .modal-footer {
    padding: 15px;
    text-align: right;
    border-top: 1px solid #e5e5e5;
  }
</style>
