<template lang="html">
    <div class="loading" v-if="show">
        <div class="cp-round"></div>
    </div>
</template>
<script>
export default {
  data: function() {
    return {
      show: false
    }
  },
  created: function() {
    // 避免闪烁，用白屏代替，内部
    // setTimeout(() => {
    this.show = true
    // })
  }
}
</script>
<style lang="css" scoped>
.loading {
    text-align: center;
    width: 100%;
    margin-bottom: 20px;
}
.cp-round {
    width: 40px;
    height: 40px;
    display: inline-block;
    box-sizing: border-box;
    position: relative;
}
.cp-round:before {
    border-radius: 50%;
    content: " ";
    width: 40px;
    height: 40px;
    display: inline-block;
    box-sizing: border-box;
    position: relative;
    border: solid 3px #aaa;
    position: absolute;
    top: 0;
    left: 0;
}
.cp-round:after {
    border-radius: 50%;
    content: " ";
    width: 40px;
    height: 40px;
    display: inline-block;
    box-sizing: border-box;
    border-top: solid 3px #fff;
    border-right: solid 3px transparent;
    border-bottom: solid 3px transparent;
    border-left: solid 3px transparent;
    position: absolute;
    top: 0;
    left: 0;
    animation: cp-round-animate 1s ease-in-out infinite;
}
@keyframes cp-round-animate {
    0% {
        transform: rotate(0);
    }
    100% {
        transform: rotate(360deg);
    }
}
</style>
