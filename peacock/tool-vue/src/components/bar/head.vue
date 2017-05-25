<template lang="html">
  <div class="bar-head">
    <h1>Binning Function</h1>
    <b>{{branch}}</b> /
    <b>{{modelName}}</b>
    <span @click="initBar">初始化</span>
    <span @click="saveAll">保存所有</span>
    <span @click="loadAll">读取所有</span>
    <span @click="selectAll">选取所有</span>
    <span @click="rank">排序</span>
    <span @click="exportFunc">导出</span>
    <span @click="variableExport">导出已选变量</span>
  </div>
</template>

<script>
const host = 'http://localhost:8091'
let maxIndex = 2;
let minIndex = 1;
let minBoundIndex = 3;
let maxBoundIndex = 4;
let woeIndex = 10;
let binNumIndex = 0;
let cateIndex = 11;
let categoricalIndex = 1;

function exportData(exportSelected) {
    var row = localStorage.getItem('rowNum');
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




// exportDataWithIV
function exportDataWithIV() {
    var row = localStorage.getItem('rowNum');
    var data = {};
    for (var i = 0; i < row; i++) {
        var name = $('#merge_' + i).attr("name");
        var innerObj = {};
        innerObj.var_table = [];
        innerObj.iv = $("#" + name).text();
        data[name] = innerObj;

        var childTrs = $('#tbody_' + i).children("tr");
        if ($("#" + name + "_name").find(".variable_apply.checked").length > 0) {
            innerObj.is_selected = 1
        } else {
            innerObj.is_selected = 0
        }
        for (var innerRow = 0; innerRow < childTrs.length; innerRow++) {
            var innerDate = {};
            innerObj.var_table.push(innerDate);
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
                innerDate[name] = tds.get(categoricalIndex).innerHTML;
            }
            var binNum = $(childTrs.get(innerRow)).children("td").get(binNumIndex).innerHTML;
            innerDate["woe"] = tds.get(tds.length - 2).innerHTML;
            innerDate["bad_rate"] = tds.get(tds.length - 3).innerHTML;
            innerDate["total_perc"] = tds.get(tds.length - 4).innerHTML;
            innerDate["total"] = tds.get(tds.length - 5).innerHTML;
            innerDate["goods"] = tds.get(tds.length - 6).innerHTML;
            innerDate["bads"] = tds.get(tds.length - 7).innerHTML;
            innerDate["bin_num"] = binNum;
            innerDate["type"] = category_t;
        }
    }

    return data
}


export default {
    data() {
        return {
            checkAllFlag: false
        }
    },
    computed: {
        branch() {
            console.warn();
            return localStorage.getItem("branch")
        },
        modelName() {
            return localStorage.getItem("model_name")
        }
        // return  {
        //     branch: localStorage.getItem("branch"),
        //     modelName: localStorage.getItem("model_name")
        // }
    },
    methods: {
        initBar() {
            console.warn(16);
            //注册右下方按钮的点击事件
            //提醒等待的样式
            $(".spinner").css('display', 'block');
            //图形部分位于analyze的div中,初始化前需要将原有数据清空
            $("#analyze").html("");

            // var div = content.append("div");
            // div.append("h5").text("123");
            const self = this;

            $.ajax({
                url: host + "/tool/init",
                type: 'post',
                data: {
                    branch: this.branch,
                    modelName: this.modelName
                },
                async: true,
                success: function(result) {
                    self.$root.$emit('initBar', result);
                }
            });
        },
        saveAll() {
            $(".spinner").css('display', 'block');
            $.ajax({
                url: host + "/tool/db/save",
                data: {
                    "branch": this.branch,
                    "model_name": this.modelName,
                    "data": JSON.stringify(exportDataWithIV())
                },
                type: 'post',
                async: true,
                success: function(result) {
                    $(".spinner").css('display', 'none');
                },
                error: function() {
                    alert("保存出错");
                }
            });
        },
        loadAll() {
            $(".spinner").css('display', 'block');
            const self = this;
            $.ajax({
                url: host + "/tool/db/load_all",
                data: {
                    "branch": this.branch,
                    "model_name": this.modelName
                },
                type: 'post',
                async: true,
                success: function(result) {
                    $("#analyze").html("");
                    self.$root.$emit('initBar', result);
                },
                error: function() {
                    alert("读取出错");
                }
            });
        },
        selectAll() {
            if (!this.checkAllFlag) {
                $(".apply-checks").iCheck("check");
            } else {
                $(".apply-checks").iCheck("uncheck");
            }
            this.checkAllFlag = !this.checkAllFlag;
        },
        rank() {
            var data = exportDataWithIV();
            const self = this;
            $.ajax({
                url: host + "/tool/rank",
                data: {
                    "data": JSON.stringify(data)
                },
                type: 'post',
                async: true,
                success: function(result) {
                    $("#analyze").html("");
                    self.$root.$emit('initBar', result);
                },
                error: function() {
                    alert("读取出错");
                }
            });
        },
        exportFunc() {
            var data = exportData();

            $("#downConfig").remove();
            var form = $("<form>"); //定义一个form表单
            form.attr("id", "downConfig");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", host + "/tool/export");
            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("name", "data");

            input1.attr("value", JSON.stringify(data));
            form.append(input1);
            $("body").append(form); //将表单放置在web中

            form.submit(); //表单提交
        },
        variableExport() {
            $("#downVariable").remove();
            var data = {};
            data.model_name = localStorage.getItem("model_name");
            data.branch = localStorage.getItem("branch");
            data.type = "xlsx";
            var form = $("<form>"); //定义一个form表单
            form.attr("id", "downVariable");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", host + "/tool/export_selected_variable");
            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("name", "data");

            input1.attr("value", JSON.stringify(data));
            form.append(input1);
            $("body").append(form); //将表单放置在web中

            form.submit(); //表单提交
        }
    },
    created() {
        console.warn(48);
        var branch = $("#branch").val();
        var model_name = $("#model").val();
        var target = $("#target").val();
        if (branch != null && model_name != null) {
            localStorage.setItem("branch", branch);
            localStorage.setItem("model_name", model_name);
            localStorage.setItem("target", target);
        } else {
            branch = localStorage.getItem("branch");
            model_name = localStorage.getItem("model_name");
            target = localStorage.getItem("target");
        }

        if (target === null || target === '') {
            alert("target is null");
        }

    }
}
</script>

<style lang="css" scoped>
span {
    cursor: pointer;
    font-weight: bold;
    margin-left: 10px;
    color: #555;
}
span:hover {
    color: #000;
}
.bar-head {
    margin-left: 10px;
}
</style>
