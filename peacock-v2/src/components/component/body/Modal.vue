<template>
  <div class="modal fade" :id='toggleId' role="dialog">
    <div class="modal-dialog">
      <slot></slot>
    </div><!-- /.modal -->
  </div>
</template>

<script>
  export default {
    name: 'Modal',
    componentName: 'Modal',
    props: ['toggleId']
  }

</script>
