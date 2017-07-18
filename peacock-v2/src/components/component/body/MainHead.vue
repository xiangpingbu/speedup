<template>
  <div class="row">
    <div class="page-head">
      <slot></slot>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'PageHead',
    componentName: 'PageHead'
  }
</script>

<style>
  .page-head {
    margin-top: 0;
    margin-bottom: 15px;
    border-bottom: 1px solid rgba(0, 0, 0, .075);
    padding-bottom: 20px;
    padding-left: 40px;
    padding-right: 40px;
    background-color: #fafafa;
    overflow: hidden;
    position: relative;
    /*z-index: 1001;*/
  }

  .page-head h1 {
    font-size: 20px;
    margin-top: 20px;
    margin-bottom: 0;
    display: inline-block;
    font-family: "Roboto Condensed",sans-serif;
    font-weight: 400;
  }
</style>
