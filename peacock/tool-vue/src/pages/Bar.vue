<template lang="html">
<div class="" >
    <BarHead />
    <div class="spinner" style="display:none;width:50px;height:38px;position:fixed;left:50%;top:50%;margin-top:-150px;">
        <div class="rect1"></div>
        <div class="rect2"></div>
        <div class="rect3"></div>
        <div class="rect4"></div>
        <div class="rect5"></div>
    </div>
    <!-- <div id="analyze">

    </div> -->
    <div class="">
        <BarItem :item="item" v-for="item in barItemList"/>
    </div>
</div>

</template>

<script>
// import tool_button from '@/assets/js/tool_button'
// import tool from '@/assets/js/tool'
var column_numMap =
    {
        "bin_num": 0, "min": 1,
        "max": 2, "min_boundary": 3,
        "max_boundary": 4, "bads": 5,
        "goods": 6, "total": 7,
        "total_perc": 8, "bad_rate": 9,
        "woe": 10, "type": 11
    };

var column_cateMap =
    {
        "bin_num": 0, "name": 1,
        "bads": 2, "goods": 3,
        "total": 4, "total_perc": 5,
        "bad_rate": 6, "woe": 7,
        "type": 8
    };

function getHead(name) {
    if (name != null) {
        return column_cateMap;
    } else {
        return column_numMap;
    }
}

export default {
    data() {
        return {
            barItemList: []
        }
    },
    mounted() {
        // TODO 拆分，移到item中
        const self = this;

        this.$root.$on('initBar', (result) => {
            var num = 0;
            var initList = [];
            //通过变量名获取数据
            for (var valName in result.data) {
                var varData = result.data[valName]["var_table"];
                var iv = result.data[valName]["iv"];
                var type = varData[0]["type"];
                var table_head;
                if (type == "Numerical") {
                    table_head = getHead(null)
                } else {
                    table_head = getHead(varData[0][valName]);
                }
                this.barItemList.push({valName, iv, num, table_head, varData})
                initList.push(num);

                var isSelected = result.data[valName]["is_selected"];
                if (isSelected != null && (isSelected === true || isSelected ===1)) {
                    $("#" + valName + "_name").find(".icheckbox_square-green").iCheck('check');
                }
                num++;
            }
            //记录行数
            // $("#rowNum").val(num);
            localStorage.setItem('rowNum', num);
            //设置table内的标签可以点击

            // changeTd();
            // //初始化按钮,有合并和分裂的操作
            // this.$nextTick(() => {
            //     setTimeout(() => {
            //         buttonInit(initList);
            //     }, 1000);
            // })
            $(".spinner").css('display', 'none');
        })
        // initHead();
        // commitBranch();
    }
}
</script>

<style lang="css">
</style>
