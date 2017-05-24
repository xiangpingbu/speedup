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

var host = "http://localhost:8091";

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
var finishInit= false;

//numerical的列的排布
var num_columnMap =
    {
        0: "bin_num", 1: "min",
        2: "max", 3: "min_boundary",
        4: "max_boundary", 5: "bads",
        6: "goods", 7: "total",
        8: "total_perc", 9: "bad_rate",
        10: "woe", 11: "type"
    };

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
//categorical的列的排布
var cate_columnMap =
    {
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
        function buttonInit(initList) {
            // debugger;
            // $(".var-btn").unbind("click");
            /**
             * 前后点击两次点击bar,可以多选,第三次会重置,重新开始选择
             */
            //初始化每一个variable的点击事件,预先初始化相关变量
            for (var a of initList) {
                $("#merge_" + a).unbind("click");
                $("#divide_" + a).unbind("click");
                controlMap[a] = {};
                var map = controlMap[a];
                map.start = {};
                map.end = {};
                map.array = [$("#index_" + a)];
                var rects = $('#svg_' + a).find('.MyRect').click(function () {
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

                /**
                 * 分裂选择的bar
                 */
                $("#divide_" + a).bind("click", function () {
                    $(".spinner").css('display', 'block');
                    var data = {};
                    var id = $(this).attr("id").split("_")[1];
                    var start = controlMap[id].start;
                    var name = $(this).attr("name");

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
                    $(".spinner").css('display', 'block');
                    $.ajax({
                        url: host + "/tool/divide",
                        type: 'post',
                        data: {
                            "data": JSON.stringify(data),
                            "target": localStorage.getItem("target"),
                            "modelName":localStorage.getItem("model_name"),
                            "branch":localStorage.getItem("branch")
                        },
                        async: true,
                        success: function (result) {
                            $(this).unbind();
                            adjustTable(result, id, initList, name)
                        }
                    });
                });

                /**
                 * 合并选择的集合
                 */
                $("#merge_" + a).bind("click", function () {
                    var id = $(this).attr("id").split("_")[1];
                    var name = $(this).attr("name");
                    var start = controlMap[id].start;
                    var end = controlMap[id].end;
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
                        }
                        else valIndex = categoricalIndex;

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
                    $.ajax({
                        url: host + "/tool/merge",
                        type: 'post',
                        data: {
                            "varName": name,
                            "boundary": list,
                            "allBoundary": wholeList,
                            "type": type,
                            "target": localStorage.getItem("target"),
                            "modelName":localStorage.getItem("model_name"),
                            "branch":localStorage.getItem("branch")
                        },
                        async: true,
                        success: function (result) {
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
                            adjustTable(result, id, initList, name)
                        }
                    });
                });

                $("#divide_manually_" + a).click(function () {
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
                        $('#tbody_' + id).find("tr").each(function (i, n) {
                            str.push($(n).children().eq(index).html());
                        });
                        $("#manual_input_" + id).text(str.join(","));


                    } else {
                        d3.select(input_area_id).style("display", "none");
                    }

                    $("#manual_btn_" + id).bind("click", function () {
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
                            success: function (result) {
                                adjustTable(result, id, initList, name);
                                $(".spinner").css('display', 'none');
                            },
                            error: function (result) {
                                $(".spinner").css('display', 'none');
                            }
                        });
                    });
                });


            }
        }
        
        function adjustTable(result, id, initList, name) {
            // console.warn(393);
            var varData = result.data[name]["var_table"];
            // var svg = null;
            // renderBars(svg, varData, id, true);
            console.warn(445);
            console.warn(id);
            const temp = JSON.parse(JSON.stringify(self.barItemList))
            temp[1] = varData;
            self.barItemList = temp;
            initList = [id];
            buttonInit(initList);
            // renderTable(varData, id);
            // changeTd();
            controlMap[id].start = {};
            controlMap[id].end = {};
            //设置按钮不可用
            $("#merge_" + id).attr("disabled", "disabled");
            //隐藏等待提示
            $(".spinner").css('display', 'none');
            // $("#" + name).text(result.data[name]["iv"]);
        }

        this.$root.$on('initBar', (result) => {
            console.warn(574);
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
            this.$nextTick(() => {
                setTimeout(() => {
                    buttonInit(initList);
                }, 1000);
            })
            $(".spinner").css('display', 'none');
        })
        // initHead();
        // commitBranch();
    }
}
</script>

<style lang="css">
</style>
