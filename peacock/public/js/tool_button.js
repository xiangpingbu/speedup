maxIndex = 2;
minIndex = 1;
minBoundIndex = 3;
maxBoundIndex = 4;
woeIndex = 10;
binNumIndex = 0;
cateIndex = 11;

categoricalIndex = 1;

define(['jquery', 'd3', 'i-checks','select2'], function ($, d3) {
    function outputDateMap() {
        $("#output").click(function () {
                $("#downloadform").remove();
                var form = $("<form>");//定义一个form表单
                form.attr("id", "downloadform");
                form.attr("style", "display:none");
                form.attr("target", "");
                form.attr("method", "post");
                form.attr("action", "http://localhost:8091/tool/apply");
                var input1 = $("<input>");
                input1.attr("type", "hidden");
                input1.attr("name", "data");

                // var data ={
                //     "data": JSON.stringify(exportData())
                // };

                input1.attr("value", JSON.stringify(exportData()));
                form.append(input1);
                $("body").append(form);//将表单放置在web中

                form.submit();//表单提交

                // $.ajax({
                //     url: "http://localhost:8091/tool/apply",
                //     type: 'post',
                //     data: {
                //         "data": JSON.stringify(exportData())
                //     },
                //     async: true,
                //     success: function (result) {
                //
                //     }
                // });
            }
        )
    }

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

        $("td[name='min_bound']").click(function () {
            if (!$(this).is('.input')) {
                $(this).addClass("input")
                    .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                    .find('input').focus().blur(function () {
                    $(this).parent().removeClass('input').html($(this).val() || 0);
                });
            }
        });

        $("td[name='max_bound']").click(function () {
            if (!$(this).is('.input')) {
                $(this).addClass("input")
                    .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                    .find('input').focus().blur(function () {
                    $(this).parent().removeClass('input').html($(this).val() || 0);
                });
            }
        });
    }

    $("#prev").click(function () {
        getTable();
    });

    /**
     * 预处理数据并获取表格
     */
    function getTable() {
        $("#dataframe").html("");
        $.ajax({
            url: "http://localhost:8091/tool/parse",
            type: 'get',
            async: true,
            success: function (result) {
                var varSelect = d3.select("#dataframe")
                    .append("div").attr("class","form-group");
                varSelect.append("span").attr("class","col-sm-1")
                    .text("target");
                varSelect
                    .append("div").
                    attr("class","col-sm-2").
                append("select").attr("class","form-control").attr("id","target");


                var table = d3.select("#dataframe").append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");

                var data = result.data;
                var thead = table.append("thead");
                var tbody = table.append("tbody");
                var tr = thead.append("tr");

                //head处加入一列,用于控制标签的全选
                var headTd = tr.append("td").append("div");
                headTd.append("input")
                    .attr("type", "checkbox")
                    .attr("id", "head-checks")
                    .attr("name", "input[]");

                for (var a of data.head) {
                    tr.append("td").text(a);
                }
                for (var b of data.body) {
                    tr = tbody.append("tr");
                    var select = tr.append("td");
                    var div = select.append("div");
                    div.append("input")
                        .attr("type", "checkbox")
                        .attr("class", "i-checks")
                        .attr("name", "input[]");

                    for (var item of b) {
                        tr.append("td").text(item)
                    }

                    $("#target").select2();
                    d3.select("#target").append("option").text(b[0]);

                }
                $('.i-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green',
                    radioClass: 'iradio_square-green',
                });

                /**
                 * 点击头部按钮可以全选或者反选.
                 */
                $('#head-checks')
                    .iCheck({
                        checkboxClass: 'icheckbox_square-green',
                        radioClass: 'iradio_square-green',
                    })
                    .on('ifChecked', function (event) {
                        $(".icheckbox_square-green").iCheck('check');
                    })
                    .on('ifUnchecked', function (event) {
                        $(".icheckbox_square-green").iCheck('uncheck');
                    });

                /**
                 * dataframe行点击事件
                 * 点击一行可以触发radio的勾选和反选
                 */
                $("#dataframe").find("tr").click(function () {
                    //.icheckbox_square-green负责按钮的渲染
                    var radio = $($(this).find(".icheckbox_square-green"));
                    if (radio.hasClass('checked')) {
                        radio.iCheck('uncheck');
                    } else {
                        radio.iCheck('check');
                    }
                });
            }
        });
    }


    $("#init").click(function () {
        $('#preDefine').html("");
        init();
    });


    $("#columnConfig").click(function () {
        $.ajax({
            url: "http://localhost:8091/tool/column-config",
            type: 'post',
            data: {
                "data": JSON.stringify(exportData())
            },
            async: true,
            success: function (result) {

            }
        });
    });

    $("#getBar").click(function () {
        window.location.href = window.location.href.substr(0, window.location.href.indexOf("#")) + "#bar";
    });


    function exportData() {
        var row = $("#rowNum").val();
        var data = {};
        for (var i = 0; i < row; i++) {
            var name = $('#merge_' + i).attr("name");
            var innerList = [];
            data[name] = innerList;

            var childTrs = $('#tbody_' + i).children("tr");
            for (var innerRow = 0; innerRow < childTrs.length; innerRow++) {
                var innerDate = {};
                innerList.push(innerDate);
                var tds = $(childTrs.get(innerRow)).children("td");
                var category_t = tds.get(tds.length - 1).innerHTML;
                if (category_t.indexOf("F") >= 0) {
                    var max = tds.get(maxIndex).innerHTML;
                    var min = tds.get(minIndex).innerHTML;
                    var minBound = tds.get(minBoundIndex).innerHTML;
                    var maxBound = tds.get(maxBoundIndex).innerHTML;
                    innerDate["max"] = max;
                    innerDate["min"] = min;
                    innerDate["min_bound"] = minBound;
                    innerDate["max_bound"] = maxBound;
                } else {
                    var ca = tds.get(categoricalIndex).innerHTML;

                    innerDate[name] = ca.split('|');
                }
                var binNum = $(childTrs.get(innerRow)).children("td").get(binNumIndex).innerHTML;
                innerDate["woe"] = tds.get(tds.length - 2).innerHTML;
                innerDate["binNum"] = binNum;
                innerDate["category_t"] = category_t;
            }
        }

        return data
    }

    return {
        output: outputDateMap,
        changeTd: changeTd,
        getTable: getTable
    }
});









