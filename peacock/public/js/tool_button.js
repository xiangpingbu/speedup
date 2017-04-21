maxIndex = 2;
minIndex = 1;
minBoundIndex = 3;
maxBoundIndex = 4;
woeIndex = 10;
binNumIndex = 0;
cateIndex = 11;

categoricalIndex = 1;

define(['jquery', 'd3'],function ($,d3){
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

    $("#prev").click(function(){
        $('#upload').html("");
        $('#analyze').html("");
        $('#dataframe').html("");
        getTable();
    });

    function getTable() {


        var table = d3.select("#dataframe").append("table").attr("class", "table table-bordered");

        $.ajax({
            url: "http://localhost:8091/tool/parse",
            type: 'get',
            async: true,
            success: function (result) {
                var data = result.data;
                var thead = table.append("thead");
                var tbody = table.append("tbody");
                var tr = thead.append("tr");
                for (var a of data.head) {
                    tr.append("td").text(a);
                }
                for (var b of data.body) {
                    tr = tbody.append("tr");
                    for (var item of b) {
                        tr.append("td").text(item)
                    }
                }
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

    $("#getBar").click(function() {
        window.location.href = window.location.href.substr(0,window.location.href.indexOf("#")) + "#bar";
        window.location.reload()
    });


    function  exportData() {
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
                var category_t = tds.get(tds.length-1).innerHTML;
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
                innerDate["woe"] = tds.get(tds.length-2).innerHTML;
                innerDate["binNum"] = binNum;
                innerDate["category_t"] = category_t;
            }
        }

        return data
    }

 return {
     output:outputDateMap,
     changeTd:changeTd,
     getTable:getTable
 }
});









