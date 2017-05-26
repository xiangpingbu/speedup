<template lang="html">
  <div v-if="item">
      <!-- <ItemChart />
      <ItemTable /> -->
      <div :id="`${itemData.valName}_name`" class="item-top">
          <input type="checkbox" :name="itemData.valName" class="apply-checks" value="">
          <span style="margin-left: 10px">{{itemData.valName + "-------"}}</span>
          <span class="iv" :id="itemData.valName">{{itemData.iv}}</span>
      </div>
      <ItemChart :item="itemData" />
      <div class="">
          <button @click="mergeClick" :name="item.valName" :id="`merge_${itemData.num}`" class="btn btn-primary var-btn" type="button" style="maring-left: 25px" disabled="disabled">合并</button>
          <button @click="divideClick" :name="item.valName" :id="`divide_${item.num}`" class="btn btn-danger var-btn" type="button">分裂</button>
          <button  :name="item.valName" :id="`save_${item.num}`" class="btn btn-warning var-btn" type="button">保存记录</button>
          <button :name="itemData.valName" :id="`divide_manually_${itemData.num}`" class="btn btn-success var-btn" type="button">手动分裂</button>
      </div>
  </div>
</template>

<script>
import ItemChart from './ItemChart'
// import ItemTable from './ItemTable'
import * as d3 from 'd3'

// var xScale, yScale;

import {
    host
} from '@/config';

var controlMap = {};

var maxIndex = 2;
var minIndex = 1;
var minBoundIndex = 3;
var maxBoundIndex = 4;
var woeIndex = 10;
var binNumIndex = 0;
var cateIndex = 11;
var categoricalIndex = 1;
var branches = null;
var originalBranch = null;
var finishInit = false;

//numerical的列的排布
var num_columnMap = {
    0: "bin_num",
    1: "min",
    2: "max",
    3: "min_boundary",
    4: "max_boundary",
    5: "bads",
    6: "goods",
    7: "total",
    8: "total_perc",
    9: "bad_rate",
    10: "woe",
    11: "type"
};


//categorical的列的排布
var cate_columnMap = {
    0: "bin_num",
    1: "name",
    2: "bads",
    3: "goods",
    4: "total",
    5: "total_perc",
    6: "bad_rate",
    7: "woe",
    8: "type"
};



