<template lang="html">
  <div class="select">
      select
  </div>
</template>

<script>
import tool_variable_select from '@/assets/js/tool_variable_select'
//var host = "http://localhost:8091";
var host = "http://101.71.245.166:8091";
export default {
    mounted() {
        const self = this;
        $.ajax({
            url: host + "/tool/if_applyed",
            type: 'post',
            async: false,
            success: function(result) {
                if (!result.success) {
                    alert("you have not applyed yet");
                    //因为没有没有执行过apply,所以不能跳转到#select页面,
                    //因此url人为地设置在bar页面
                    self.$router.push('/bar')
                    //url更改后会出发onhashchange方法,所以要记录原来的url
                } else {
                    display("variableSelect");
                    tool_variable_select.preInit();
                }
                //通过原先的url判断是否要刷新页面
                originUt = ut;
            }
        });
    }
}
</script>

<style lang="css">
</style>
