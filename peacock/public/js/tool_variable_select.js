/**
 * Created by lifeng on 2017/4/27.
 */
define(['jquery', 'd3', 'i-checks', 'select2'], function ($, d3) {
    /**
     * @param isVariableSelect 是否进入variableSelect环节
     */
    function variableSelect(isVariableSelect) {
        var url;
        var data = {};
        data.target = localStorage.getItem("target");
        data.modelName = localStorage.getItem("model_name");
        data.branch = localStorage.getItem("branch");
        var selected_variable = [];
        //如果进入isVariableSelect环节,将从该环节的页面获取数据
        //否则将从getBar环节获取数据
        if (isVariableSelect) {
            var all_list = [];

            url = host + "/tool/variable_select_manual";
            $(".selected-body.checked").each(function () {
                selected_variable.push($(this).find(".selected-body-checks").attr("name"))
            });
            $(".backup-body.checked").each(function () {
                selected_variable.push($(this).find(".backup-body-checks").attr("name"))
            });
            $(".selected-body").each(function () {
                all_list.push($(this).find(".selected-body-checks").attr("name"))
            });
            $(".backup-body").each(function () {
                all_list.push($(this).find(".backup-body-checks").attr("name"))
            });
            data.selected_list = selected_variable.join(",");
            data.all_list = all_list.join(",");
            data.with_intercept =  $("#withIntercept").val();
            data.ks_group_num = $("#ksGroupNum").val();

        } else {
            $(".variable_apply.checked").each(function () {

                selected_variable.push($(this).find(".apply-checks").attr("name"));
                data.var_list = selected_variable.join(",");
            });
            url = host + "/tool/variable_select";
            data.ks_group_num = $("#ksGroupNum").val();
            data.with_intercept =  $("#withIntercept").val();
        }


        $.ajax({
            url: url,
            type: 'post',
            data: data,
            async: true,
            success: function (result) {
                // adjustTable(result, id, initList, name);
                if (!result.success) {
                    alert("invalid variable select,please change the variable");
                }

                $("#variable_select_content").remove();

                var div =  d3.select("#variableSelect").append("div").attr("class", "ibox-content").attr("id","variable_select_content");
                // var div =  d3.select("#variable_select_content");
                div.append("div").text("模型信息");
                var table = div.append("table").attr("class", "table table-striped");
                var tbody = table.append("tbody");
                var data = result.data;

                var i = 0;
                for (let key in data["model_analysis"]) {
                    var tr;
                    if (i % 2 == 0) {
                        tr = tbody.append("tr");
                    }
                    tr.append("th").text(key);
                    tr.append("td").text(data["model_analysis"][key]);
                    i++;
                }
                //必选项
                div.append("div").text("必选项");
                table = div.append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable")
                    .attr("id", "variableSelected");
                tbody = table.append("tbody").attr("id", "tbody-selected");
                var thead = table.append("thead");
                tr = thead.append("tr");
                var select = tr.append("td");
                //头部的checkbox
                var ss = select.append("div");
                ss.append("input")
                    .attr("type", "checkbox")
                    .attr("class", "selected-head-checks")
                    .attr("name", "input[]");
                tr.append("td").text("name");
                tr.append("td").text("params");
                tr.append("td").text("pvalues");
                tr.append("td").text("bse");
                tr.append("td").text("tvalues");
                tr.append("td").text("conf_int0");
                tr.append("td").text("conf_int1");
                for (let obj in data["selected_var"]) {
                    tr = tbody.append("tr");
                    select = tr.append("td");
                    //body的checkbox
                    var variable = data["selected_var"][obj][0];
                    var index = variable.indexOf("_woe") > 0 ? variable.indexOf("_woe") : variable.length;
                    var name = variable.substring(0, index);
                    select.append("div").append("input")
                        .attr("type", "checkbox")
                        .attr("class", "selected-body-checks")
                        .attr("name", name);
                    tr.append("td").text(name);
                    var columns = data["selected_var"][obj][1].split(",");
                    for (let i in columns) {
                        tr.append("td").text(columns[i]);
                    }
                }


                div.append("div").text("可选项");
                //备选的table
                table = div.append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");
                tbody = table.append("tbody");
                thead = table.append("thead");

                //3th head
                tr = thead.append("tr");
                select = tr.append("td");
                ss = select.append("div");
                //头部的checkbox
                ss.append("input")
                    .attr("type", "checkbox")
                    .attr("class", "backup-head-checks")
                    .attr("name", "input[]");
                tr.append("td").text("name");
                tr.append("td").text("ks_train");
                tr.append("td").text("ks_test");
                tr.append("td").text("pvalues");
                for (let obj in data["marginal_var"]) {
                    tr = tbody.append("tr");
                    select = tr.append("td");
                    //body的checkbox
                    variable = data["marginal_var"][obj][0];
                    name = variable.substring(0, variable.indexOf("_woe"));
                    select.append("div").append("input")
                        .attr("type", "checkbox")
                        .attr("class", "backup-body-checks")
                        .attr("name", name);
                    tr.append("td").text(name);
                    var columns = data["marginal_var"][obj][1].split(",");
                    for (let i in columns) {
                        tr.append("td").text(columns[i]);
                    }
                }


                $('.selected-body-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green selected-body'
                });
                $('.selected-head-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green'
                }).on('ifChecked', function () {
                    $(".selected-body").iCheck('check');
                }).on('ifUnchecked', function () {
                    $('.selected-body').iCheck('uncheck');
                });

                $('.backup-body-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green backup-body'
                });

                $('.backup-head-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green'
                }).on('ifChecked', function () {
                    $(".backup-body").iCheck('check')
                }).on('ifUnchecked', function () {
                    $('.backup-body').iCheck('uncheck');
                });


            }
        });


    }

    function preInit() {
        $("#variableSelect").html("");
        // d3.select("#variableSelect").append()

        var selectArea = d3.select("#variableSelect");
        var head = selectArea.append("div").attr("class", "row wrapper border-bottom white-bg page-heading");
        var content = head.append("div").attr("class", "col-lg-10");
        content.append("h1").text("variable Select");

        var ol = content.append("ol").attr("class", "breadcrumb");
        ol.append("li").append("span").append("a").attr("id", "execute").text("筛选");
        ol.append("li").append("span").append("a").attr("id", "columnConfig").text("column_config");

        var intercept = content.append("div")
            .attr("class", "table-line")
            .style("margin-top", "5px");
        // .attr("class","form-group");
        intercept
            .append("div")
            .style("display", "inline-block")
            .attr("class", "variable-label")
            .append("span")
            .text("withIntercept");
        var interceptSelect = intercept
            .append("div")
            .style("display", "inline-block")
            .style("margin-left", "5px")
            .append("select")
            .attr("class", "variable-select")
            .attr("id", "withIntercept");

        interceptSelect.append("option").text("true");
        interceptSelect.append("option").text("false");
        $("#withIntercept").select2();


        var groupNum = content.append("div")
            .attr("class", "table-line")
            .style("margin-top", "5px");
        // .attr("class","form-group");
        groupNum
            .append("div")
            .style("display", "inline-block")
            .attr("class", "variable-label")
            .append("span")
            .text("ksGroupNum");
        groupNum
            .append("div")
            .style("display", "inline-block")
            .style("margin-left", "5px")
            .append("input")
            .attr("id", "ksGroupNum");

        $("#execute").bind("click", function () {
            if ($(this).val()){
                variableSelect(true);
            }else{
                variableSelect(false);
                $(this).val("0")
            }
        });

        $("#columnConfig").bind("click", function () {
            var params = [];
            $("#variableSelected").find("tbody tr").find("td:eq(2)").each(function () {
                params.push($(this).text())
            });
            var list = [];
            $(".selected-body").each(function () {
                list.push($(this).find(".selected-body-checks").attr("name"))
            });

            $("#columnConfigform").remove();
            var form = $("<form>");//定义一个form表单
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
            $("body").append(form);//将表单放置在web中

            form.submit();//表单提交

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


    return {
        variableSelect: variableSelect,
        preInit: preInit
    }

});