export default {
    data() {
        return {
            tempData: ''
        }
    },
    computed: {
        itemData() {
            return this.tempData || this.item;
        }
    },
    methods: {
        adjustTable(result) {
            // const {
            //     valName,
            //     iv,
            //     num,
            //     table_head,
            //     varData
            // } = this.item;
            const {
                valName,
                num
            } = this.itemData;
            // console.warn(393);
            // var svg = null;
            // renderBars(svg, varData, id, true);
            const temp = JSON.parse(JSON.stringify(this.itemData));
            temp.varData = result.data[valName]["var_table"];
            temp.iv = result.data[valName]["iv"];
            this.tempData = temp;

            this.$nextTick(() => {
                this.buttonInit([num]);
            })
            //隐藏等待提示
            $(".spinner").css('display', 'none');
        },

        buttonInit(initList) {
            const a = initList[0]
            // debugger;
            // $(".var-btn").unbind("click");
            /**
             * 前后点击两次点击bar,可以多选,第三次会重置,重新开始选择
             */
            //初始化每一个variable的点击事件,预先初始化相关变量
            controlMap[a] = {};
            var map = controlMap[a];
            map.start = {};
            map.end = {};
            map.array = [$("#index_" + a)];
            var rects = $('#svg_' + a).find('.MyRect').click(function() {
                //id和a的值是一致的,因此可以通过这个值从map中得到数据
                var id = $(this).parent().parent().attr("id").split("_")[1];
                var childTrs = $('#tbody_' + id).children("tr");
                var tds = $(childTrs.get(0)).children("td");
                //判断是否为categorical还是numerical
                var type = tds.get(tds.length - 1).innerHTML;
                var isNum = (type == "Numerical");

                //觉得应该可以用队列来实现
                var array = controlMap[id].array;
                var start = controlMap[id].start;
                var end = controlMap[id].end;
                if (array.length == 1) {
                    var val = $(this).attr("id");
                    var index = val.split("_")[2];
                    if ($.isEmptyObject(start)) {
                        $("#" + val).attr("fill", "brown");
                        start.index = index;
                    } else if ($.isEmptyObject(end)) {
                        end.index = index;
                    }
                    //如果前后两次都点击完毕,会按照次序将bar填充颜色.并在数组里添加记录
                    if (!$.isEmptyObject(start) && !$.isEmptyObject(end)) {
                        var si = parseInt(start.index);
                        var ei = parseInt(end.index);
                        var bar;
                        if (isNum) {
                            for (var i = Math.min(si, ei); i <= Math.max(si, ei); i++) {
                                bar = $("#index_" + id + "_" + i);
                                array.push(bar);
                                bar.attr("fill", "brown");
                            }
                        } else {
                            bar = $("#index_" + id + "_" + si);
                            bar.attr("fill", "brown");
                            array.push(bar);
                            bar = $("#index_" + id + "_" + ei);
                            bar.attr("fill", "brown");
                            array.push(bar);
                        }
                    }
                } else {
                    //将之前数组中添加的记录提取出来,重置颜色
                    for (var m = 0; m < array.length; m++) {
                        array[m].attr("fill", "#000000");
                    }
                    array = controlMap[id].array = [];
                    controlMap[id].end = {};
                    end = controlMap[id].end;
                    array.push($(this));
                    $(this).attr("fill", "brown");
                    start.index = $(this).attr("id").split("_")[2];
                }

                if (!$.isEmptyObject(start) && !$.isEmptyObject(end)) {
                    $("#merge_" + id).removeAttr("disabled");
                } else {
                    $("#merge_" + id).attr("disabled", "disabled");
                }
            });



            $("#divide_manually_" + a).click(function() {
                $(".var-area-btn").unbind("click");
                var id = $(this).attr("id").split("_")[2];
                var input_area_id = "#divide_area_" + id;
                var childTrs = $('#tbody_' + id).children("tr");
                var tds = $(childTrs.get(0)).children("td");
                var type = tds.get(tds.length - 1).innerHTML;
                var isCate = (type == "Categorical");

                if (d3.select(input_area_id).style("display") == 'none') {
                    d3.select(input_area_id).style("display", "block");
                    var str = [];
                    var index = 3;
                    if (isCate) index = 1;
                    $('#tbody_' + id).find("tr").each(function(i, n) {
                        str.push($(n).children().eq(index).html());
                    });
                    $("#manual_input_" + id).text(str.join(","));


                } else {
                    d3.select(input_area_id).style("display", "none");
                }

                $("#manual_btn_" + id).bind("click", function() {
                    var name = $(this).attr("name");
                    var boundary = $("#manual_input_" + id).val();
                    alert(boundary);
                    var branch = localStorage.getItem("branch");
                    var model_name = localStorage.getItem("model_name");
                    $(".spinner").css('display', 'block');
                    $.ajax({
                        url: host + "/tool/divide_manually",
                        type: 'post',
                        data: {
                            "variable_name": name,
                            "boundary": boundary,
                            "branch": branch,
                            "modelName": model_name,
                            "type": isCate
                        },
                        async: true,
                        success: function(result) {
                            adjustTable(result, id, initList, name);
                            $(".spinner").css('display', 'none');
                        },
                        error: function(result) {
                            $(".spinner").css('display', 'none');
                        }
                    });
                });
            });

        },

        divideClick() {
            console.warn(303);
            $(".spinner").css('display', 'block');
            var data = {};
            const self = this;
            const id = self.item.num;
            const name = self.item.valName;
            var start = controlMap[id].start;
            // 
            var childs = $('#tbody_' + id).children("tr");
            var tds_0 = $(childs.get(0)).children("td");
            //                var td = $(childs.get(start.index)).children("td");
            var type = tds_0.get(tds_0.length - 1).innerHTML;
            var columnMap;
            if (type == 'Numerical') {
                columnMap = num_columnMap;
            } else {
                cate_columnMap[1] = name;
                columnMap = cate_columnMap;
            }
            var list = [];
            data.table = list;
            data.name = name;
            // 
            var all = 0;
            for (var i = 0; i < childs.length; i++) {
                var tds = $(childs.get(i)).children("td");
                var obj = {};
                for (var j = 0; j < tds.length; j++) {
                    var key = columnMap[j];
                    if (key == 'total') {
                        all += parseInt(tds.get(j).innerHTML);
                    }
                    obj[key] = tds.get(j).innerHTML;
                }
                list.push(obj);
                if (start.index == i) {
                    data.selected = obj;
                    data.selectedIndex = i;
                }
            }

            data.all = all;
            // $(".spinner").css('display', 'block');
            $.ajax({
                url: host + "/tool/divide",
                type: 'post',
                data: {
                    "data": JSON.stringify(data),
                    "target": localStorage.getItem("target"),
                    "modelName": localStorage.getItem("model_name"),
                    "branch": localStorage.getItem("branch")
                },
                async: true,
                success: function(result) {
                    self.adjustTable(result)
                }
            });
        },
        mergeClick() {
            const self = this;
            const id = self.item.num;
            const name = self.item.valName;
            var start = controlMap[id].start;
            var end = controlMap[id].end;
            console.warn(321);
            var list = '';
            var wholeList = '';
            var childTrs = $('#tbody_' + id).children("tr");

            var tds = $(childTrs.get(0)).children("td");
            //判断是否为categorical还是numerical
            var type = tds.get(tds.length - 1).innerHTML;
            //得到每个bin的最大值
            var min = Math.min(start.index, end.index);
            var max = Math.max(start.index, end.index);
            //var isNum = type.indexOf("F") >= 0;
            var isNum = (type == "Numerical");
            //判断应该从哪一列获取相应的值
            var valIndex;
            if (isNum) valIndex = minBoundIndex;
            else valIndex = categoricalIndex;
            var iterList = [];
            if (!isNum) iterList = [min, max];
            else {
                for (var i = min; i <= max; i++) {
                    iterList.push(i);
                }
            }
            for (var n in iterList) {
                if (iterList[n] == min) {
                    if (isNum) {
                        //此时该min的值可以被丢弃
                        continue;
                    }
                }
                if (isNum) {
                    //                        valIndex = maxIndex;
                    valIndex = minBoundIndex;
                } else valIndex = categoricalIndex;

                list = list + ($(childTrs.get(iterList[n])).children("td").get(valIndex).innerHTML) + ("&");
            }
            //去除末尾的&符号
            list = list.substring(0, list.length - 1);


            var tdVal;
            //numerical按照区间筛选
            if (isNum) {
                for (var index = 0; index < childTrs.length; index++) {
                    tdVal = $(childTrs.get(index)).children("td").get(valIndex).innerHTML;
                    wholeList = wholeList + (tdVal) + ("&");
                }
            } else {
                for (var b = 0; b < childTrs.length; b++) {
                    //获取categorical的除选中以外的值
                    if (b != min && b != max) {
                        wholeList = wholeList + ($(childTrs.get(b)).children("td").get(valIndex).innerHTML) + ("&");
                    }
                }
            }

            wholeList = wholeList.substring(0, wholeList.length - 1);
            $(".spinner").css('display', 'block');
            console.warn(381);
            $.ajax({
                url: host + "/tool/merge",
                type: 'post',
                data: {
                    "varName": name,
                    "boundary": list,
                    "allBoundary": wholeList,
                    "type": type,
                    "target": localStorage.getItem("target"),
                    "modelName": localStorage.getItem("model_name"),
                    "branch": localStorage.getItem("branch")
                },
                async: true,
                success: function(result) {
                    console.warn(395);
                    // var varData = result.data[name]["var_table"];
                    // var svg = null;
                    // renderBars(svg, varData, id, true);
                    // initList = [id];
                    // buttonInit(initList);
                    // renderTable(varData, id);
                    // tool_button.changeTd();
                    // controlMap[id].start = {};
                    // controlMap[id].end = {};
                    // //设置按钮不可用
                    // $("#merge_" + id).attr("disabled", "disabled");
                    // //隐藏等待提示
                    // $(".spinner").css('display', 'none');
                    //
                    // $("#"+name).val(result.data[name]["iv"]);
                    self.adjustTable(result)
                }
            });
        }
    },
    mounted() {
        const {
            valName,
            iv,
            num,
            table_head,
            varData
        } = this.itemData;
        const self = this;


        $(".apply-checks").iCheck({
            checkboxClass: 'icheckbox_square-green variable_apply'
        });
        this.$nextTick(() => {
            this.buttonInit([num]);
        })

    },
    props: ['item'],
    components: {
        ItemChart,
        // ItemTable
    }
}
</script>

<style lang="css" scoped>
.item-top {
    margin:20px 0px 5px;
    padding: 5px 10px 0;
    border-top: 1px solid #e7eaec;
}
</style>
