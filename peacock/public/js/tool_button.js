maxIndex = 2;
minIndex = 1;
minBoundIndex = 3;
maxBoundIndex = 4;
woeIndex = 10;
binNumIndex = 0;
cateIndex = 11;
categoricalIndex = 1;
branches = null;

var host = "http://localhost:8091";

define(['jquery', 'd3', 'i-checks', 'select2'], function ($, d3) {
    function outputDateMap() {
        $("#output").click(function () {
                $("#downloadform").remove();
                var form = $("<form>");//定义一个form表单
                form.attr("id", "downloadform");
                form.attr("style", "display:none");
                form.attr("target", "");
                form.attr("method", "post");
                form.attr("action", host+"/tool/apply");
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
            url: host+"/tool/parse",
            type: 'get',
            async: true,
            success: function (result) {
                addLabel("#dataframe", "model",false);
                addLabel("#dataframe", "branch",false);
                addLabel("#dataframe", "target",true);

                $("#branch-commit").click(function () {
                    var remove_list = {};
                    var target = $('#target').val();
                    var branch = $('#branch').val();
                    var model_name = $("#model").val();
                    /**
                     * 将被选中的variable添加到removeList中
                     */
                    $("#dataframe").find("tbody .checked").each(function (i,n) {
                        remove_list[$(n).parents("tr").children().eq(1).html()] = i;
                    });
                    $(this).attr("disabled","disabled");
                    $.ajax({
                        url: host+"/tool/db/branch/commit-branch",
                        type: 'post',
                        data:{remove_list:JSON.stringify(remove_list),target:target,branch:branch,model_name:model_name},
                        async: true,
                        success: function (result) {
                            $("#branch-commit").removeAttr("disabled");
                        },
                        error: function () {
                            $(this).removeClass("btn-primary");
                            $(this).addClass("btn-warning")
                        }
                    });
                });


                var table = d3.select("#dataframe").append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");

                var data = result.data;
                var thead = table.append("thead");
                var tbody = table.append("tbody");
                var tr = thead.append("tr");

                /**
                 * 添加头部的select标签栏
                 */
                branches = data["branches"];
                for (let obj of branches) {
                    d3.select("#branch").append("option").text(obj)
                }
                d3.select("#model").append("option").text(data["current_model"]);

                $("#branch").on("select2:closing", function (e) {
                   var current = $(this).val();
                    for (let obj of branches) {
                        //当该分支已经存在时
                        if (current == obj) {
                            $.ajax({
                                url: host+"/tool/db/branch/checkout",
                                data:{"branch":current,"model_name":$("#model").val()},
                                type: 'get',
                                async: false,
                                success: function (result) {
                                    clearAndSet(result.data.remove_list);
                                    $('#target').val(result.data.model_target).trigger("change")
                                }
                            });
                            return;
                        }
                    }
                    branches.push(current);
                    d3.select("#branch").append("option").text(current);
                    $.ajax({
                        url: host+"/tool/db/branch",
                        data:{"branch":current,"model_name":$("#model").val()},
                        type: 'post',
                        async: true,
                        success: function (result) {
                            branches.push(newBranch);
                            d3.select("#branch").append("option").text(newBranch);
                        }
                    });
                });


                //head处加入一列,用于控制标签的全选
                var headTd = tr.append("td").append("div");
                headTd.append("input")
                    .attr("type", "checkbox")
                    .attr("id", "head-checks")
                    .attr("name", "input[]");

                for (var a of data.head) {
                    tr.append("td").text(a);
                }

                //绘制table
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
                    d3.select("#target").append("option").text(b[0]);
                }

                $('#target').val(data["target"]).trigger("change");



                $('.i-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green',
                    radioClass: 'iradio_square-green',
                });


                setSelected(data.remove_list,b[0]);

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
                // $("#dataframe").find("tbody tr").find("td:eq(1)").click(function () {
                //     //.icheckbox_square-green负责按钮的渲染
                //     var radio = $($(this).parent().find(".icheckbox_square-green"));
                //     if (radio.hasClass('checked')) {
                //         radio.iCheck('uncheck');
                //     } else {
                //         radio.iCheck('check');
                //     }
                // });
            }
        });
    }


    $("#init").click(function () {
        $('#preDefine').html("");
        init();
    });


    $("#columnConfig").click(function () {
        $.ajax({
            url: host+"/tool/column-config",
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


    function addLabel(parent, labelName,isLast) {
        var varSelect = d3.select(parent)
            .append("div")
            .attr("class", "table-line");
        // .attr("class","form-group");
        varSelect
            .append("div")
            .style("display", "inline-block")
            .attr("class", "table-label")
            .append("span")
            .text(labelName);
        varSelect
            .append("div")
            .style("display", "inline-block").append("select")
            .attr("class", "table-ele")
            .attr("id", labelName);

        if (isLast) {
            varSelect
                .append("div")
                .style("display", "inline-block").append("button")
                .attr("class", "btn-primary")
                .attr("id", "branch-commit")
                .style("margin-left","20px")
                .text("commit")
        }

        $("#" + labelName).select2({tags: true});
    }

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

    function setSelected(removeList) {
        var remove_list = JSON.parse(JSON.parse(removeList));
        $("#dataframe").find("tbody tr").each(function (i,n) {
            if (remove_list[$(n).children().eq(1).html()] != undefined) {
                $(n).children().eq(0).iCheck('check');
            }
        });
    }

    function clearAndSet(removeList) {
        var remove_list = JSON.parse(JSON.parse(removeList));
        $("#dataframe").find("tbody tr").each(function (i,n) {
            $(n).children().eq(0).iCheck('uncheck');
            if (remove_list[$(n).children().eq(1).html()] != undefined) {
                $(n).children().eq(0).iCheck('check');
            }
        });
    }

    return {
        output: outputDateMap,
        changeTd: changeTd,
        getTable: getTable
    }
});








