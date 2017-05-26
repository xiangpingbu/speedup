<template lang="html">
  <div class="upload" id="dataframe">
      <div class="table-line" :name="name" v-for="name in selectList">
          <span class="table-label">{{name}}</span>
          <select class="table-ele" :id="name" name="">
          </select>
      </div>
      <button class="btn btn-primary" @click="submit" name="button">commit</button>
      
  </div>
</template>

<script>
import '@/../static/js/icheck.min'
import '@/../static/js/select2.min'

import {
    host
} from '@/config';
// import tool_button from '@/assets/js/tool_button'
let branches = null;
let originalBranch = null;
let finishInit = false;


function clearAndSet(removeList) {
    if (removeList != null) {
        var remove_list = JSON.parse(removeList);

        $("#dataframe").find("tbody tr").each(function(i, n) {
            $(n).children().eq(0).iCheck('uncheck');
            if (remove_list[$(n).children().eq(1).html()] != undefined) {
                $(n).children().eq(0).iCheck('check');
            }
        });
    }
}

function setSelected(selected) {
    if (selected != null) {
        var list = JSON.parse(selected);
        $("#dataframe").find("tbody tr").each(function(i, n) {
            if (list[$(n).children().eq(1).html()] != undefined) {
                $(n).children().eq(0).iCheck('check');
            }
        });
    }
}

export default {
    data() {
        return {
            selectList: ['model', 'branch', 'target', 'filePath']
        }
    },
    methods: {
        submit() {
            // var remove_list = {};
            var target = $('#target').val();
            var branch = $('#branch').val();
            var model_name = $("#model").val();
            var file_path = $("#filePath").val();
            /**
             * 将被选中的variable添加到removeList中
             */
            // $("#dataframe").find("tbody .checked").each(function (i, n) {
            //     remove_list[$(n).parents("tr").children().eq(1).html()] = i;
            // });
            // $(this).attr("disabled", "disabled");
            $.ajax({
                url: host + "/tool/parse",
                type: 'post',
                data: {
                    // remove_list: JSON.stringify(remove_list),
                    target: target,
                    branch: branch,
                    modelName: model_name,
                    filePath: file_path
                },
                async: true,
                success: function(result) {
                    // $("#branch-commit").removeAttr("disabled");
                    $("#table-content").remove();
                    var tableContent = d3.select("#dataframe").append("div").attr("id", "table-content");
                    var table = tableContent.append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");

                    var data = result.data;
                    var thead = table.append("thead");
                    var tbody = table.append("tbody");
                    var tr = thead.append("tr");

                    /**
                     * 添加头部的select标签栏
                     */
                    branches = data["branches"];
                    // for (let obj of branches) {
                    //     d3.select("#branch").append("option").text(obj)
                    // }
                    d3.select("#model").append("option").text(data["current_model"]);

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
                    $("#target").html("");
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


                    setSelected(data.selected_list);

                    /**
                     * 点击头部按钮可以全选或者反选.
                     */
                    $('#head-checks')
                        .iCheck({
                            checkboxClass: 'icheckbox_square-green hhhh',
                            radioClass: 'iradio_square-green',
                        })
                        .on('ifChecked', function(event) {
                            $(".icheckbox_square-green").iCheck('check');
                        })
                        .on('ifUnchecked', function(event) {
                            $(".icheckbox_square-green").iCheck('uncheck');
                        });
                    // TODO: 控制下一步流程，把不可点按钮置灰
                    finishInit = true;
                },
                error: function() {
                    $(this).removeClass("btn-primary");
                    $(this).addClass("btn-warning")
                }
            });
        }
    },
    mounted() {
        this.selectList.forEach(name => {
            $("#" + name).select2({
                tags: true
            });
            $("#" + name).append(new Option("请选择"))
        })
        // addLabel("#dataframe", "model", false);
        // addLabel("#dataframe", "branch", false);
        // addLabel("#dataframe", "target", false);
        // addLabel("#dataframe", "filePath", true);

        $.ajax({
            url: host + "/tool/init_model_name",
            type: 'get',
            async: true,
            success: function(result) {
                if (result.success) {
                    // d3.select("#model").append("option").text(data["current_model"]);
                    var data = result.data;
                    // $("#model").append(new Option("选择模型", "选择模型", false,false));
                    for (let i in data) {
                        $("#model").append(new Option(data[i], data[i], false, false));
                    }
                    // 选择模型名后的联动效果
                    $("#model").on("select2:select", function(e) {
                        $.ajax({
                            url: host + "/tool/get_branch_name",
                            type: 'get',
                            data: {
                                "modelName": $("#model").val()
                            },
                            async: true,
                            success: function(result) {
                                var data = result.data;
                                // $("#model").append(new Option("选择模型", "选择模型", false,false));
                                for (let i in data) {
                                    $("#branch").append(new Option(data[i], data[i], false, false));
                                }
                            }
                        });
                    })
                }
            }
        });

        $("#branch").on("select2:open", function(e) {
                originalBranch = $(this).val();
            }).on("select2:select", function(e) {
                $.ajax({
                    url: host + "/tool/get_branch_info",
                    type: 'get',
                    data: {
                        "modelName": $("#model").val(),
                        "branch": $("#branch").val(),
                        "originalBranch": originalBranch
                    },
                    async: true,
                    success: function(result) {
                        var files = result.data["files"];
                        var selectedFile = result.data["selected_file"];
                        var target = result.data["target"];
                        // var target = result.data["target"];
                        // $("#model").append(new Option("选择模型", "选择模型", false,false));
                        for (let i in files) {
                            $("#filePath").append(new Option(files[i], files[i]));
                            // $("#target").append(new Option(data[i]["target"], data[i][["target"]]));
                        }

                        $("#filePath").val(selectedFile).trigger("change");
                        if (target != '') {
                            $("#target").append(new Option(target, target, true, true));
                            // currentTarget = target;
                        }
                        // $("#target").val(result.data["target"]).trigger("change")

                    }
                });
            })
            .on("select2:closing", function(e) {
                var current = $(this).val();
                if (branches != null) {
                    for (let obj of branches) {
                        //当该分支已经存在时
                        if (current == obj) {
                            originalBranch = null;
                            $.ajax({
                                url: host + "/tool/db/branch/checkout",
                                data: {
                                    "branch": current,
                                    "model_name": $("#model").val()
                                },
                                type: 'get',
                                async: false,
                                success: function(result) {
                                    clearAndSet(result.data.remove_list);
                                    // $('#target').val(result.data.model_target).trigger("change")
                                }
                            });
                            return;
                        }
                    }
                }
                // branches.push(current);
                // d3.select("#branch").append("option").text(current);
                // $.ajax({
                //     url: host + "/tool/db/branch",
                //     data: {"branch": current, "model_name": $("#model").val(), "original_branch": originalBranch},
                //     type: 'post',
                //     async: true,
                //     success: function (result) {
                //         branches.push(current);
                //         d3.select("#branch").append("option").text(current);
                //     }
                // });
            });

    }
}
</script>

<style lang="css">
.upload {
    position: relative;
    padding-top: 20px;
}
.upload .table-label {
    display: inline-block;
    width: 50px;
    text-align: left;
    margin-right: 10px;
}
.upload .btn {
    position: absolute;
    left: 250px;
    top: 113px;
}
</style>
