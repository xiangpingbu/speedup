var height = 500,
    width = 500,
    margin = 25;

var host = "http://192.168.31.42:8091";

var controlMap = {};

var padding = {left: 25, right: 30, top: 5, bottom: 20};

var num_columnMap =
    {
        0: "bin_num",
        1: "min",
        2: "max",
        3: "min_bound",
        4: "max_bound",
        5: "bads",
        6: "goods",
        7: "total",
        8: "total_perc",
        9: "bad_rate",
        10: "woe",
        11: "category_t"
    };


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
        8: "category_t"
    };

//描绘一个画布
var xScale, yScale;

define(['jquery', 'd3', 'tool_button'], function ($, d3, tool_button) {
    function init() {
        tool_button.output();
        $(".spinner").css('display', 'block');
        d3.json("/tool/init", function (error, result) {
            var num = 0;

            var initList = [];
            for (var key in result.data) {
                var table_head = [];
                for (var subKey in result.data[key][0]) {
                    table_head.push(subKey);
                }
                var svg = initPanel(key, num, table_head);
                renderXAxis(svg, num, result.data[key]);
                renderYAxis(svg, num, result.data[key]);
                renderBody(svg, result.data[key], num);
                initList.push(num);
                num++;
            }
            //记录行数
            $("#rowNum").val(num);
            tool_button.changeTd();
            buttonInit(initList);
            $(".spinner").css('display', 'none');
        });
    }

    function initPanel(rowName, num, table_head) {
        d3.select("body")
            .select("div").append("h5").text(rowName);
        //设置画布
        svg = d3.select("body")
            .select("div")
            .append("div")
            .attr("id", "svg_" + num)
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
            .attr("class", "btn btn-primary")
            .attr("id", "merge_" + num)
            .attr("name", rowName)
            .attr("disabled", "disabled")
            .text("合并")
            .style("margin-left", "25px");

        buttonDiv.append("button")
            .attr("class", "btn btn-danger")
            .attr("id", "divide_" + num)
            .attr("name", rowName)
            .text("分裂")
            .style("margin-left", "25px");

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
                var isNum = type.indexOf("F") >= 0;

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

            $("#divide_" + a).click(function () {
                $(".spinner").css('display', 'block');
                var data = {};
                var id = $(this).attr("id").split("_")[1];
                var start = controlMap[id].start;
                var name = $(this).attr("name");

                var childs = $('#tbody_' + id).children("tr");
                var tds_0 = $(childs.get(0)).children("td");
//                var td = $(childs.get(start.index)).children("td");
                var category_t = tds_0.get(tds_0.length - 1).innerHTML;
                var columnMap;
                if (category_t.indexOf('F') >= 0) {
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
                        var svg = null;
                        renderBars(svg, result.data[name], id, true);
                        initList = [id];
                        buttonInit(initList);
                        renderTable(result.data[name], id);
                        tool_button.changeTd();
                        controlMap[id].start = {};
                        controlMap[id].end = {};
                        $("#merge_" + id).attr("disabled", "disabled");
                        $(".spinner").css('display', 'none');
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
                var isNum = type.indexOf("F") >= 0;
                //判断应该从哪一列获取相应的值
                var valIndex;
                if (isNum) valIndex = maxIndex;
                else valIndex = categoricalIndex;

                var iterList = [];
                if (!isNum) iterList = [min, max];
                else {
                    for (var i = min; i <= max; i++) {
                        iterList.push(i);
                    }
                }
                for (var n in iterList) {
                    if (iterList[n] == max) {
                        //如果存在'F',代表type为false,variable为numerical,
                        if (isNum) {
                            //此时该max的值可以被丢弃
                            continue;
                        }
                    }
                    if (isNum) {
//                        valIndex = maxIndex;
                        valIndex = maxBoundIndex;
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
                            //整个区间的大部分从max_bound列中得到,除了inf一列
                            if (index == childTrs.length - 2) {
                                tdVal = $(childTrs.get(index)).children("td").get(maxIndex).innerHTML;
                            } else {
                                tdVal = $(childTrs.get(index)).children("td").get(valIndex).innerHTML
                            }
                            wholeList = wholeList + (tdVal) + ("&");
                        }
                    }else {
                        for (var b = 0;  b< childTrs.length; b++) {
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
                        var svg = null;
                        renderBars(svg, result.data[name], id, true);
                        initList = [id];
                        buttonInit(initList);
                        renderTable(result.data[name], id);
                        tool_button.changeTd();
                        controlMap[id].start = {};
                        controlMap[id].end = {};
                        $("#merge_" + id).attr("disabled", "disabled");
                        $(".spinner").css('display', 'none');

                    }
                });
            });


        }
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
                .attr("id", function (d) {//绑定id
                    return "index_" + num + "_" + d.bin_num;
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
                .attr("id", function (d) {//绑定id
                    return "index_" + num + "_" + d.bin_num;
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

    return {
        init: init
    };
});

