<template lang="html">
  <div class="">
      table
  </div>
</template>

<script>
function changeTd() {
    $("td[name='woe']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:60px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });

    $("td[name='min_boundary']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });

    $("td[name='max_boundary']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });
}
export default {
    mounted() {
        changeTd();
    }
}
</script>

<style lang="css">
</style>
