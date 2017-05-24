<template lang="html">
    <div class="side-bar">
        <!-- <a id="getBar" class="bar">getBar</a> -->
        <router-link to="/bar">getBar</router-link>
        <a id="history">history</a>
        <a id="output">apply</a>
        <a href="#bar" id="columnConfig" class="bar">column</a>
        <router-link to="/uploaded">prevInit</router-link>
        <a @click="goSelect">select</a>
    </div>
</template>

<script>
var host = 'http://localhost:8091'

function exportData(exportSelected) {
    var row = $("#rowNum").val();
    var data = {};
    for (var i = 0; i < row; i++) {
        var name = $('#merge_' + i).attr("name");
        if (exportSelected) {
            if ($("#" + name + "_name").find(".checked").length <= 0) {
                continue;
            }
        }
        var innerList = [];
        data[name] = innerList;

        var childTrs = $('#tbody_' + i).children("tr");
        for (var innerRow = 0; innerRow < childTrs.length; innerRow++) {
            var innerDate = {};
            innerList.push(innerDate);
            var tds = $(childTrs.get(innerRow)).children("td");
            var category_t = tds.get(tds.length - 1).innerHTML;
            if (category_t.indexOf("Numerical") >= 0) {
                var max = tds.get(maxIndex).innerHTML;
                var min = tds.get(minIndex).innerHTML;
                var minBound = tds.get(minBoundIndex).innerHTML;
                var maxBound = tds.get(maxBoundIndex).innerHTML;
                innerDate["max"] = max;
                innerDate["min"] = min;
                innerDate["min_boundary"] = minBound;
                innerDate["max_boundary"] = maxBound;
            } else {
                var ca = tds.get(categoricalIndex).innerHTML;

                innerDate[name] = ca.split('|');
            }
            var binNum = $(childTrs.get(innerRow)).children("td").get(binNumIndex).innerHTML;
            innerDate["woe"] = tds.get(tds.length - 2).innerHTML;
            innerDate["binNum"] = binNum;
            innerDate["type"] = category_t;
        }
    }

    return data
}

export default {
    methods: {
        goSelect() {
            const self = this;
            $.ajax({
                url: host + "/tool/if_applyed",
                type: 'post',
                async: false,
                success: function(result) {
                    if (!result.success) {
                        alert("you have not applyed yet");
                    } else {
                        self.$router.push('/select')
                    }
                }
            });
        }
    },
    mounted() {
        $("#output").click(function() {
            $("#downloadform").remove();
            var form = $("<form>"); //定义一个form表单
            form.attr("id", "downloadform");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", host + "/tool/apply");
            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("name", "data");

            // var data ={
            //     "data": JSON.stringify(exportData())
            // };
            const o = {};
            o.data = exportData(true);
            o.target = localStorage.getItem("target");
            o.modelName = localStorage.getItem("model_name");
            o.branch = localStorage.getItem("branch");
            input1.attr("value", JSON.stringify(o));
            form.append(input1);
            $("body").append(form); //将表单放置在web中

            form.submit(); //表单提交

            // $.ajax({
            //     url: host+"/tool/apply",
            //     type: 'post',
            //     data: {
            //         "data": JSON.stringify(exportData())
            //     },
            //     async: true,
            //     success: function (result) {
            //
            //     }
            // });
        });

        $("#columnConfig").bind("click", function() {
            var params = [];
            $("#variableSelected").find("tbody tr").find("td:eq(2)").each(function() {
                params.push($(this).text())
            });
            var list = [];
            $(".selected-body").each(function() {
                list.push($(this).find(".selected-body-checks").attr("name"))
            });

            $("#columnConfigform").remove();
            var form = $("<form>"); //定义一个form表单
            form.attr("id", "columnConfigform");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", host + "/tool/column_config");
            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("name", "data");

            // var data ={
            //     "data": JSON.stringify(exportData())
            // };
            var o = {};
            o.params = params.join(",");
            o.list = list.join(",");
            o.model_branch = localStorage.getItem("branch");
            o.model_name = localStorage.getItem("model_name");
            input1.attr("value", JSON.stringify(o));
            form.append(input1);
            $("body").append(form); //将表单放置在web中

            form.submit(); //表单提交

            // $.ajax({
            //     url: host + "/tool/column_config",
            //     type: 'post',
            //     data: {list: list.join(",")},
            //     async: true,
            //     success:function (result) {
            //
            //     }
            // });
        })
    }
}
</script>

<style lang="css">  
.side-bar a {
    color: #333;
    font-size: 15px;
    line-height: 66px;
    text-align: center;
    text-decoration: none;
    padding: 0 !important;
    cursor: pointer;
}
.side-bar a:hover {
    color: #fff;
}
</style>
