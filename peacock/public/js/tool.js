var height = 500,
    width = 500,
    margin = 25;

var host = "http://localhost:8091";

var controlMap = {};

var padding = {left: 25, right: 30, top: 5, bottom: 20};

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

var column_cateMap =
    {
        "bin_num": 0, "name": 1,
        "bads": 2, "goods": 3,
        "total": 4, "total_perc": 5,
        "bad_rate": 6, "woe": 7,
        "type": 8
    };

//描绘一个画布
var xScale, yScale;

define(['jquery', 'd3', 'tool_button'], function ($, d3, tool_button) {
    /**
     * 数据图形化初始化
     * 展示选中的variable的woe值分布
     */
    function init() {
        //注册右下方按钮的点击事件
        tool_button.output();
        //提醒等待的样式
        $(".spinner").css('display', 'block');
        //图形部分位于analyze的div中,初始化前需要将原有数据清空
        $("#analyze").html("");

        var branch = $("#branch").val();
        var model_name = $("#model").val();
        if (branch != null && model_name != null) {
            localStorage.setItem("branch", branch);
            localStorage.setItem("model_name", model_name);
        } else {
            branch = localStorage.getItem("branch");
            model_name = localStorage.getItem("model_name");
        }


        $.ajax({
            url: host + "/tool/init",
            type: 'post',
            data: {
                branch: branch,
                model_name: model_name
            },
            async: true,
            success: function (result) {
                var num = 0;
                var initList = [];
                //通过变量名获取数据
                for (var valName in result.data) {
                    var varData = result.data[valName]["var_table"];
                    var iv = result.data[valName]["iv"];
                    var table_head = [];
                    for (var subKey in varData[0]) {
                        table_head.push(subKey);
                    }
                    var svg = initPanel(valName, iv, num, table_head);
                    //画出x轴
                    renderXAxis(svg, num, varData);
                    //画出y轴
                    renderYAxis(svg, num, varData);
                    //绘制坐标轴内的bar和table
                    renderBody(svg, varData, num);
                    initList.push(num);
                    num++;
                }
                //记录行数
                $("#rowNum").val(num);
                //设置table内的标签可以点击
                tool_button.changeTd();
                //初始化按钮,有合并和分裂的操作
                buttonInit(initList);
                $(".spinner").css('display', 'none');

            }
        });
    }

    function initPanel(rowName, iv, num, table_head) {
        var h5 = d3.select("body")
            .select("div").append("h5");
        h5.text(rowName + "-------");

        h5.append("span").attr("class", "iv").attr("id", rowName).text(iv);
        //设置画布
        svg = d3.select("body")
            .select("div")
            .append("div")
            .attr("id", "svg_" + num)
            .style("width", "2000")
            .attr("class", "svg-content")
            .append("svg")
            .attr("class", "axis")
            .attr("id", "my_svg_" + num)
            .attr("width", width)
            .attr("height", height);

        //设置与之对其的表格
        var p_table = d3.select("#svg_" + num)
            .append("div")
            .attr("class", "sheet");

        var table = p_table.append("table")
            .attr("class", "table table-bordered");

        var tr = table.append("thead").append("tr");
        for (var head of table_head) {
            tr.append("td").text(head);
        }

        var tbody = table.append("tbody").attr("id", "tbody_" + num);


        //设置按钮
        var buttonDiv = d3.select("body")
            .select("div")
            .append("div");

        buttonDiv.append("button")
            .attr("class", "btn btn-primary var-btn")
            .attr("id", "merge_" + num)
            .attr("name", rowName)
            .attr("disabled", "disabled")
            .text("合并")
            .style("margin-left", "25px");

        buttonDiv.append("button")
            .attr("class", "btn btn-danger var-btn")
            .attr("id", "divide_" + num)
            .attr("name", rowName)
            .text("分裂");

        buttonDiv.append("button")
            .attr("class", "btn btn-warning var-btn")
            .attr("id", "save_" + num)
            .attr("name", rowName)
            .text("保存记录");

        buttonDiv.append("button")
            .attr("class", "btn btn-success var-btn")
            .attr("id", "divide_manually_" + num)
            .attr("name", rowName)
            .text("手动分裂");

        var inputDiv = buttonDiv
            .append("div")
            .attr("id", "divide_area_" + num)
            .style("display", "none");

        inputDiv.append("textarea")
            .attr("rows", 5)
            .attr("cols", 140)
            .attr("id", "manual_input_" + num)
            .attr("class", "var-area");
        inputDiv.append("button")
            .attr("id","manual_btn_"+num)
            .attr("class", "btn btn-primary var-area-btn")
            .attr("name",rowName)
            .text("提交");


        return svg
    }

    /**
     * 描绘x轴
     * 需要指定画布(svg),variable的下标,variable的相关数据
     */
    function renderXAxis(svg, num, data) {
        var axisLength = width - 2 * margin;
        var min = 0, max = 0;
        for (var obj of data) {
            if (parseFloat(min) > parseFloat(obj.woe)) {
                min = obj.woe;
            }
            if (parseFloat(max) < parseFloat(obj.woe)) {
                max = obj.woe;
            }
        }
        //设定比例
        xScale = d3.scaleLinear()
            .domain([min * 2, max * 2])//数值范围
            .range([0, axisLength]);//该范围在屏幕内展示的长度

        //ticks表示我们希望的刻度数,会根据数值调整
        var axis = d3.axisBottom(xScale).ticks(5);

        svg.select("#x_" + num).remove();
        svg.append("g")
            .attr("class", "x-axis")
            .attr("id", "x_" + num)
            .attr("transform", function () {
                return "translate(" + margin + "," + 475 + ")";
            }).call(axis);


        d3.selectAll("g.x-axis g.tick")
            .attr("stroke", "#777")
            .attr("stroke-dasharray", "2,2") //设置虚线
            .append("line")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", -(height - 2 * margin));
    }

    /**
     * 描绘y轴
     */
    function renderYAxis(svg, num, data) {
        var axisLength = width - 2 * margin;
        //设定比例
        yScale = d3.scaleBand()
            .range([0, axisLength]).padding(0.1);
        yScale.domain(data.map(function (d) {
            return d.bin_num;
        }));

        var axis = d3.axisLeft(yScale);

        svg.select("#y_" + num).remove();
        svg.append("g")
            .attr("class", "y-axis")
            .attr("id", "y_" + num)
            .attr("transform", function () {
                return "translate(" + margin + "," + margin + ")";
            }).call(axis);


        d3.selectAll("g.x-axis")
        //            .attr("stroke", "#777")
        //            .attr("stroke-dasharray", "2,2") //设置虚线
            .append("line")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", axisLength)
            .attr("y2", 0);
    }


    /**
     * 初始化合并操作按钮
     * @param initList 一个列表,通过该列表指定需要绑定事件的按钮
     * 例如 [0,1,2] ,初始化三个前三个variable的合并按钮
     */
    function buttonInit(initList) {
        /**
         * 前后点击两次点击bar,可以多选,第三次会重置,重新开始选择
         */
        //初始化每一个variable的点击事件,预先初始化相关变量
        for (var a of initList) {
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
            $("#divide_" + a).click(function () {
                $(".spinner").css('display', 'block');
                var data = {};
                var id = $(this).attr("id").split("_")[1];
                var start = controlMap[id].start;
                var name = $(this).attr("name");

                debugger;
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
                        "data": JSON.stringify(data)
                    },
                    async: true,
                    success: function (result) {
                        adjustTable(result, id, initList, name)
                    }
                });
            });

            /**
             * 保存选择的集合
             */
            $("#save_" + a).click(function () {
                $(".spinner").css('display', 'block');
                var data = {};
                var content = {};

                var id = $(this).attr("id").split("_")[1];
                var name = $(this).attr("name");
                var iv = $("#" + name).text();

                data[name] = content;

                //获得所有的行数据
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

                var var_table = [];
                content.iv = iv;
                content.var_table = var_table;

                for (var i = 0; i < childs.length; i++) {
                    var tds = $(childs.get(i)).children("td");
                    var obj = {};
                    for (var j = 0; j < tds.length; j++) {
                        obj[columnMap[j]] = tds.get(j).innerHTML;
                    }
                    var_table.push(obj);
                }


                $(".spinner").css('display', 'block');
                $.ajax({
                    url: host + "/tool/db/save",
                    type: 'post',
                    data: {
                        data: JSON.stringify(data),
                        model_name: localStorage.getItem("model_name"),
                        branch: localStorage.getItem("branch")
                    },
                    async: true,
                    success: function (result) {
                        $(".spinner").css('display', "none");
                    }
                });
            });

            /**
             * 合并选择的集合
             */
            $("#merge_" + a).click(function () {
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
                debugger;
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
                        debugger;
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
                        "type": type
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
                var id = $(this).attr("id").split("_")[2];
                var input_area_id = "#divide_area_"+id;
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
                    
                    $("#manual_btn_"+id).click(function () {
                        debugger;
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
                                "model_name": model_name,
                                "type": isCate
                            },
                            async: true,
                            success: function (result) {
                                adjustTable(result, id, initList, name)
                            },
                            error: function (result) {
                                $(".spinner").css('display', 'none');
                            }
                        });
                    })
                    
                } else{
                    d3.select(input_area_id).style("display", "none");
                }


            })

        }
    }

    function adjustTable(result, id, initList, name) {
        debugger;
        var varData = result.data[name]["var_table"];
        var svg = null;
        renderBars(svg, varData, id, true);
        initList = [id];
        buttonInit(initList);
        renderTable(varData, id);
        tool_button.changeTd();
        controlMap[id].start = {};
        controlMap[id].end = {};
        //设置按钮不可用
        $("#merge_" + id).attr("disabled", "disabled");
        //隐藏等待提示
        $(".spinner").css('display', 'none');

        $("#" + name).text(result.data[name]["iv"]);
    }

    function renderBody(svg, data, num) {
        renderBars(svg, data, num, false);
        renderTable(data, num);

    }

    function renderTable(data, num) {
        var tbody = d3.select("#tbody_" + num);
        tbody.selectAll("tr").remove();
        var height;
        if (data.length == 1) {
            height = yScale(data[0].bin_num);
        } else {
            height = yScale(data[1].bin_num) - yScale(data[0].bin_num);
        }
        var index = 0;
        for (var obj of data) {
            //设置每一行的高度
            var tr = tbody.append("tr").attr("height", height);

            //根据结果添加列,并设置宽度
            for (var key in obj) {
                var td = tr.append("td");

                if (index == 0 || index == obj.length - 2) {
                    if (key == 'min_bound') {
                        td = td.attr("name", key);
                    }
                } else if (index == data.length - 2) {
                    if (key == 'max_bound') {
                        td = td.attr("name", key);
                    }
                }
                if (key == 'woe') {
                    td = td.attr("name", key);
                }

                td.style("text-align", "center")
                    .style("vertical-align", "middle").text(obj[key]);

            }
            index++;
        }
    }

    /**
     *
     * @param svg 画布
     * @param data 单个variable的数据
     * @param num  variable的下标
     * @param isSingle 如果是初始化所有数据,
     */
    function renderBars(svg, data, num, isSingle) {
        var bar;
        if (isSingle == false) {
            bar = svg.selectAll(".MyRect");
            svg.selectAll(".MyRect").data(data)
                .enter()
                .append("rect")
                .attr("class", "MyRect")
                .attr("fill", "#000000")//设定bar的颜色
                .attr("id", function (d, i) {//绑定id
                    return "index_" + num + "_" + i;
                })
                .attr("transform", "translate(" + padding.left + "," + 40 + ")")//设置偏移位置
                .attr("x", function (d) {
                    return xScale(Math.min(0, d.woe)); //根据woe值设置x轴的位置
                })
                .attr("y", function (d) {
//                console.log(yScale(d.bin_num));
                    return yScale(d.bin_num);//根据bin_num的值设定y轴的值
                })
                .attr("width", function (d) {
                    return Math.abs(xScale(d.woe) - xScale(0));//根据woe设置宽度
                })
                .attr("height", function (d) {
                    return 230 / data.length;//设置高度
                });
        }
        else {
            svg = d3.select("#my_svg_" + num);
            renderXAxis(svg, num, data);
            renderYAxis(svg, num, data);
            svg.selectAll(".MyRect").remove();
            svg.selectAll(".MyRect").data(data)
                .enter()
                .append("rect")
                .attr("class", "MyRect")
                .attr("fill", "#000000")//设定bar的颜色
                .attr("id", function (d, i) {//绑定id
                    return "index_" + num + "_" + i;
                })
                .attr("transform", "translate(" + padding.left + "," + 40 + ")")//设置偏移位置
                .attr("x", function (d) {
                    return xScale(Math.min(0, d.woe)); //根据woe值设置x轴的位置
                })
                .attr("y", function (d) {
//                console.log(yScale(d.bin_num));
                    return yScale(d.bin_num);//根据bin_num的值设定y轴的值
                })
                .attr("width", function (d) {
                    return Math.abs(xScale(d.woe) - xScale(0));//根据woe设置宽度
                })
                .attr("height", function (d) {
                    return 230 / data.length;//设置高度
                });
        }
    }

    function getUtf8Length(str) {
        if (str == "" || str == null)
            return 0;
        var n = 0;
        len = 0;
        for (i = 0; i < str.length; i++) {
            n = str.charCodeAt(i);
            if (n <= 255)
                len += 1;
            else
                len += 3;
        }
        return len;
    }

    return {
        init: init
    };
});

